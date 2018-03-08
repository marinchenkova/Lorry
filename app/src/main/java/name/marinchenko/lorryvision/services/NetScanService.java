package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.net.Net;
import name.marinchenko.lorryvision.util.net.ScanResultParser;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;

import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_LEVEL;
import static name.marinchenko.lorryvision.services.ConnectService.STABLE_CONNECT_TIME;


/**
 * Service for scanning Wi-Fi networks.
 */

public class NetScanService extends Service {

    public final static int MSG_SCANS = 0;
    public final static int MSG_LORRIES_DETECTED = 1;

    public final static String MESSENGER_MAIN_ACTIVITY = "messenger_main_activity";

    public final static String ACTION_SCAN_SINGLE = "scan_single";
    public final static String ACTION_SCAN_START = "scan_start";
    public final static String ACTION_SCAN_STOP = "scan_stop";

    private final static int SCAN_PERIOD_MS = 1000;

    private Timer scanTimer;
    private ScanResultParser scanResultParser = new ScanResultParser();

    private Messenger mActivityMessenger;
    private WifiAgent wifiAgent;

    private boolean lorriesNear = false;


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
                startScan();
                break;

            case ACTION_SCAN_STOP:
                stopScan();
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
        final List<Net> nets = this.scanResultParser.getNets(
                this,
                wifiAgent.getScanResults()
        );

        lorriesNear(this.scanResultParser.getLorries());

        final Message msg = new Message();
        msg.what = MSG_SCANS;
        msg.obj = nets;
        sendMessage(mActivityMessenger, msg);
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

    private void startScan() {
        stopScan();
        this.scanTimer = new Timer();
        this.scanTimer.schedule(new ScanTimerTask(), 0, SCAN_PERIOD_MS);
    }

    private void stopScan() {
        if (this.scanTimer != null) {
            this.scanTimer.cancel();
            this.scanTimer = null;
        }
    }

    private void lorriesNear(final List<Net> lorries) {
        if (lorries.size() > 0) {
            this.lorriesNear = true;
            startScan();
            for (Net net : lorries) {
                //TODO priority by earlier detection time
                if (net.getLastTimeMeanLevel(STABLE_CONNECT_TIME) > STABLE_CONNECT_LEVEL) {
                    connect(net.wrap());
                    return;
                }
            }
        } else this.lorriesNear = false;
    }

    private void connect(ArrayList<String> config) {
        final Intent connectService = new Intent(this, ConnectService.class);
        connectService.putStringArrayListExtra(ConnectService.KEY_CONFIG, config);
        startService(connectService);
    }


    private class ScanTimerTask extends TimerTask {
        @Override
        public void run() {
            updateAndSendScanResults();
        }
    }
}
