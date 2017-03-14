package ru.marinchenko.lorry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.util.WifiAuth;

public class MainActivity extends Activity {

    private NetListAdapter netListAdapter;
    private Settings settings = new Settings();

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private WifiConfiguration wifiConfig;
    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResult currNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWifi();

        netListAdapter = new NetListAdapter(this, scanResults);

        if(wifiManager.isWifiEnabled()) scanWifi();

        initNetList();
    }

    private void initWifi(){
        wifiReceiver = new WifiReceiver();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiConfig = new WifiConfiguration();
    }

    private void initNetList(){
        netListAdapter = new NetListAdapter(this, scanResults);

        final ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);

        netListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                currNet = (ScanResult) netListAdapter.getItem(position);
                toNet();
            }
        });
    }

    /**
     * Вызывается при нажатии кнопки "Настройки"
     * @param view кнопка
     */
    public void toSettings(View view){
        //TODO toSettings()
    }

    /**
     * Вызывается при переключении флажка "Подключаться автоматически"
     * @param view флажок
     */
    public void setAutoConnect(View view){
        //TODO setAutoConnect()
    }

    /**
     * Вывод информации о времени обновления списка сетей
     * @param view текстовое поле
     */
    public void showUpdateInfo(View view){
        //TODO showUpdateInfo()
    }

    /**
     * Вызывается при нажатии кнопки "ОБНОВИТЬ"
     * @param view кнопка
     */
    public void updateNets(View view){ scanWifi(); }

    public void scanWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }

        wifiManager.startScan();
        scanResults = wifiManager.getScanResults();

        netListAdapter.updateNets(scanResults);
        netListAdapter.notifyDataSetChanged();
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     */
    public void toNet(){
        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");
    }

    public void authenticate(String password){
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        WifiAuth.configure(wifiConfig, currNet);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.saveConfiguration();

        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        //TODO Переход на другую активити
    }

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) { scanWifi(); }
    }
}
