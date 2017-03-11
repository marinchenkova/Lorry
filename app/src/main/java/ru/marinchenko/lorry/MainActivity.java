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

        wifiReceiver = new WifiReceiver();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiConfig = new WifiConfiguration();

        netListAdapter = new NetListAdapter(this, scanResults);

        if(wifiManager.isWifiEnabled()) scanWifi();

        initNetList();
    }


    private void initNetList(){
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
    public void updateNets(View view){
        scanWifi();
    }

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
     * Символы для пароля в кодировке UTF-8: 0 - 9 (48-57), A - Z (65-90), a - z (97-122)
     */
    public void pick(int num){
        String password;
        char pass[] = new char[num];
        char sym[] = new char[62];
        int q;
        boolean ok = false;

        for(int i = 0; i < 62; i++){
            if(i < 10) sym[i] = (char) (i + 48);
            else if(i < 36) sym[i] = (char) (i + 55);
            else sym[i] = (char) (i + 61);
        }

        for(int i = 0; i < Math.pow(62, num); i++){
            for(int j = 0; j < num; j++){
                q = i;
                for(int k = num - 1 - j; k > 0; k--) q /= 62;
                pass[j] = sym[q % 62];
                password = String.valueOf(pass);
                if((j >= num - 1) || ok) authentificate(password);
            }
            ok = true;
        }
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     */
    public void toNet(){
        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");
    }

    public void authentificate(String password){
        /*
        Context context = getApplicationContext();
        CharSequence text = password;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();*/

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
        public void onReceive(Context c, Intent intent) {
            scanWifi();
        }
    }
}
