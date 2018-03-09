package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.ToastThread;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends IntentService {

    private final static String CONSTRUCTOR = "connect_service";
    private final static int PERIOD_TRY_CONNECTED = 500;
    private final static int CNT_TRY_CONNECTED = 6;


    public final static String ACTION_CONNECTING = "action_connecting";
    public final static String ACTION_CONNECTED = "action_connected";
    public final static String ACTION_CONNECT_AUTO = "action_connect_auto";

    public final static String EXTRA_CONFIG = "extra_config";
    public final static String EXTRA_SSID = "extra_ssid";
    public final static String EXTRA_AUTO_CONNECT = "extra_auto_connect";
    public final static String EXTRA_CONNECTED = "extra_connected";

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

            final Timer timer = new Timer();
            final TimerTask videoTask = new VideoTask(timer, netConfig.getSsid());
            timer.schedule(videoTask, 0, PERIOD_TRY_CONNECTED);
        }
    }

    private void toVideoActivity(final String netSsid) {
        ToastThread.postToastMessage(
                this,
                "Connected to " + netSsid,
                Toast.LENGTH_SHORT
        );
    }


    private class VideoTask extends TimerTask {
        private final String ssid;
        private Timer timer;
        private int cnt = CNT_TRY_CONNECTED;

        public VideoTask(final Timer timer,
                         final String ssid) {
            this.ssid = ssid;
            this.timer = timer;
        }

        @Override
        public void run() {
            if (cnt > 0) {
                if (WifiAgent.connected(getApplicationContext(), WifiConfig.formatSsid(ssid))) {
                    toVideoActivity(ssid);
                    timer.cancel();
                    timer = null;
                }
            } else {
                timer.cancel();
                timer = null;
            }
            cnt--;
        }
    }
}
