package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.List;

import ru.marinchenko.lorry.activities.MainActivity;

/**
 *
 */
public class WifiAgent extends IntentService {

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private final IBinder mBinder = new LocalBinder();

    private boolean autoUpdate = false;

    public WifiAgent(){
        super("WifiAgent");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    public class LocalBinder extends Binder {
        public WifiAgent getService() {
            return WifiAgent.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

    /**
     * Инициализация {@link WifiManager}.
     */
    private void init(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        IntentFilter inf = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        inf.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);

        registerReceiver(wifiReceiver, inf);
    }

    /**
     * Сканирование доступных Wi-Fi сетей.
     */
    public List<ScanResult> scanWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        return wifiManager.getScanResults();
    }

    /**
     * Аутентификация в сети.
     * @param config конфигурация сети
     */
    public void authenticate(WifiConfiguration config){
        int netId = wifiManager.addNetwork(config);
        wifiManager.saveConfiguration();

        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public void setAutoUpdate(boolean auto){
        autoUpdate = auto;
    }

    /**
     * Возвращение имени сети, к которой подключено устройство.
     * @return имя сети (SSID)
     */
    public String getPresentSSID(){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     * Задание {@link WifiManager}. Используется для тестирования.
     * @param manager альтернативный {@link WifiManager}
     */
    public void setWifiManager(WifiManager manager){
        wifiManager = manager;
    }

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            Intent toNet = new Intent(MainActivity.TO_NET);
            Intent update = new Intent(MainActivity.UPDATE_NETS);

            sendBroadcast(toNet);
            if(autoUpdate) sendBroadcast(update);
        }
    }
}