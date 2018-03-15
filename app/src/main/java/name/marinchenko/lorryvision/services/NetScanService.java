package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.Initializer;
import name.marinchenko.lorryvision.util.Notificator;
import name.marinchenko.lorryvision.util.net.Net;
import name.marinchenko.lorryvision.util.net.NetBuffer;
import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECT_AUTO;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECT_MANUAL;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_WIFIAGENT_CONNECTED_TO;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_WIFIAGENT_DISCONNECT;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_CONNECT_AUTO;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_NET_SSID;
import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_LEVEL_DB;
import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_TIME_S;


/**
 * Service for scanning Wi-Fi networks.
 */

public class NetScanService extends Service {

    public final static int MSG_SCANS = 0;
    public final static int MSG_LORRIES_DETECTED = 1;
    public final static int MSG_СONNECT_START = 2;
    public final static int MSG_СONNECT_END = 3;
    public final static int MSG_RETURN_TO_MAIN = 4;

    public final static String MESSENGER = "messenger_main_activity";

    public final static String ACTION_SCAN_SINGLE = "action_scan_single";
    public final static String ACTION_SCAN_START = "action_scan_start";
    public final static String ACTION_SCAN_STOP = "action_scan_stop";
    public final static String ACTION_UNREGISTER_MESSENGER = "action_unregister_messenger";

    private final static int SCAN_PERIOD_MS = 1000;

    private Timer scanTimer;
    private NetBuffer netBuffer = new NetBuffer();

    private Messenger mActivityMessenger;
    private WifiAgent wifiAgent;

    private boolean scanning = false;
    private boolean autoConnect = false;
    private boolean lorriesNear = false;
    private boolean manualConnectActivated = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() { this.wifiAgent = new WifiAgent(this); }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (intent != null) process(intent);
                    }
                }
        );

        return START_STICKY;
    }

    private void process(@NonNull final Intent intent) {
        final Messenger activityMessenger = intent.getParcelableExtra(MESSENGER);
        if (activityMessenger != null) this.mActivityMessenger = activityMessenger;

        switch (intent.getAction() == null ? "" : intent.getAction()) {
            case ACTION_SCAN_SINGLE:
                updateAndSendScanResults();
                break;

            case ACTION_SCAN_START:
                startScan(0);
                break;

            case ACTION_SCAN_STOP:
                stopScan();
                break;

            case ACTION_CONNECT_AUTO:
                this.autoConnect = intent.getBooleanExtra(EXTRA_CONNECT_AUTO, true);
                if (!this.autoConnect) this.netBuffer.setConnectingNet(null, true);
                break;

            case ACTION_CONNECT_MANUAL:
                this.manualConnectActivated = true;
                this.netBuffer.setConnectingNet(intent.getStringExtra(EXTRA_NET_SSID), false);
                break;

            case ACTION_WIFIAGENT_CONNECTED_TO:
                this.netBuffer.connect(this.autoConnect);
                sendMsgСonnectEnd();
                break;

            case ACTION_WIFIAGENT_DISCONNECT:
                this.netBuffer.detach();
                /*
                ToastThread.postToastMessage(
                        this,
                        "Nets detached:" + String.valueOf(),
                        Toast.LENGTH_SHORT
                );
                */
                break;

            case ACTION_UNREGISTER_MESSENGER:
                this.mActivityMessenger = null;
                break;
        }
    }

    @Override
    public void onDestroy() {
        stopScan();
        super.onDestroy();
    }

    public boolean isMessengerNull() {
        return this.mActivityMessenger == null;
    }

    public void sendMessage(final Message msg) {
        if (this.mActivityMessenger != null) {
            try {
                this.mActivityMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsgConnectStart(final String ssid) {
        Notificator.jumpToMainActivity(this);

        final Message msg = new Message();
        msg.what = MSG_СONNECT_START;
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NET_SSID, ssid);

        msg.setData(bundle);
        sendMessage(msg);
    }

    private void sendMsgСonnectEnd() {
        final Message msg = new Message();
        msg.what = MSG_СONNECT_END;
        sendMessage(msg);
    }

    private void updateAndSendScanResults() {
        final List<Net> nets = this.netBuffer.getNets(wifiAgent.getScanResults(), this);

        final Message msg = new Message();
        msg.what = MSG_SCANS;
        msg.obj = nets;

        if (this.netBuffer.lorriesNear()) {
            if (this.netBuffer.lorriesChanged()) Notificator.notifyNetDetected(this);
            startScan(SCAN_PERIOD_MS);
            lorriesNear();
            this.lorriesNear = true;
            msg.arg1 = MSG_LORRIES_DETECTED;
        }
        else {
            Initializer.initNetScanService(this);
            Notificator.removeNetDetectedNotification(this);
            msg.arg1 = -1;
            this.lorriesNear = false;
        }

        sendMessage(msg);
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

    private void lorriesNear() {
        if (this.autoConnect) {
            this.netBuffer.setConnectingNet(this.netBuffer.getPrefferedNetSsid(), true);
        }

        final Net net = this.netBuffer.getConnectingNet(
                this.manualConnectActivated
        );

        if (canConnect(net)) connect(net);
    }

    private boolean canConnect(final Net net) {
        return net != null

                && this.mActivityMessenger != null

                && net.getLastTimeMeanLevel(STABLE_CONNECT_TIME_S) > STABLE_CONNECT_LEVEL_DB

                && !WifiAgent.connectedTo(this,
                WifiConfig.formatSsid(this.netBuffer.getConnectingNetSsid(
                        this.manualConnectActivated
                )))

                && !WifiAgent.connectedTo(this,
                WifiConfig.formatSsid(this.netBuffer.getConnectedNetSsid()));
    }

    private void connect(@NonNull final Net net) {
        this.manualConnectActivated = false;

        startConnectService(net);
        sendMsgConnectStart(net.getSsid());
        Notificator.removeNetDetectedNotification(this);
    }

    private void startConnectService(final NetConfig config) {
        final Intent connectService = new Intent(this, ConnectService.class);

        connectService.setAction(ConnectService.ACTION_WIFIAGENT_CONNECT);
        connectService.putStringArrayListExtra(
                ConnectService.EXTRA_NET_CONFIG,
                config.asArrayList()
        );

        startService(connectService);
    }


    private class ScanTimerTask extends TimerTask {
        @Override
        public void run() {
            WifiAgent.enableWifi(getApplicationContext(), false, true);
            updateAndSendScanResults();
        }
    }
}
