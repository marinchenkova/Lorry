package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.activities.MainActivity;
import ru.marinchenko.lorry.util.WifiConfigurator;

/**
 *
 */
public class WifiAgent extends IntentService {

    public final static String AUTH = "auth";
    public final static String AUTO_UPDATE = "autoUpdate";
    public final static String CONFIGURE = "configure";
    public final static String RETURN_INFO = "returnInfo";
    public final static String RETURN_NETS = "returnNets";


    private boolean autoUpdate = false;

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private WifiConfigurator wifiConf;

    List<ScanResult> scanResults = new ArrayList<>();

    public WifiAgent(){ super("WifiAgent"); }

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

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

        IntentFilter actionFilter = new IntentFilter(RETURN_NETS);
        actionFilter.addAction(AUTO_UPDATE);
        actionFilter.addAction(CONFIGURE);
        registerReceiver(actionReceiver, actionFilter);
    }

    public Intent wrapScanResults(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        Intent nets = new Intent(MainActivity.UPDATE_NETS);

        ArrayList<String> stringList = new ArrayList<>();
        for(ScanResult s : scanResults)
            stringList.add(s.SSID);

        nets.putStringArrayListExtra("ids", stringList);
        return nets;
    }

    public Intent wrapInfo(){
        Intent info = new Intent(MainActivity.WIFI_INFO);
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
    public void setWifiManager(WifiManager manager){
        wifiManager = manager;
    }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private void sendLocalBroadcastMessage(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
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
                case RETURN_INFO:
                    sendLocalBroadcastMessage(wrapInfo());
                    break;
                case RETURN_NETS:
                    sendLocalBroadcastMessage(wrapScanResults());
                    break;
            }
        }
    };

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            if(autoUpdate) sendLocalBroadcastMessage(wrapScanResults());
        }
    }
}
