package ru.marinchenko.lorry.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.util.Net;
import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.NetList;
import ru.marinchenko.lorry.util.WifiConfig;

import static junit.framework.Assert.assertNotNull;
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
    private int auth = 0;

    private Messenger messenger = null;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()){
                case AUTHENTICATE:
                    auth = 1;
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

                case MESSENGER:
                    Bundle extras = intent.getExtras();
                    messenger = (Messenger) extras.get(MESSENGER);
                    break;

                case RETURN_NETS:
                    sendMessage(UPDATE_NETS);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void authenticate(String ssid, String pass) {
        authenticating = true;

        //sendMessage(AUTH_IN_PROCESS);

        wifiConfig.configure(ssid, scanResults);
        wifiConfig.setPassword(pass);

        lastId = wifiManager.addNetwork(wifiConfig.getConfiguredNet());

        wifiManager.saveConfiguration();
        wifiManager.disconnect();
        wifiManager.enableNetwork(lastId, true);
        wifiManager.reconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void autoConnection(){
        if(!authenticating) {
            auth = 1;
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
        wifiFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        registerReceiver(wifiReceiver, wifiFilter);
        wifiManager.setWifiEnabled(true);
    }

    private void initTimer(){
        Timer updateTimer = new Timer();
        TimerTask updateTask = new UpdateTimerTask();
        updateTimer.schedule(updateTask, 0, 1000);
    }



    /**
     * Сканирование доступных Wi-Fi сетей.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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

        if(recs.size() > 0 && autoConnect){
          if(allowAutoConnect) autoConnection();
        } else  allowAutoConnect = true;

        scanResults.removeAll(toRemove);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void sendMessage(int what) {
        scanNets();

        ArrayList<Net> nets = new ArrayList<>();
        NetList list;

        switch (what) {
            case TO_VIDEO:
                for(ScanResult s : recs){
                    nets.add(new Net(s.SSID, s.level));
                }
                break;

            case UPDATE_NETS:
                for(ScanResult s : scanResults){
                    nets.add(new Net(s.SSID, s.level));
                }
                break;
        }

        list = new NetList(nets);
        list.setPresent(getPresentSSID());

        Message message = Message.obtain(null, what, list);

        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public class LocalBinder extends Binder {
        public WifiAgent getService() { return WifiAgent.this; }
    }

    private class UpdateTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            sendMessage(UPDATE_NETS);
        }
    }

    /**
     * Получатель сообщений для объекта {@link WifiManager}.
     */
    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context c, Intent intent) {
            SupplicantState state = wifiManager.getConnectionInfo().getSupplicantState();
            if(state == SupplicantState.COMPLETED && authenticating) {
                authenticating = false;
                sendMessage(TO_VIDEO);
            }

            /*
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                sendMessage(UPDATE_NETS);
            }
            */
        }
    }
}
