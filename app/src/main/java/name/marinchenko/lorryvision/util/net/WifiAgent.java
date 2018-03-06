package name.marinchenko.lorryvision.util.net;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;
import java.util.Set;

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
        if (wifiManager != null) wifiManager.setWifiEnabled(true);
    }

    public List<ScanResult> getScanResults() {
        this.wifiManager.startScan();
        return this.wifiManager.getScanResults();
    }

}
