package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.ToastThread;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends IntentService {

    private final static String CONSTRUCTOR = "connect_service";

    public final static String ACTION_CONNECTING = "action_connecting";
    public final static String ACTION_CONNECTED = "action_connected";
    public final static String ACTION_CONNECT_AUTO = "action_connect_auto";

    public final static String EXTRA_CONFIG = "extra_config";
    public final static String EXTRA_SSID = "extra_ssid";
    public final static String EXTRA_AUTO_CONNECT = "extra_auto_connect";

    public final static int STABLE_CONNECT_TIME = 5;
    public final static int STABLE_CONNECT_LEVEL = -80;


    private boolean connecting = false;

    public ConnectService() { super(CONSTRUCTOR); }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction() == null ? "" : intent.getAction();
            switch (action) {
                case ACTION_CONNECTING:
                    connect(intent.getStringArrayListExtra(EXTRA_CONFIG));
                    break;

                case ACTION_CONNECTED:
                    this.connecting = false;
                    break;
            }
        }
    }

    private void connect(final ArrayList<String> configList) {
        final NetConfig netConfig = new NetConfig(configList);
        if (!this.connecting
                && configList != null
                && !WifiAgent.connected(this, WifiConfig.formatSsid(netConfig.getSsid()))) {

            this.connecting = true;
            final WifiConfiguration wifiConfig = netConfig.getWifiConfiguration();
            WifiAgent.connect(this, wifiConfig);

            toVideoActivity(netConfig.getSsid());
        }
    }

    private void toVideoActivity(final String netSsid) {
        /*
        final Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.setAction(ACTION_CONNECTING);
        mainActivity.putExtra(EXTRA_SSID, netSsid);
        startActivity(mainActivity);
        */

        ToastThread.postToastMessage(
                this,
                "Connecting to " + netSsid,
                Toast.LENGTH_SHORT
        );
    }

}
