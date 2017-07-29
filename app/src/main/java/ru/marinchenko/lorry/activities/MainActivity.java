package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.services.ScanManager;
import ru.marinchenko.lorry.services.WifiAgent;
import ru.marinchenko.lorry.util.UpdateFormatter;

import static ru.marinchenko.lorry.services.ScanManager.START;
import static ru.marinchenko.lorry.services.ScanManager.START_TIME;
import static ru.marinchenko.lorry.services.WifiAgent.AUTHENTICATE;
import static ru.marinchenko.lorry.services.WifiAgent.AUTO_UPDATE;
import static ru.marinchenko.lorry.services.WifiAgent.CONFIGURE;
import static ru.marinchenko.lorry.services.WifiAgent.CONNECTED;

public class MainActivity extends Activity {

    /**
     * Указатели для приема сообщений, рассылаемых сервисом {@link WifiAgent}.
     */
    public final static String APPLICATION_ON_PAUSE = "applicationOnPause";
    public final static String APPLICATION_ON_RESUME = "applicationOnResume";
    public final static String TO_NET = "toNet";
    public final static String UPDATE_NETS = "updateNets";
    public final static String NET_INFO = "netInfo";
    public final static String NET_INFO_PASSWORD = "netInfoPassword";
    public final static String NET_INFO_SSID = "netInfoSsid";
    public final static String NET_INFO_NUM = "netInfoNum";

    private int updateTime = 1000;

    private String presentSSID = "";

    private NetListAdapter netListAdapter;
    private String currNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerActionReceiver();
        startWifiAgent();
        initNetList();
    }

    @Override
    protected void onResume() {
        Intent onResume = new Intent(this, WifiAgent.class);
        onResume.setAction(APPLICATION_ON_RESUME);
        startService(onResume);

        startScanManager();
        registerActionReceiver();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean onAuto = sharedPref.getBoolean(SettingsActivity.PREF_AUTOUPDATE, false);
        Boolean onTimer = sharedPref.getBoolean(SettingsActivity.PREF_TIMERUPDATE, false);
        Boolean autoConnect = sharedPref.getBoolean(SettingsActivity.PREF_AUTOCONNECT, false);

        int timerVal = sharedPref.getInt(SettingsActivity.PREF_TIMERUPDATE_VAL, 0);
        updateTime = onTimer ? UpdateFormatter.formatTime(timerVal) : onAuto ? 0 : 1000;
        setUpdateTime(updateTime);

        Intent autoCon = new Intent(this, WifiAgent.class);
        autoCon.setAction(WifiAgent.AUTO_CONNECT);
        autoCon.putExtra(WifiAgent.AUTO_CONNECT, autoConnect);
        startService(autoCon);

        super.onResume();
    }

    @Override
    protected void onPause() {
        Intent wifiOn = new Intent(this, WifiAgent.class);
        wifiOn.setAction(APPLICATION_ON_PAUSE);
        startService(wifiOn);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(actionReceiver);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent stopWifiAgency = new Intent(this, WifiAgent.class);
        stopService(stopWifiAgency);

        Intent stopScanManaging = new Intent(this, ScanManager.class);
        stopService(stopScanManaging);

        super.onDestroy();
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

        //authenticate(NetConfig.generatePass(currNet));

        // Ручной ввод пароля
        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");

    }

    /**
     * Вызывается при нажатии кнопки "Настройки".
     * @param view кнопка
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toSettings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivityAsChild(settings);
    }

    /**
     * Вызывается при нажатии кнопки "ОБНОВИТЬ".
     * @param view кнопка
     */
    public void updateNets(View view){
        startScanManager();
    }


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
        auth.setAction(AUTHENTICATE);
        auth.putExtra(NET_INFO_PASSWORD, password);
        startService(auth);
    }

    /**
     * Инициализация списка доступных Wi-Fi сетей.
     */
    private void initNetList(){
        startScanManager();

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
        startScanManager();

        ((TextView) findViewById(R.id.textview_update))
                .setText(UpdateFormatter.formatString(updateTime));
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
     * Инициализация сервиса {@link ScanManager}.
     */
    private void startScanManager(){
        Intent timer = new Intent(this, ScanManager.class);
        timer.setAction(START);
        timer.putExtra(START_TIME, updateTime);
        startService(timer);
    }

    /**
     * Инициализация сервиса {@link WifiAgent}.
     */
    private void startWifiAgent(){
        Intent wifiAgency = new Intent(this, WifiAgent.class);
        startService(wifiAgency);
    }

    /**
     * Переход к {@link VideoStreamActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        if(currNet != null && presentSSID.equals(String.format("\"%s\"", currNet))){
            Intent success = new Intent(this, WifiAgent.class);
            success.setAction(CONNECTED);
            startService(success);

            Intent in = new Intent(this, VideoStreamActivity.class);
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
                    netListAdapter.updateNets(intent.getStringArrayListExtra(NET_INFO_SSID));
                    netListAdapter.notifyDataSetChanged();
                    if(updateTime > 0 || !intent.getBooleanExtra(AUTO_UPDATE, false)) {
                        animateTimerTask();
                    }
                    break;

                case NET_INFO:
                    presentSSID = intent.getStringExtra(NET_INFO_SSID);
                    break;
            }
        }
    };
}
