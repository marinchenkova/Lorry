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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.activities.MainActivity;
import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.WifiConfigurator;
import ru.marinchenko.lorry.util.WifiStateAgent;

import static ru.marinchenko.lorry.activities.MainActivity.*;

/**
 * Сервис для работы с Wi-Fi.
 */
public class WifiAgent extends Service {

    public final static String AUTHENTICATE = "auth";
    public final static String AUTO_UPDATE = "autoUpdate";
    public final static String AUTO_CONNECT = "autoConnect";
    public final static String CONFIGURE = "configure";
    public final static String CONNECTED = "connected";
    public final static String DISCONNECT = "disconnect";
    public final static String PREPARE_RETURN_NETS = "prepareReturnNets";
    public final static String RETURN_NETS = "returnNets";

    private final IBinder mBinder = new LocalBinder();

    private boolean onPause = false;
    private boolean autoUpdate = false;
    private boolean autoConnect = false;
    private boolean scanResultsReturned = false;
    private boolean authenticating = false;
    private int lastId;

    private WifiConfigurator wifiConf;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private WifiStateAgent wifiStateAgent;

    private List<ScanResult> scanResults = new ArrayList<>();
    private List<ScanResult> recs = new ArrayList<>();


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
                case APPLICATION_ON_PAUSE:
                    onPause = true;
                    break;

                case APPLICATION_ON_RESUME:
                    onPause = false;
                    wifiStateAgent.save();
                    break;

                case AUTHENTICATE:
                    authenticate(intent.getStringExtra(NET_INFO_PASSWORD));
                    break;

                case AUTO_CONNECT:
                    autoConnect = intent.getBooleanExtra(AUTO_CONNECT, false);
                    break;

                case AUTO_UPDATE:
                    //TODO предупреждение о включении Wi-Fi
                    autoUpdate = intent.getBooleanExtra(AUTO_UPDATE, false);
                    if(autoUpdate) wifiStateAgent.wifiOn();
                    else wifiStateAgent.restore();
                    break;

                case CONFIGURE:
                    wifiStateAgent.wifiOn();
                    configure(intent.getStringExtra(NET_INFO_SSID));
                    break;

                case CONNECTED:
                    authenticating = false;
                    break;

                case DISCONNECT:
                    disconnect();
                    disableNetwork();
                    wifiStateAgent.restore();
                    break;

                case PREPARE_RETURN_NETS:
                    if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
                        scanResultsReturned = sendLocalBroadcastMessage(wrapScanResults());
                    } else {
                        if(!autoUpdate) wifiStateAgent.wifiOn();
                        scanResultsReturned = false;
                    }
                    break;

                case RETURN_NETS:
                    if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED &&
                            !scanResultsReturned){
                        sendLocalBroadcastMessage(wrapScanResults());
                        scanResultsReturned = false;
                        if(!autoUpdate) wifiStateAgent.restore();
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        wifiStateAgent.restore();
        super.onDestroy();
    }


    /**
     * Аутентификация в сети.
     * @param password пароль
     */
    public void authenticate(String password){
        authenticating = true;
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

        IntentFilter wifiFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, wifiFilter);

        sendLocalBroadcastMessage(wrapScanResults());
    }

    /**
     * Сканирование доступных Wi-Fi сетей.
     * @return количество сетей видеорегистраторов.
     */
    public int scanRec(){
        scanResults.clear();
        recs.clear();

        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        int num = 0;
        for(ScanResult s : scanResults) {
            if(s.level < -90) scanResults.remove(s);
            else if (NetConfig.ifRec(s.SSID)) {
                recs.add(s);
                num++;
            }
        }

        return num;
    }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private boolean sendLocalBroadcastMessage(Intent intent){
        return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void sendToNetInfo(){
        int num = scanRec();
        //if(num > 0) {
            wifiStateAgent.wifiOn();
            Intent toNet = new Intent(WifiAgent.this, MainActivity.class);
            toNet.setAction(MainActivity.TO_NET);
            toNet.putExtra(NET_INFO_NUM, num);

            ArrayList<String> stringList = new ArrayList<>();
            for(ScanResult r : recs) stringList.add(r.SSID);
            toNet.putStringArrayListExtra(NET_INFO_SSID, stringList);

            sendLocalBroadcastMessage(toNet);
        //}
    }

    /**
     * Упаковать SSID и IP адрес текущей сети в {@link Intent}.
     * @return {@link Intent} с данными
     */
    public Intent wrapCurrentNetInfo(){
        Intent info = new Intent(this, MainActivity.class);
        info.setAction(MainActivity.NET_INFO);
        info.putExtra(NET_INFO_IP, getPresentIP());
        info.putExtra(NET_INFO_SSID, getPresentSSID());
        return info;
    }

    /**
     * Упаковать результаты сканирования в {@link Intent}.
     * @return {@link Intent} с данными
     */
    public Intent wrapScanResults(){
        scanRec();

        Intent updateNets = new Intent(this, MainActivity.class);
        updateNets.setAction(MainActivity.UPDATE_NETS);

        ArrayList<String> stringList = new ArrayList<>();
        for(ScanResult s : scanResults)
            stringList.add(s.SSID);

        updateNets.putStringArrayListExtra(NET_INFO_SSID, stringList);
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
            if(authenticating) {
                sendLocalBroadcastMessage(wrapCurrentNetInfo());
                sendToNetInfo();
            }

            if(autoUpdate) {
                Intent autoUpdate = wrapScanResults();
                autoUpdate.putExtra(AUTO_UPDATE, true);
                sendLocalBroadcastMessage(autoUpdate);
            }

            if(onPause) {
                int num = scanRec();
                if(num > 0) {
                    //TODO уведомление
                }
            }
        }
    }
}
