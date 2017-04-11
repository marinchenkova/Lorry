package ru.marinchenko.lorry;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import ru.marinchenko.lorry.util.WifiAgent;
import ru.marinchenko.lorry.util.WifiConfigurator;

public class MainActivity extends Activity {

    private NetListAdapter netListAdapter;
    private Settings settings = new Settings();

    private WifiConfigurator wifiConf = new WifiConfigurator();
    private WifiAgent wifiAgent;
    private boolean wifiAgentBound = false;

    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResult currNet;

    public final static String TO_NET = "toNet";

    private boolean autoConnect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAutoConnect(findViewById(R.id.autoconnect_checkbox));

        startWifiAgent();
        initNetList();
    }


    private void startWifiAgent(){
        Intent intent = new Intent(this, WifiAgent.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        IntentFilter intFilt = new IntentFilter(TO_NET);
        registerReceiver(br, intFilt);
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
    public void updateNets(View view){
        if(wifiAgentBound) {
            netListAdapter.updateNets(wifiAgent.scanWifi());
            netListAdapter.notifyDataSetChanged();
        }
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
        if(wifiAgentBound) wifiAgent.authenticate(wifiConf.getConfiguredNet());
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        if(currNet != null &&
                wifiAgent.getPresentSSID().equals(String.format("\"%s\"", currNet.SSID))){
            Intent in = new Intent(getApplicationContext(), VideoStreamActivity.class);
            startActivityAsChild(in);
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


    private BroadcastReceiver br = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            toVideoStream();
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            WifiAgent.LocalBinder binder = (WifiAgent.LocalBinder) service;
            wifiAgent = binder.getService();
            wifiAgentBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            wifiAgentBound = false;
        }
    };
}
