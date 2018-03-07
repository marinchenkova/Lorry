package name.marinchenko.lorryvision.util.net;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import name.marinchenko.lorryvision.R;

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

    public static void enableWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Toast.makeText(
                    context,
                    context.getString(R.string.toast_enabling_wifi),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public List<ScanResult> getScanResults() {
        this.wifiManager.startScan();
        return this.wifiManager.getScanResults();
    }

}
