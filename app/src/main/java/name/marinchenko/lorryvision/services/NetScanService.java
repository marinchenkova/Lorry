package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.Notificator;
import name.marinchenko.lorryvision.util.net.Net;
import name.marinchenko.lorryvision.util.net.NetBuffer;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;

import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECTED;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECTING;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECT_AUTO;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_DISCONNECTED;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_AUTO_CONNECT;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_CONNECTED;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_SSID;
import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_LEVEL;
import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_TIME;


/**
 * Service for scanning Wi-Fi networks.
 */

public class NetScanService extends Service {

    public final static int MSG_SCANS = 0;
    public final static int MSG_LORRIES_DETECTED = 1;
    public final static int MSG_СONNECT_START = 2;

    public final static String MESSENGER_MAIN_ACTIVITY = "messenger_main_activity";

    public final static String ACTION_SCAN_SINGLE = "scan_single";
    public final static String ACTION_SCAN_START = "scan_start";
    public final static String ACTION_SCAN_STOP = "scan_stop";

    private final static int SCAN_PERIOD_MS = 1000;

    private Timer scanTimer;
    private NetBuffer netBuffer = new NetBuffer();

    private Messenger mActivityMessenger;
    private WifiAgent wifiAgent;

    private boolean scanning = false;
    private boolean autoConnect = false;
    private boolean connecting = false;
    private boolean connected = false;
    private boolean lorriesNear = false;
    private String connectingNetSsid;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.wifiAgent = new WifiAgent(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Messenger activityMessenger = intent.getParcelableExtra(MESSENGER_MAIN_ACTIVITY);
        if (activityMessenger != null) this.mActivityMessenger = activityMessenger;

        switch (intent.getAction() == null ? "" : intent.getAction()) {
            case ACTION_SCAN_SINGLE:
                singleScan();
                break;

            case ACTION_SCAN_START:
                startScan(0);
                break;

            case ACTION_SCAN_STOP:
                stopScan();
                removeScanResults();
                break;

            case ACTION_CONNECTING:
                this.connectingNetSsid = intent.getStringExtra(EXTRA_SSID);
                break;

            case ACTION_CONNECTED:
                this.connecting = false;
                this.connected = true;
                break;

            case ACTION_DISCONNECTED:
                this.connected = false;
                break;

            case ACTION_CONNECT_AUTO:
                this.autoConnect = intent.getBooleanExtra(EXTRA_AUTO_CONNECT, true);
                break;
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopScan();
        super.onDestroy();
    }


    private void sendMessage(final Messenger target,
                             final Message msg) {
        if (target != null) {
            try {
                target.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAndSendScanResults() {
        final List<Net> nets = this.netBuffer.getNets(
                wifiAgent.getScanResults(),
                this
        );
        final Message msg = new Message();
        msg.what = MSG_SCANS;
        msg.obj = nets;

        final List<Net> lorries = this.netBuffer.getLorries();
        if (lorries.size() > 0 ) {
            if (!this.scanning) startScan(SCAN_PERIOD_MS);
            if (!this.connected) lorriesNear(lorries);
            this.lorriesNear = true;
            msg.arg1 = MSG_LORRIES_DETECTED;
        } else {
            msg.arg1 = -1;
            this.lorriesNear = false;
        }

        sendMessage(mActivityMessenger, msg);
    }

    private void removeScanResults() {
        this.netBuffer.removeAll();
    }

    private void singleScan() {
        DefaultExecutorSupplier.getInstance().forLightWeightBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        updateAndSendScanResults();
                    }
                }
        );
    }

    private void startScan(final int delayMs) {
        if (!this.scanning) {
            stopScan();
            this.scanTimer = new Timer();
            this.scanTimer.schedule(new ScanTimerTask(), delayMs, SCAN_PERIOD_MS);
            this.scanning = true;
        }
    }

    private void stopScan() {
        if (!this.lorriesNear && this.scanTimer != null) {
            this.scanTimer.cancel();
            this.scanTimer = null;
            this.scanning = false;
        }
    }

    private void lorriesNear(final List<Net> lorries) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        setConnectingNet(lorries);

                        if (!WifiAgent.connectedTo(getApplicationContext(),
                                WifiConfig.formatSsid(connectingNetSsid))) {
                            for (Net net : lorries) {
                                if (net.getSsid().equals(connectingNetSsid)) {
                                    if (connect(net)) {
                                        connecting = true;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
        );
     }

    private void setConnectingNet(final List<Net> lorries) {
        if (this.connectingNetSsid == null && !this.connecting) {
            for (Net net : lorries) {
                if (this.autoConnect) {
                    this.connectingNetSsid = net.getSsid();
                    break;
                }
            }
        }
    }

    private boolean connect(final Net net) {
        if (net.getLastTimeMeanLevel(STABLE_CONNECT_TIME) > STABLE_CONNECT_LEVEL
                && !this.connecting) {
            startConnectService(net);

            Notificator.notifyNetDetected(this);

            final Message msg = new Message();
            msg.what = MSG_СONNECT_START;
            sendMessage(this.mActivityMessenger, msg);

            this.connectingNetSsid = null;
            return true;
        } else return false;
    }

    private void startConnectService(final Net net) {
        final Intent connectService = new Intent(this, ConnectService.class);
        connectService.setAction(ConnectService.ACTION_CONNECTING);
        connectService.putStringArrayListExtra(
                ConnectService.EXTRA_CONFIG,
                net.wrapConfig().asArrayList()
        );
        startService(connectService);
    }

    private class ScanTimerTask extends TimerTask {
        @Override
        public void run() {
            updateAndSendScanResults();
        }
    }
}
