package ru.marinchenko.lorry.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.activities.MainActivity;
import ru.marinchenko.lorry.util.WifiConfigurator;
import ru.marinchenko.lorry.util.WifiStateAgent;

/**
 * Сервис для работы с Wi-Fi.
 */
public class WifiAgent extends Service {

    public final static String AUTH = "auth";
    public final static String AUTO_UPDATE = "autoUpdate";
    public final static String AUTO_CONNECT = "autoConnect";
    public final static String CONFIGURE = "configure";
    public final static String DISCONNECT = "disconnect";
    public final static String PREPARE_RETURN_NETS = "prepareReturnNets";
    public final static String RESTORE_WIFI = "restoreWifi";
    public final static String RETURN_INFO = "returnInfo";
    public final static String RETURN_NETS = "returnNets";

    private final IBinder mBinder = new LocalBinder();

    private boolean autoUpdate = false;
    private boolean autoConnect = false;
    boolean scanResultsReturned = false;
    private int lastId;

    private WifiConfigurator wifiConf;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private WifiStateAgent wifiStateAgent;

    private List<ScanResult> scanResults = new ArrayList<>();


    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()){
                case AUTH:
                    wifiStateAgent.saveAndPrepare();
                    authenticate(intent.getStringExtra("password"));
                    break;

                case AUTO_CONNECT:
                    autoConnect = intent.getBooleanExtra("flag", false);
                    break;

                case AUTO_UPDATE:
                    //TODO предупреждение о включении Wi-Fi
                    autoUpdate = intent.getBooleanExtra("flag", false);
                    if(autoUpdate) wifiStateAgent.saveAndPrepare();
                    else wifiStateAgent.restore(false);
                    break;

                case CONFIGURE:
                    configure(intent.getStringExtra("id"));
                    break;

                case DISCONNECT:
                    disconnect();
                    disableNetwork();
                    wifiStateAgent.restore(true);
                    break;

                case PREPARE_RETURN_NETS:
                    if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
                        scanResultsReturned = sendLocalBroadcastMessage(wrapScanResults());
                    } else {
                        wifiStateAgent.saveAndPrepare();
                        scanResultsReturned = false;
                    }
                    break;

                case RESTORE_WIFI:
                    wifiStateAgent.restore(false);
                    break;

                case RETURN_INFO:
                    sendLocalBroadcastMessage(wrapNetInfo());
                    break;

                case RETURN_NETS:
                    if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED &&
                            !scanResultsReturned){
                        sendLocalBroadcastMessage(wrapScanResults());
                        scanResultsReturned = false;
                        wifiStateAgent.restore(false);
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        wifiStateAgent.restore(true);
        super.onDestroy();
    }


    /**
     * Аутентификация в сети.
     * @param password пароль
     */
    public void authenticate(String password){
        wifiConf.setPassword(password);

        lastId = wifiManager.addNetwork(wifiConf.getConfiguredNet());
        wifiManager.saveConfiguration();

        wifiManager.disconnect();
        wifiManager.enableNetwork(lastId, true);
        wifiManager.reconnect();
    }

    /**
     * Конфигурировать выбранную сеть.
     * @param SSID имя сети
     */
    public void configure(String SSID){
        for(ScanResult s : scanResults)
            if(s.SSID.equals(SSID)) {
                wifiConf.configure(s);
                break;
            }
    }

    /**
     * Отсоединиться от текущей сети Wi-Fi.
     */
    public void disconnect(){ wifiManager.disconnect(); }

    /**
     * Забыть текущую сеть Wi-Fi.
     */
    public void disableNetwork(){ wifiManager.disableNetwork(lastId); }

    /**
     * Возвращение имени сети, к которой подключено устройство.
     * @return имя сети (SSID)
     */
    public String getPresentSSID(){ return wifiManager.getConnectionInfo().getSSID(); }

    /**
     * Возвращение IP адреса сети, к которой подключено устройство.
     * @return IP адрес
     */
    public int getPresentIP(){ return wifiManager.getConnectionInfo().getIpAddress(); }

    /**
     * Инициализация работы с Wi-Fi сервисом.
     */
    private void init(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        wifiConf = new WifiConfigurator();
        wifiStateAgent = new WifiStateAgent(wifiManager);
        wifiStateAgent.saveAndPrepare();

        IntentFilter wifiFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, wifiFilter);
    }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private boolean sendLocalBroadcastMessage(Intent intent){
        return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Упаковать SSID и IP адрес текущей сети в {@link Intent}.
     * @return {@link Intent} с данными
     */
    public Intent wrapNetInfo(){
        Intent info = new Intent(this, MainActivity.class);
        info.setAction(MainActivity.WIFI_INFO);
        info.putExtra("IP", getPresentIP());
        info.putExtra("SSID", getPresentSSID());
        return info;
    }

    /**
     * Упаковать результаты сканирования в {@link Intent}.
     * @return {@link Intent} с данными
     */
    public Intent wrapScanResults(){
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


    public class LocalBinder extends Binder {
        public WifiAgent getService() { return WifiAgent.this; }
    }

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            switch (intent.getAction()){
                case WifiManager.ACTION_PICK_WIFI_NETWORK:
                    Intent toNet = new Intent(WifiAgent.this, MainActivity.class);
                    toNet.setAction(MainActivity.TO_NET);
                    sendLocalBroadcastMessage(toNet);
                    break;
            }

            if(autoUpdate) sendLocalBroadcastMessage(wrapScanResults());
        }
    }
}
