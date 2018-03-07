package name.marinchenko.lorryvision.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.util.debug.ToastHelper;

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

    public static void enableWifi(final Context context,
                                  final boolean toast) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            if (toast) {
                Toast.makeText(
                        context,
                        context.getString(R.string.toast_enabling_wifi),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    public List<ScanResult> getScanResults() {
        this.wifiManager.startScan();
        return this.wifiManager.getScanResults();
    }

    public static class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wifiManager = (WifiManager) context
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);

            switch (wifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_DISABLED:
                    enableWifi(context, false);
                    break;

                case WifiManager.WIFI_STATE_DISABLING:
                    enableWifi(context, false);
                    break;

                default:
                    break;

            }
        }
    }
}
