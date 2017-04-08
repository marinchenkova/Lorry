package ru.marinchenko.lorry;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.util.WifiConfigurator;

public class MainActivity extends Activity {

    private NetListAdapter netListAdapter;
    private Settings settings = new Settings();

    private WifiConfigurator wifiConf;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResult currNet;

    private int updateTimer = 1;
    private boolean autoConnect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAutoConnect(findViewById(R.id.autoconnect_checkbox));

        initWifi();
        initNetList();
    }


    private void initWifi(){
        wifiConf = new WifiConfigurator();
        wifiReceiver = new WifiReceiver();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }


    private void initNetList(){
        netListAdapter = new NetListAdapter(this, scanResults);

        final ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);

        netListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                toNet((ScanResult) netListAdapter.getItem(position));
            }
        });
    }


    public void setWifiManager(WifiManager manager){
        wifiManager = manager;
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
    public void setAutoConnect(View view){ autoConnect = ((CheckBox) view).isChecked(); }


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


    /**
     * Сканирование доступных Wi-Fi сетей.
     */
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
    public void toNet(ScanResult net){
        currNet = net;
        wifiConf.configure(currNet);

        Toast.makeText(this, currNet.capabilities, Toast.LENGTH_LONG).show();

        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");
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
     * Проверка подключения к сети, которая была выбрана из списка доступных сетей.
     * @return {@code true}, если подключение есть
     */
    public boolean isConnectedRight(){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return currNet != null &&
                wifiInfo.getSSID().equals(String.format("\"%s\"", currNet.SSID));
    }


    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }


    protected void onResume() {
        IntentFilter inf = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        inf.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        inf.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, inf);
        currNet = null;
        super.onResume();
    }


    private class WifiReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context c, Intent intent) {
            if(updateTimer == 0) scanWifi();

            if (isConnectedRight()) {
                Intent i = new Intent(getApplicationContext(), VideoStreamActivity.class);
                startActivityAsChild(i);
            }
        }
    }


    /**
     * Вызов Activity с помещением текущей Activity в стек переходов. При нажатии кнопки "Назад" в
     * вызываемой Activity не будет зацикливания переходов.
     * @param next вызываемая Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startActivityAsChild(Intent next){
        TaskStackBuilder.create(this).addNextIntentWithParentStack(this.getIntent());
        startActivity(next);
    }
}
