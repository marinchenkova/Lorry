package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.util.net.NetConfig;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.net.WifiConfig;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;
import name.marinchenko.lorryvision.util.threading.ToastThread;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends Service {

    private final static int PERIOD_TRY_CONNECTED = 500;
    private final static int PERIOD_TRY_DISCONNECTED = 2000;
    private final static int CNT_TRY_CONNECTED = 20;

    public final static String ACTION_CONNECTING = "action_connecting";
    public final static String ACTION_CONNECTED = "action_connected";
    public final static String ACTION_DISCONNECTED = "action_disconnected";
    public final static String ACTION_CANCEL = "action_cancel";
    public final static String ACTION_CONNECT_AUTO = "action_connect_auto";

    public final static String EXTRA_CONFIG = "extra_config";
    public final static String EXTRA_SSID = "extra_ssid";
    public final static String EXTRA_AUTO_CONNECT = "extra_auto_connect";

    public final static int STABLE_CONNECT_TIME = 5;
    public final static int STABLE_CONNECT_LEVEL = -80;

    private boolean connecting = false;
    private int currentConnectedNetId = -1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction() == null ? "" : intent.getAction();
            switch (action) {
                case ACTION_CONNECTING:
                    connect(intent.getStringArrayListExtra(EXTRA_CONFIG));
                    break;

                case ACTION_CONNECTED:
                    this.connecting = false;
                    break;

                case ACTION_CANCEL:
                    disconnect();
                    break;
            }
        }

        return START_STICKY;
    }

    private void connect(final ArrayList<String> configList) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final NetConfig netConfig = new NetConfig(configList);
                        if (!connecting && configList != null && !WifiAgent.connectedTo(
                                getApplicationContext(),
                                WifiConfig.formatSsid(netConfig.getSsid())
                        )) {

                            connecting = true;
                            final WifiConfiguration wifiConfig = netConfig.getWifiConfiguration();

                            currentConnectedNetId = WifiAgent.connect(
                                    getApplicationContext(),
                                    wifiConfig
                            );

                            final Timer timer = new Timer();
                            final TimerTask checkConnectionTask =
                                    new CheckConnectionTask(netConfig.getSsid());
                            timer.schedule(checkConnectionTask, 0, PERIOD_TRY_CONNECTED);
                        }

                    }
                }
        );
    }

    private void disconnect() {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        WifiAgent.disconnect(getApplicationContext(), currentConnectedNetId);
                    }
                }
        );
        stopSelf();
    }

    private void connected(final String ssid) {
        sendConnectionState(ACTION_CONNECTED);
        final Timer timer = new Timer();
        final TimerTask stabilityTask = new StabilityTask(ssid);
        timer.schedule(stabilityTask, 0, PERIOD_TRY_DISCONNECTED);
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
            if (cnt < CNT_TRY_CONNECTED) {
                if (WifiAgent.connectedTo(getApplicationContext(), WifiConfig.formatSsid(ssid))) {
                    connected(ssid);
                    this.cancel();
                }

            } else this.cancel();

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
                sendConnectionState(ACTION_DISCONNECTED);
            }
        }
    }
}
