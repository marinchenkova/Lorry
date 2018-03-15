package name.marinchenko.lorryvision.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.services.ConnectService;
import name.marinchenko.lorryvision.services.NetScanService;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static name.marinchenko.lorryvision.services.ConnectService.ACTION_WIFIAGENT_DISCONNECT;

/**
 * Class with Wi-Fi operations methods.
 */

public class WifiAgent {

    private final WifiManager wifiManager;

    public WifiAgent(Context context) {
        this.wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }


    public static void enableWifi(final Context context,
                                  final boolean toast,
                                  final boolean force) {
        DefaultExecutorSupplier.getInstance().forLightWeightBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        WifiManager wifiManager = (WifiManager) context
                                .getApplicationContext()
                                .getSystemService(Context.WIFI_SERVICE);

                        final Timer timer = new Timer();
                        final TimerTask enableWifiTimerTask = new EnableWifiTimerTask(
                                wifiManager,
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

    public static int connect(final Context context,
                               final WifiConfiguration config) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            final int lastId = wifiManager.addNetwork(config);
            wifiManager.disconnect();
            wifiManager.enableNetwork(lastId, true);
            return lastId;
        }
        return -1;
    }

    public static void disconnect(final Context context,
                                  final int lastId) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null && lastId != -1) {
            wifiManager.disconnect();
            wifiManager.removeNetwork(lastId);
            wifiManager.reconnect();
        }
    }

    public static boolean connectedTo(final Context context,
                                      final String ssid) {
        final WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = wifiManager != null ? wifiManager.getConnectionInfo() : null;
        final SupplicantState state = info != null ? info.getSupplicantState() : null;

        return state != null
                && state == SupplicantState.COMPLETED
                && (ssid == null || ssid.equals(info.getSSID()));
    }

    public static void notifyDisconnected(final Context context) {
        final Intent disconnect1 = new Intent(context, NetScanService.class);
        disconnect1.setAction(ACTION_WIFIAGENT_DISCONNECT);
        context.startService(disconnect1);

        final Intent disconnect2 = new Intent(context, ConnectService.class);
        disconnect2.setAction(ACTION_WIFIAGENT_DISCONNECT);
        context.startService(disconnect2);
    }

    private static void notifyConnected(final Context context) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (connectedTo(context, null)) {
                            final Intent connected = new Intent(context, ConnectService.class);
                            connected.setAction(ConnectService.ACTION_WIFIAGENT_CONNECTED);
                            context.startService(connected);
                        } else {
                            final Intent disconnected = new Intent(context, NetScanService.class);
                            disconnected.setAction(ConnectService.ACTION_WIFIAGENT_DISCONNECT);
                            context.startService(disconnected);
                        }
                    }
                }
        );
    }

    private static class EnableWifiTimerTask extends TimerTask {
        public final static int CNT_MAX = 3;
        public final static int PERIOD = 500;

        private final WifiManager wifiManager;
        private final Context context;
        private final boolean toast;
        private final boolean force;
        private int cnt = 0;

        public EnableWifiTimerTask(final WifiManager wifiManager,
                                   final Context context,
                                   final boolean toast,
                                   final boolean force) {
            this.wifiManager = wifiManager;
            this.context = context;
            this.toast = toast;
            this.force = force;
        }
        @Override
        public void run() {
            if (cnt > CNT_MAX) this.cancel();

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
            enableWifi(context, false, true);
            notifyConnected(context);
        }
    }
}
