package ru.marinchenko.lorry.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.activities.MainActivity;
import ru.marinchenko.lorry.util.WifiConfigurator;

/**
 *
 */
public class WifiAgent extends Service {

    public final static String AUTH = "auth";
    public final static String AUTO_UPDATE = "autoUpdate";
    public final static String CONFIGURE = "configure";
    public final static String DISCONNECT = "disconnect";
    public final static String RECONNECT = "reconnect";
    public final static String RETURN_INFO = "returnInfo";
    public final static String RETURN_NETS = "returnNets";


    private boolean autoUpdate = false;

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private WifiConfigurator wifiConf;

    List<ScanResult> scanResults = new ArrayList<>();

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()){
                case AUTH:
                    authenticate(intent.getStringExtra("password"));
                    break;
                case AUTO_UPDATE:
                    autoUpdate = intent.getBooleanExtra("flag", false);
                    break;
                case CONFIGURE:
                    configure(intent.getStringExtra("id"));
                    break;
                case DISCONNECT:
                    disconnect();
                    break;
                case RECONNECT:
                    reconnect();
                    break;
                case RETURN_INFO:
                    sendLocalBroadcastMessage(wrapInfo());
                    break;
                case RETURN_NETS:
                    sendLocalBroadcastMessage(wrapScanResults());
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Инициализация {@link WifiManager}.
     */
    private void init(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        wifiConf = new WifiConfigurator();

        IntentFilter wifiFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        registerReceiver(wifiReceiver, wifiFilter);
    }

    public Intent wrapScanResults(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        Intent updateNets = new Intent(this, MainActivity.class);
        updateNets.setAction(MainActivity.UPDATE_NETS);

        ArrayList<String> stringList = new ArrayList<>();
        for(ScanResult s : scanResults)
            stringList.add(s.SSID);

        updateNets.putStringArrayListExtra("ids", stringList);
        return updateNets;
    }

    public Intent wrapInfo(){
        Intent info = new Intent(this, MainActivity.class);
        info.setAction(MainActivity.WIFI_INFO);
        info.putExtra("IP", getPresentIP());
        info.putExtra("SSID", getPresentSSID());
        return info;
    }

    public void configure(String id){
        for(ScanResult s : scanResults)
            if(s.SSID.equals(id)) {
                wifiConf.configure(s);
                break;
            }
    }

    /**
     * Аутентификация в сети.
     * @param password пароль
     */
    public void authenticate(String password){
        wifiConf.setPassword(password);

        int netId = wifiManager.addNetwork(wifiConf.getConfiguredNet());
        wifiManager.saveConfiguration();

        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public void disconnect(){
        wifiManager.disconnect();
    }

    public void reconnect(){
        wifiManager.reconnect();
    }

    /**
     * Возвращение имени сети, к которой подключено устройство.
     * @return имя сети (SSID)
     */
    public String getPresentSSID(){
        return wifiManager.getConnectionInfo().getSSID();
    }

    /**
     * Возвращение IP адреса сети, к которой подключено устройство.
     * @return IP адрес
     */
    public int getPresentIP(){
        return wifiManager.getConnectionInfo().getIpAddress();
    }

    /**
     * Задание {@link WifiManager}. Используется для тестирования.
     * @param manager альтернативный {@link WifiManager}
     */
    public void setWifiManager(WifiManager manager){ wifiManager = manager; }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private void sendLocalBroadcastMessage(Intent intent){
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            Intent toNet = new Intent(WifiAgent.this, MainActivity.class);
            toNet.setAction(MainActivity.TO_NET);
            sendLocalBroadcastMessage(toNet);

            if(autoUpdate) sendLocalBroadcastMessage(wrapScanResults());
        }
    }
}
