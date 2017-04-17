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
import android.support.v4.content.LocalBroadcastManager;
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
    public final static String WIFI_INFO = "wifiInfo";

    private Settings settings = new Settings();
    private boolean autoConnect = false;
    private int updateTime = 10;

    private String presentSSID;
    private int presentIP;

    private ScanManager scanManager;
    private boolean scanManagerBound = false;

    private NetListAdapter netListAdapter;
    private String currNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAutoConnect(findViewById(R.id.autoconnect_checkbox));

        registerActionReceiver();
        initWifiAgent();
        initNetList();
    }

    @Override
    protected void onDestroy() {
        if (scanManagerConnection != null) {
            unbindService(scanManagerConnection);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(actionReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerActionReceiver();
        super.onResume();
    }


    /**
     * Инициализация сервиса {@link WifiAgent}. Сервис привязывается к компоненту
     * {@link MainActivity}.
     */
    private void initWifiAgent(){
        Intent wifiAgency = new Intent(this, WifiAgent.class);
        wifiAgency.setAction(WifiAgent.RETURN_NETS);
        startService(wifiAgency);
    }

    /**
     * Инициализация списка доступных Wi-Fi сетей.
     */
    private void initNetList(){
        Intent scanManage = new Intent(this, ScanManager.class);
        bindService(scanManage, scanManagerConnection, Context.BIND_AUTO_CREATE);

        netListAdapter = new NetListAdapter(this);

        final ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);

        netListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                toNet((String) netListAdapter.getItem(position));
            }
        });
    }

    /**
     * Регистрация получателя сообщений для действия.
     */
    private void registerActionReceiver(){
        IntentFilter intFilter = new IntentFilter(TO_NET);
        intFilter.addAction(UPDATE_NETS);
        intFilter.addAction(WIFI_INFO);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(actionReceiver, intFilter);
    }


    /**
     * Вызывается при нажатии кнопки "Настройки".
     * @param view кнопка
     */
    public void toSettings(View view){
        //TODO toSettings()
    }

    /**
     * Вызывается при нажатии кнопки "ОБНОВИТЬ".
     * @param view кнопка
     */
    public void updateNets(View view){
        scanManager.scan();
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     * @param net объект {@link ScanResult}, соответствующий пункту в списке доступных сетей
     */
    public void toNet(String net){
        currNet = net;
        Intent config = new Intent(this, WifiAgent.class);
        config.setAction(WifiAgent.CONFIGURE);
        config.putExtra("id", currNet);
        startService(config);

        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");
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
        if(scanManagerBound) scanManager.startTimer(sec);
        ((TextView) findViewById(R.id.textview_update))
                .setText(UpdateFormatter.format(updateTime));
    }

    /**
     * Аутентификация в сети.
     * @param password пароль
     */
    public void authenticate(String password){
        Intent auth = new Intent(this, WifiAgent.class);
        auth.setAction(WifiAgent.AUTH);
        auth.putExtra("password", password);
        startService(auth);
    }

    /**
     * Переход к {@link VideoStreamActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        Intent info = new Intent(this, WifiAgent.class);
        info.setAction(WifiAgent.RETURN_INFO);
        startService(info);

        if(currNet != null && presentSSID.equals(String.format("\"%s\"", currNet))){
            Intent in = new Intent(this, VideoStreamActivity.class);
            in.putExtra("IP", presentIP);
            currNet = null;
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
     * Анимация текстового поля с информацией о способе обновления списка.
     */
    private void animateTimerTask(){
        findViewById(R.id.textview_update).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    /**
     * Получатель сообщений действия. При условии, что последняя сеть, выбранная в
     * списке доступных сетей, соответствует сети, к которой подключено устройство, совершается
     * переход к {@link VideoStreamActivity}.
     */
    private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case TO_NET:
                    toVideoStream();
                    break;

                case UPDATE_NETS:
                    netListAdapter.updateNets(intent.getStringArrayListExtra("ids"));
                    netListAdapter.notifyDataSetChanged();
                    if(scanManagerBound && scanManager.isOnTimer()) animateTimerTask();
                    break;

                case WIFI_INFO:
                    presentIP = intent.getIntExtra("IP", 0);
                    presentSSID = intent.getStringExtra("SSID");
                    break;
            }
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
        public void onServiceDisconnected(ComponentName arg0) { scanManagerBound = false; }
    };
}
