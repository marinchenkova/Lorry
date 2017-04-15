package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.Settings;
import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.services.ScanManager;
import ru.marinchenko.lorry.services.WifiAgent;
import ru.marinchenko.lorry.util.UpdateFormatter;
import ru.marinchenko.lorry.util.WifiConfigurator;

public class MainActivity extends Activity {

    /**
     * Указатели для приема сообщений, рассылаемых сервисом {@link WifiAgent}.
     */
    public final static String TO_NET = "toNet";
    public final static String UPDATE_NETS = "updateNets";

    private Settings settings = new Settings();
    private boolean autoConnect = false;
    private int updateTime = 0;

    private WifiConfigurator wifiConf = new WifiConfigurator();
    private WifiAgent wifiAgent;
    private boolean wifiAgentBound = false;

    private ScanManager scanManager;
    private boolean scanManagerBound = false;

    private NetListAdapter netListAdapter;
    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResult currNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAutoConnect(findViewById(R.id.autoconnect_checkbox));

        initWifiAgent();
        initNetList();
    }

    /**
     * Инициализация сервиса {@link WifiAgent}. Сервис привязывается к компоненту
     * {@link MainActivity}.
     */
    private void initWifiAgent(){
        Intent wifiAgency = new Intent(this, WifiAgent.class);
        bindService(wifiAgency, wifiAgentConnection, Context.BIND_AUTO_CREATE);

        IntentFilter intFilter = new IntentFilter(TO_NET);
        intFilter.addAction(UPDATE_NETS);
        registerReceiver(br, intFilter);
    }

    /**
     * Инициализация списка доступных Wi-Fi сетей.
     */
    private void initNetList(){
        Intent scanManage = new Intent(this, ScanManager.class);
        bindService(scanManage, scanManagerConnection, Context.BIND_AUTO_CREATE);

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

    protected void onPause() {
        currNet = null;
        super.onPause();
    }

    private void animateTimerTask(){
        findViewById(R.id.textview_update).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    /**
     * Вызывается при нажатии кнопки "Настройки".
     * @param view кнопка
     */
    public void toSettings(View view){
        //TODO toSettings()
    }

    /**
     * Вызывается при переключении флажка "Подключаться автоматически".
     * @param view флажок
     */
    public void setAutoConnect(View view){
        autoConnect = ((CheckBox) view).isChecked();
    }

    /**
     * Установка времени обновления списка достпуных сетей.
     * @param sec время в секундах
     */
    public void setUpdateTime(int sec){
        updateTime = sec;
        if(sec == 0){
            if(wifiAgentBound) wifiAgent.setAutoUpdate(true);
            if(scanManagerBound) scanManager.stopTimer();
        } else if(sec <= 900){
            if(wifiAgentBound) wifiAgent.setAutoUpdate(false);
            if(scanManagerBound) scanManager.startTimer(sec);
        } else {
            if(wifiAgentBound) wifiAgent.setAutoUpdate(false);
            if(scanManagerBound) scanManager.stopTimer();
        }

        ((TextView) findViewById(R.id.textview_update)).setText(UpdateFormatter.format(updateTime));
    }

    /**
     * Вызывается при нажатии кнопки "ОБНОВИТЬ".
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
     * @param net объект {@link ScanResult}, соответствующий пункту в списке доступных сетей
     */
    public void toNet(ScanResult net){
        currNet = net;
        wifiConf.configure(currNet);

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

    /**
     * Переход к {@link VideoStreamActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        if(currNet != null &&
                wifiAgent.getPresentSSID().equals(String.format("\"%s\"", currNet.SSID))){
            Intent in = new Intent(getApplicationContext(), VideoStreamActivity.class);
            startActivityAsChild(in);
        }
    }

    /**
     * Переход к новой {@link Activity} с помещением текущей в стек переходов.
     * При нажатии кнопки "Назад" в вызываемой {@link Activity} не будет зацикливания переходов.
     * @param next вызываемая {@link Activity}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startActivityAsChild(Intent next){
        TaskStackBuilder.create(this).addNextIntentWithParentStack(this.getIntent());
        startActivity(next);
    }

    /**
     * Получатель сообщений сервиса {@link WifiAgent}. При условии, что последняя сеть, выбранная в
     * списке доступных сетей, соответствует сети, к которой подключено устройство, совершается
     * переход к {@link VideoStreamActivity}.
     */
    private BroadcastReceiver br = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case TO_NET:
                    toVideoStream();
                    break;
                case UPDATE_NETS:
                    if(updateTime <= 900) {
                        updateNets(findViewById(R.id.button_update));
                        if(updateTime != 0) animateTimerTask();
                    }
                    break;
            }
        }
    };

    /**
     * Соединение {@link MainActivity} с сервисом {@link WifiAgent}.
     */
    private ServiceConnection wifiAgentConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            WifiAgent.LocalBinder binder = (WifiAgent.LocalBinder) service;
            wifiAgent = binder.getService();
            wifiAgentBound = true;
            updateNets(findViewById(R.id.button_update));
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            wifiAgentBound = false;
        }
    };

    /**
     * Соединение {@link MainActivity} с сервисом {@link ScanManager}.
     */
    private ServiceConnection scanManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ScanManager.LocalBinder binder = (ScanManager.LocalBinder) service;
            scanManager = binder.getService();
            scanManagerBound = true;
            setUpdateTime(updateTime);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            scanManagerBound = false;
        }
    };
}