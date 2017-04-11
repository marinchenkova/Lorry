package ru.marinchenko.lorry.util;

import android.app.IntentService;
import android.app.Service;
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

import ru.marinchenko.lorry.MainActivity;

/**
 *
 */
public class WifiAgent extends IntentService {

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private final IBinder mBinder = new LocalBinder();
    private MainActivity mainActivity;


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
    protected void onHandleIntent(Intent intent) {

    }


    private void init(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        IntentFilter inf = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        inf.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        inf.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
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


    /**
     *
     * @return
     */
    public String getPresentSSID(){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }


    public void setWifiManager(WifiManager manager){
        wifiManager = manager;
    }


    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            Intent in = new Intent(MainActivity.TO_NET);
            sendBroadcast(in);
        }
    }
}
