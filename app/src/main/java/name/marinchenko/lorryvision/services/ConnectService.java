package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends Service {

    public final static String ACTION_CONNECT_MANUAL = "action_connect_manual";
    public final static String ACTION_CONNECT_AUTO = "action_connect_auto";

    public final static String ACTION_WIFIAGENT_CONNECT = "action_wifiagent_connect";
    public final static String ACTION_WIFIAGENT_CONNECTED_TO = "action_wifiagent_connected_to";
    public final static String ACTION_WIFIAGENT_CONNECTED = "action_wifiagent_connected";
    public final static String ACTION_WIFIAGENT_DISCONNECT = "action_wifiagent_disconnect";

    public final static String EXTRA_NET_CONFIG = "extra_net_config";
    public final static String EXTRA_NET_SSID = "extra_net_ssid";
    public final static String EXTRA_CONNECT_AUTO = "extra_connect_auto";

    public final static int STABLE_CONNECT_TIME_S = 5;
    public final static int STABLE_CONNECT_LEVEL_DB = -80;

    private final static int CNT_CHECK_CONNECTED = 20;
    private final static int PERIOD_CHECK_CONNECTED_MS = 500;
    private final static int PERIOD_CHECK_DISCONNECTED_MS = 2000;

    private boolean connecting = false;
    private int currentConnectedNetId = -1;
    private Timer stabilityTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (intent != null) process(intent);
                    }
                }
        );

        return START_STICKY;
    }

    private void process(@NonNull final Intent intent) {
        final String action = intent.getAction() == null ? "" : intent.getAction();
        switch (action) {
            case ACTION_WIFIAGENT_CONNECT:
                connect(intent.getStringArrayListExtra(EXTRA_NET_CONFIG));
                break;

            case ACTION_WIFIAGENT_CONNECTED:
                this.connecting = false;
                break;

            case ACTION_WIFIAGENT_DISCONNECT:
                disconnect();
                break;
        }
    }

    private void connect(final ArrayList<String> configList) {
        final NetConfig netConfig = new NetConfig(configList);
        if (!this.connecting && configList != null && !WifiAgent.connectedTo(
                getApplicationContext(),
                WifiConfig.formatSsid(netConfig.getSsid())
        )) {
            this.connecting = true;
            final WifiConfiguration wifiConfig = netConfig.getWifiConfiguration();

            this.currentConnectedNetId = WifiAgent.connect(
                    getApplicationContext(),
                    wifiConfig
            );

            final Timer timer = new Timer();
            final TimerTask checkConnectionTask =
                    new CheckConnectionTask(netConfig.getSsid());
            timer.schedule(checkConnectionTask, 0, PERIOD_CHECK_CONNECTED_MS);
        }
    }

    private void disconnect() {
        WifiAgent.disconnect(getApplicationContext(), this.currentConnectedNetId);
        this.connecting = false;
        this.currentConnectedNetId = -1;
        if (this.stabilityTimer != null) {
            this.stabilityTimer.cancel();
            this.stabilityTimer = null;
        }
        stopSelf();
    }

    private void connected(final String ssid) {
        sendConnectionState(ACTION_WIFIAGENT_CONNECTED_TO);
        this.stabilityTimer = new Timer();
        final TimerTask stabilityTask = new StabilityTask(ssid);
        this.stabilityTimer.schedule(stabilityTask, 0, PERIOD_CHECK_DISCONNECTED_MS);
    }

    private void sendConnectionState(final String action) {
        final Intent connected = new Intent(this, NetScanService.class);
        connected.setAction(action);
        startService(connected);
    }


    private class CheckConnectionTask extends TimerTask {
        private final String ssid;
        private int cnt = 0;

        public CheckConnectionTask(final String ssid) {
            this.ssid = ssid;
        }

        @Override
        public void run() {
            if (cnt < CNT_CHECK_CONNECTED) {
                if (WifiAgent.connectedTo(getApplicationContext(), WifiConfig.formatSsid(ssid))) {
                    connected(ssid);
                    this.cancel();
                }
            }
            else this.cancel();

            cnt++;
        }
    }

    private class StabilityTask extends TimerTask {
        private final String ssid;

        public StabilityTask(final String ssid) {
            this.ssid = ssid;
        }

        @Override
        public void run() {
            if (!WifiAgent.connectedTo(getApplicationContext(), WifiConfig.formatSsid(ssid))) {
                sendConnectionState(ACTION_WIFIAGENT_DISCONNECT);
            }
        }
    }
}
