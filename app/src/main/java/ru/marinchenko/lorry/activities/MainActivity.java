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

import java.util.ArrayList;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.Settings;
import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.services.ScanManager;
import ru.marinchenko.lorry.services.WifiAgent;
import ru.marinchenko.lorry.util.UpdateFormatter;

import static ru.marinchenko.lorry.services.WifiAgent.AUTH;
import static ru.marinchenko.lorry.services.WifiAgent.AUTO_CONNECT;
import static ru.marinchenko.lorry.services.WifiAgent.CONFIGURE;

public class MainActivity extends Activity {

    /**
     * Указатели для приема сообщений, рассылаемых сервисом {@link WifiAgent}.
     */
    public final static String APPLICATION_ON_PAUSE = "applicationOnPause";
    public final static String APPLICATION_ON_RESUME = "applicationOnResume";
    public final static String TO_NET = "toNet";
    public final static String UPDATE_NETS = "updateNets";
    public final static String NET_INFO = "netInfo";
    public final static String NET_INFO_IP = "netInfoIp";
    public final static String NET_INFO_PASSWORD = "netInfoPassword";
    public final static String NET_INFO_SSID = "netInfoSsid";
    public final static String NET_INFO_NUM = "netInfoNum";

    private Settings settings = new Settings();
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
    protected void onResume() {
        Intent onResume = new Intent(this, WifiAgent.class);
        onResume.setAction(APPLICATION_ON_RESUME);
        startService(onResume);

        if(scanManagerBound) scanManager.startTimer(updateTime);
        registerActionReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(scanManagerBound) scanManager.resetTimers();

        Intent wifiOn = new Intent(this, WifiAgent.class);
        wifiOn.setAction(APPLICATION_ON_PAUSE);
        startService(wifiOn);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(actionReceiver);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (scanManagerConnection != null) unbindService(scanManagerConnection);

        Intent stopWifiAgency = new Intent(this, WifiAgent.class);
        stopService(stopWifiAgency);

        super.onDestroy();
    }


    /**
     * Вызывается при переключении флажка "Подключаться автоматически".
     * @param view флажок
     */
    public void setAutoConnect(View view){
        Intent autoConnection = new Intent(this, WifiAgent.class);
        autoConnection.setAction(AUTO_CONNECT);
        autoConnection.putExtra(AUTO_CONNECT, ((CheckBox) view).isChecked());
        startService(autoConnection);
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     * @param net объект {@link ScanResult}, соответствующий пункту в списке доступных сетей
     */
    public void toNet(String net){
        currNet = net;
        Intent config = new Intent(this, WifiAgent.class);
        config.setAction(CONFIGURE);
        config.putExtra(NET_INFO_SSID, currNet);
        startService(config);

        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");
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
    public void updateNets(View view){ scanManager.startTimer(updateTime); }


    /**
     * Анимация текстового поля с информацией о способе обновления списка.
     */
    private void animateTimerTask(){
        findViewById(R.id.textview_update).startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    /**
     * Аутентификация в сети.
     * @param password пароль
     */
    public void authenticate(String password){
        Intent auth = new Intent(this, WifiAgent.class);
        auth.setAction(AUTH);
        auth.putExtra(NET_INFO_PASSWORD, password);
        startService(auth);
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
     * Инициализация сервиса {@link WifiAgent}.
     */
    private void initWifiAgent(){
        Intent wifiAgency = new Intent(this, WifiAgent.class);
        startService(wifiAgency);
    }

    /**
     * Регистрация получателя сообщений для действия.
     */
    private void registerActionReceiver(){
        IntentFilter intFilter = new IntentFilter(TO_NET);
        intFilter.addAction(UPDATE_NETS);
        intFilter.addAction(NET_INFO);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(actionReceiver, intFilter);
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
     * Переход к {@link VideoStreamActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        Intent info = new Intent(this, WifiAgent.class);
        info.setAction(WifiAgent.RETURN_CURRENT_NET_INFO);
        startService(info);

        if(currNet != null && presentSSID.equals(String.format("\"%s\"", currNet))){
            Intent in = new Intent(this, VideoStreamActivity.class);
            in.putExtra(NET_INFO_IP, presentIP);
            currNet = null;
            startActivityAsChild(in);
        }
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
                    //TODO уведомление
                    toVideoStream();
                    break;

                case UPDATE_NETS:
                    ArrayList<String> arr = intent.getStringArrayListExtra(NET_INFO_SSID);
                    boolean empty = arr.isEmpty();

                    for(String s : arr){
                        if(s == null || s.equals("")) {
                            empty = true;
                            break;
                        }
                    }

                    if(!empty){
                        netListAdapter.updateNets(intent.getStringArrayListExtra(NET_INFO_SSID));
                        netListAdapter.notifyDataSetChanged();
                        if(scanManagerBound && scanManager.isOnTimer()) animateTimerTask();
                    }

                    break;

                case NET_INFO:
                    presentIP = intent.getIntExtra(NET_INFO_IP, 0);
                    presentSSID = intent.getStringExtra(NET_INFO_SSID);
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
