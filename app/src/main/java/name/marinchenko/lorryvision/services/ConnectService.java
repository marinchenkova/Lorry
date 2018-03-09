package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import name.marinchenko.lorryvision.activities.main.MainActivity;
import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.threading.ToastThread;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends IntentService {

    private final static String CONSTRUCTOR = "connect_service";

    public final static String ACTION_CONNECTING = "action_connecting";
    public final static String ACTION_CONNECTED = "action_connected";
    public final static String MESSENGER_NET_SCAN_SERVICE = "messenger_net_scan_service";
    public final static String KEY_CONFIG = "key_config";
    public final static String EXTRA_SSID = "extra_ssid";
    public final static int STABLE_CONNECT_TIME = 5;
    public final static int STABLE_CONNECT_LEVEL = -80;


    private Messenger mNetScanServiceMessenger;
    private boolean connecting = false;

    public ConnectService() { super(CONSTRUCTOR); }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final Messenger messenger = intent.getParcelableExtra(MESSENGER_NET_SCAN_SERVICE);
            if (messenger != null) this.mNetScanServiceMessenger = messenger;

            final String action = intent.getAction() == null ? "" : intent.getAction();
            switch (action) {
                case ACTION_CONNECTING:
                    connect(intent.getStringArrayListExtra(KEY_CONFIG));
                    break;

                case ACTION_CONNECTED:
                    this.connecting = false;
                    ToastThread.postToastMessage(
                            this,
                            "Connected",
                            Toast.LENGTH_SHORT
                    );
                    break;
            }
        }
    }

    private void connect(final ArrayList<String> configList) {
        if (!this.connecting && !WifiAgent.connected(this) && configList != null) {
            this.connecting = true;
            final WifiConfiguration config = (new NetConfig(configList)).getWifiConfiguration();
            WifiAgent.connect(this, config);

            getToMainActivity(config.SSID);
        }
    }

    private void getToMainActivity(final String netSsid) {
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
