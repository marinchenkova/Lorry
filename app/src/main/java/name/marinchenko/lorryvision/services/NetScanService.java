package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableFuture;

import name.marinchenko.lorryvision.util.net.Net;
import name.marinchenko.lorryvision.util.net.ScanResultParser;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;
import name.marinchenko.lorryvision.util.threading.ToastThread;

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
        if (this.scanTimer == null) {
            this.scanTimer = new Timer();
            this.scanTimer.schedule(new ScanTimerTask(), 0, SCAN_PERIOD_MS);
        }
    }

    private void stopScan() {
        if (this.scanTimer != null) {
            this.scanTimer.cancel();
            this.scanTimer = null;
        }
    }

    private void lorriesNear(final List<Net> lorries) {
        if (lorries.size() > 0) {
            startScan();
        }
    }


    private class ScanTimerTask extends TimerTask {
        @Override
        public void run() {
            updateAndSendScanResults();
        }
    }
}
