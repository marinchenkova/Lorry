package ru.marinchenko.lorry.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
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
import java.util.Timer;
import java.util.TimerTask;

import ru.marinchenko.lorry.activities.MainActivity;
import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.WifiConfig;

import static ru.marinchenko.lorry.activities.MainActivity.*;

/**
 * Сервис для работы с Wi-Fi.
 */
public class WifiAgent extends Service {

    public final static String AUTHENTICATE = "auth";
    public final static String AUTO_CONNECT = "autoConnection";
    public final static String CONNECTED = "connected";
    public final static String DISCONNECT = "disconnect";
    public final static String RETURN_NETS = "returnNets";

    private final IBinder mBinder = new LocalBinder();

    private boolean onPause = false;
    private boolean autoConnect = false;
    private boolean allowAutoConnect = true;
    private boolean authenticating = false;
    private int lastId;

    private WifiConfig wifiConfig;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private List<ScanResult> scanResults = new ArrayList<>();
    private List<ScanResult> recs = new ArrayList<>();

    @Override
    public void onCreate(){
        super.onCreate();
        initWifi();
        initTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager.setWifiEnabled(true);

        if (intent.getAction() != null) {
            switch (intent.getAction()){
                case AUTHENTICATE:
                    String ssid = intent.getStringExtra(NET_SSID);
                    String pass = intent.getStringExtra(NET_PASSWORD);
                    authenticate(ssid, pass);
                    break;

                case AUTO_CONNECT:
                    autoConnect = intent.getBooleanExtra(AUTO_CONNECT, false);
                    break;

                case CONNECTED:
                    authenticating = false;
                    allowAutoConnect = false;
                    break;

                case DISCONNECT:
                    disconnect();
                    disableNetwork();
                    break;

                case RETURN_NETS:
                    sendScanResults();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }


    /**
     * Аутентификация в сети.
     * @param ssid имя сети
     * @param pass пароль
     */
    public void authenticate(String ssid, String pass){
        authenticating = true;

        wifiConfig.configure(ssid, scanResults);
        wifiConfig.setPassword(pass);

        lastId = wifiManager.addNetwork(wifiConfig.getConfiguredNet());

        wifiManager.saveConfiguration();
        wifiManager.disconnect();
        wifiManager.enableNetwork(lastId, true);
        wifiManager.reconnect();

    }

    public void autoConnection(){
        if(!authenticating) {
            String ssid = recs.get(0).SSID;
            String pass = NetConfig.generatePass(ssid);
            authenticate(ssid, pass);
        }
    }

    /**
     * Отсоединиться от текущей сети Wi-Fi.
     */
    public void disconnect(){ wifiManager.disconnect(); }

    /**
     * Забыть текущую сеть Wi-Fi.
     */
    public void disableNetwork(){
        wifiManager.removeNetwork(lastId);
        wifiManager.saveConfiguration();
    }

    /**
     * Возвращение имени сети, к которой подключено устройство.
     * @return имя сети (SSID)
     */
    public String getPresentSSID(){
        return wifiManager.getConnectionInfo().getSSID();
    }

    /**
     * Инициализация работы с Wi-Fi сервисом.
     */
    private void initWifi(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        wifiConfig = new WifiConfig();

        IntentFilter wifiFilter = new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        //wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        //wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        //wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //wifiFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        registerReceiver(wifiReceiver, wifiFilter);
    }

    private void initTimer(){
        Timer updateTimer = new Timer();
        TimerTask updateTask = new UpdateTimerTask();
        updateTimer.schedule(updateTask, 0, 1000);
    }

    /**
     * Сканирование доступных Wi-Fi сетей.
     */
    public void scanNets(){
        scanResults.clear();
        recs.clear();

        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        ArrayList<ScanResult> toRemove = new ArrayList<>();
        for(ScanResult s : scanResults) {
            if(s.level < -100) toRemove.add(s);
            else if (NetConfig.ifRec(s.SSID)) {
                recs.add(s);
            }
        }

        if(recs.size() > 0){
          if(autoConnect && allowAutoConnect) autoConnection();
        } else  allowAutoConnect = true;

        scanResults.removeAll(toRemove);
    }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private boolean sendLocalBroadcastMessage(Intent intent){
        return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     *
     */
    public void sendScanResults(){
        scanNets();

        Intent updateNets = new Intent(this, MainActivity.class);
        updateNets.setAction(MainActivity.UPDATE_NETS);

        ArrayList<String> stringList = new ArrayList<>();
        ArrayList<Integer> intList = new ArrayList<>();
        for(ScanResult s : scanResults){
            stringList.add(s.SSID);
            intList.add(s.level);
        }

        updateNets.putStringArrayListExtra(NET_ARR_SSID, stringList);
        updateNets.putIntegerArrayListExtra(NET_ARR_SIGNAL, intList);
        sendLocalBroadcastMessage(updateNets);
    }

    public void sendToVideoInfo(){
        int num = recs.size();
        //if(num > 0) {
        Intent toNet = new Intent(WifiAgent.this, MainActivity.class);
        toNet.setAction(MainActivity.TO_VIDEO);

        toNet.putExtra(NET_NUM, num);
        toNet.putExtra(NET_SSID, getPresentSSID());

        ArrayList<String> stringList = new ArrayList<>();
        for(ScanResult r : recs) stringList.add(r.SSID);
        toNet.putStringArrayListExtra(NET_ARR_SSID, stringList);

        sendLocalBroadcastMessage(toNet);
        //}
    }


    public class LocalBinder extends Binder {
        public WifiAgent getService() { return WifiAgent.this; }
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            sendScanResults();
        }
    }

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            SupplicantState state = wifiManager.getConnectionInfo().getSupplicantState();
            if(state == SupplicantState.COMPLETED && authenticating) {
                authenticating = false;
                sendToVideoInfo();
            }
        }
    }
}
