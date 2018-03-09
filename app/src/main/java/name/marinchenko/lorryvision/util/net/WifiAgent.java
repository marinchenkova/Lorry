package name.marinchenko.lorryvision.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.services.ConnectService;
import name.marinchenko.lorryvision.util.threading.ToastThread;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;

/**
 * Class with Wi-Fi operations methods.
 */

public class WifiAgent {

    private final Context context;
    private final WifiManager wifiManager;

    public WifiAgent(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public static void connect(final Context context,
                               final WifiConfiguration config) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            final int lastId = wifiManager.addNetwork(config);

            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.enableNetwork(lastId, true);
            wifiManager.reconnect();
        }
    }

    public static void enableWifi(final Context context,
                                  final boolean toast,
                                  final boolean force) {
        DefaultExecutorSupplier.getInstance().forLightWeightBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final Timer timer = new Timer();
                        final TimerTask enableWifiTimerTask = new EnableWifiTimerTask(
                                timer,
                                context,
                                toast,
                                force
                        );
                        timer.schedule(enableWifiTimerTask, 0, EnableWifiTimerTask.PERIOD);
                    }
                }
        );
    }

    public List<ScanResult> getScanResults() {
        this.wifiManager.startScan();
        return this.wifiManager.getScanResults();
    }

    private static void startConnected(final Context context) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (connected(context)) {
                            final Intent connected = new Intent(context, ConnectService.class);
                            connected.setAction(ConnectService.ACTION_CONNECTED);
                            context.startService(connected);
                        }
                    }
                }
        );
    }

    public static boolean connected(final Context context) {
        final WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        final SupplicantState state = wifiManager != null ? wifiManager
                .getConnectionInfo()
                .getSupplicantState() : null;

        return state != null && state == SupplicantState.COMPLETED;
    }

    public static class EnableWifiTimerTask extends TimerTask {
        public final static int CNT_MAX = 3;
        public final static int PERIOD = 500;

        private Timer timer;
        private final Context context;
        private final boolean toast;
        private final boolean force;
        private int cnt = 0;

        public EnableWifiTimerTask(final Timer timer,
                                   final Context context,
                                   final boolean toast,
                                   final boolean force) {
            this.timer = timer;
            this.context = context;
            this.toast = toast;
            this.force = force;
        }
        @Override
        public void run() {
            if (cnt > CNT_MAX && timer != null) {
                timer.cancel();
                timer = null;
            }
            WifiManager wifiManager = (WifiManager) context
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);

            if (wifiManager != null && (force || !wifiManager.isWifiEnabled())) {
                wifiManager.setWifiEnabled(true);
                if (toast && cnt == 0) {
                    ToastThread.postToastMessage(
                            context,
                            context.getString(R.string.toast_enabling_wifi),
                            Toast.LENGTH_LONG
                    );
                }
            }
            cnt++;
        }
    }

    public static class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            enableWifi(
                    context,
                    false,
                    true
            );
            startConnected(context);
        }
    }
}
