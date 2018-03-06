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
import android.widget.Toast;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Service for scanning Wi-Fi networks.
 */

public class NetScanService extends Service {

    public final static int MSG_SCAN_SINGLE = 1;
    public final static int MSG_SCAN_START = 2;
    public final static int MSG_SCAN_STOP = 3;

    private final static int SCAN_PERIOD_MS = 1000;


    private HashSet<ScanResult> scanResults = new HashSet<>();
    private Timer scanTimer;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Messenger mMessenger;


    @Override
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
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
        this.mMessenger = new Messenger(mServiceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Message msg = this.mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        this.mServiceHandler.sendMessage(msg);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopScan();
        super.onDestroy();
    }

    private void updateAndSendScanResults() {
        postToastMessage("Scans are sent");
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
                    startScan();
                    break;
            }
        }
    }
}
