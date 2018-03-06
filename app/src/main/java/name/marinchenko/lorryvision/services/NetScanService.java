package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.net.WifiAgent;

/**
 * Service for scanning Wi-Fi networks.
 */

public class NetScanService extends Service {

    public final static int MSG_SCAN_SINGLE = 1;
    public final static int MSG_SCAN_START = 2;
    public final static int MSG_SCAN_STOP = 3;
    public final static int MSG_SCANS = 4;

    public final static String MESSENGER = "messenger";
    public final static String ACTION_SCAN_START = "scan_start";
    public final static String ACTION_SCAN_STOP = "scan_stop";

    private final static int SCAN_PERIOD_MS = 1000;


    private List<ScanResult> scanResults = new ArrayList<>();
    private Timer scanTimer;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Messenger mServiceMessenger;
    private Messenger mActivityMessenger;
    private WifiAgent wifiAgent;


    @Override
    public IBinder onBind(Intent intent) {
        this.mActivityMessenger = intent.getParcelableExtra(MESSENGER);
        return this.mServiceMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(
                "ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND
        );
        thread.start();

        this.mServiceLooper = thread.getLooper();
        this.mServiceHandler = new ServiceHandler(mServiceLooper);
        this.mServiceMessenger = new Messenger(mServiceHandler);
        this.wifiAgent = new WifiAgent(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction() == null ? "" : intent.getAction();
        switch (action) {
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

    private void sendMessage(final Message msg) {
        try {
            this.mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateAndSendScanResults() {
        //this.scanResults = this.wifiAgent.getScanResults();
        postToastMessage("Sending...");
        Message msg = new Message();
        msg.what = MSG_SCANS;
        //msg.obj = this.scanResults;

        sendMessage(msg);
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

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class ScanTimerTask extends TimerTask {
        @Override
        public void run() {
            updateAndSendScanResults();
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(final Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_SCAN_SINGLE:
                    updateAndSendScanResults();
                    break;

                case MSG_SCAN_START:
                    startScan();
                    break;

                case MSG_SCAN_STOP:
                    stopScan();
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
