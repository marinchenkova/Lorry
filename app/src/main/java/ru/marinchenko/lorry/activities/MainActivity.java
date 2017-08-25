package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.dialogs.LoginDialog;
import ru.marinchenko.lorry.util.Net;
import ru.marinchenko.lorry.util.NetConfig;
import ru.marinchenko.lorry.util.NetList;
import ru.marinchenko.lorry.util.NetListAdapter;
import ru.marinchenko.lorry.services.WifiAgent;

import static ru.marinchenko.lorry.services.WifiAgent.*;

public class MainActivity extends Activity {

    /**
     * Указатели для приема сообщений, рассылаемых сервисом {@link WifiAgent}.
     */
    public final static String APP_PAUSE = "appPause";
    public final static String APP_RESUME = "appResume";
    public final static String MESSENGER = "messenger";
    public final static String NET_SSID = "netSsid";
    public final static String NET_ARR_SSID = "netArrSsid";
    public final static String NET_ARR_SIGNAL = "netArrSignal";
    public final static String NET_PASSWORD = "netPassword";
    public final static String NET_NUM = "netNum";

    public final static int TO_VIDEO = 100;
    public final static int UPDATE_NETS = 200;

    private String presentSSID = "";
    private boolean autoConnect = false;
    private boolean inVideo = false;

    private SharedPreferences sharedPref;
    private NetListAdapter netListAdapter;
    private String currNet;
    private String password;
    public static Handler messageHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageHandler = new MessageHandler(this);
        startWifiAgent();
        initNetList();
        initPreferences();
    }


    @Override
    protected void onResume() {
        inVideo = false;

        Intent onResume = new Intent(this, WifiAgent.class);
        onResume.setAction(APP_RESUME);
        startService(onResume);

        //registerActionReceiver();
        setAutoConnect(findViewById(R.id.autoconnect_checkbox));

        super.onResume();
    }

    @Override
    protected void onPause() {
        Intent wifiOn = new Intent(this, WifiAgent.class);
        wifiOn.setAction(APP_PAUSE);
        startService(wifiOn);
/*
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(actionReceiver);
*/
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent stopWifiAgency = new Intent(this, WifiAgent.class);
        stopService(stopWifiAgency);

        super.onDestroy();
    }

    /**
     * Инициализация списка доступных Wi-Fi сетей.
     */
    private void initNetList(){
        netListAdapter = new NetListAdapter(this);

        final ListView netListView = (ListView) findViewById(R.id.netList);
        netListView.setAdapter(netListAdapter);

        netListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                toNet(((Net) netListAdapter.getItem(position)).getSsid());
            }
        });
    }

    private void initPreferences() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        ((CheckBox) findViewById(R.id.autoconnect_checkbox)).
                setChecked(sharedPref.getBoolean(AUTO_CONNECT, true));
    }

    /**
     * Регистрация получателя сообщений для действия.
     */
    /*
    private void registerActionReceiver(){
        IntentFilter intFilter = new IntentFilter(TO_VIDEO);
        intFilter.addAction(UPDATE_NETS);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(actionReceiver, intFilter);
    }
    */

    public void setAutoConnect(View view) {
        autoConnect = ((CheckBox) view).isChecked();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(AUTO_CONNECT, autoConnect);
        editor.apply();

        Intent autoConnection = new Intent(this, WifiAgent.class);
        autoConnection.setAction(WifiAgent.AUTO_CONNECT);
        autoConnection.putExtra(WifiAgent.AUTO_CONNECT, autoConnect);
        startService(autoConnection);
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
     * Инициализация сервиса {@link WifiAgent}.
     */
    private void startWifiAgent(){
        Intent wifiAgency = new Intent(this, WifiAgent.class);
        wifiAgency.setAction(MESSENGER);
        wifiAgency.putExtra(MESSENGER, new Messenger(messageHandler));
        startService(wifiAgency);
    }

    /**
     * Вызывается при нажатии на сеть из списка доступных сетей. Если сеть раздается
     * видеорегистратором, приложение перейдет к просмотру его камеры.
     * @param net объект {@link ScanResult}, соответствующий пункту в списке доступных сетей
     */
    public void toNet(String net){
        currNet = net;
        password = NetConfig.generatePass(currNet);


        // Ручной ввод пароля
        LoginDialog dialog = new LoginDialog();
        dialog.show(getFragmentManager(), "login");

        /*
        Intent auth = new Intent(this, WifiAgent.class);
        auth.setAction(AUTHENTICATE);
        auth.putExtra(NET_SSID, currNet);
        auth.putExtra(NET_PASSWORD, password);
        startService(auth);
        */
    }

    public void setPassword(String pass){
        password = pass;
        Intent auth = new Intent(this, WifiAgent.class);
        auth.setAction(AUTHENTICATE);
        auth.putExtra(NET_SSID, currNet);
        auth.putExtra(NET_PASSWORD, password);
        startService(auth);
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
     * Переход к {@link VideoStreamActivity}.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void toVideoStream(){
        if(currNet != null && presentSSID.equals(String.format("\"%s\"", currNet)) ||
                autoConnect && !presentSSID.equals("<unknown ssid>")){
            inVideo = true;

            Intent connected = new Intent(this, WifiAgent.class);
            connected.setAction(CONNECTED);
            startService(connected);

            Intent in = new Intent(this, VideoStreamActivity.class);
            currNet = null;

            startActivityAsChild(in);
        }
    }

    /**
     * Вызывается при нажатии кнопки "ОБНОВИТЬ".
     * @param view кнопка
     */
    public void updateNets(View view){
        Intent updateNets = new Intent(this, WifiAgent.class);
        updateNets.setAction(RETURN_NETS);
        startService(updateNets);
    }

    /**
     * Получатель сообщений действия. При условии, что последняя сеть, выбранная в
     * списке доступных сетей, соответствует сети, к которой подключено устройство, совершается
     * переход к {@link VideoStreamActivity}.
     */
    /*
    private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case TO_VIDEO:
                    //TODO уведомление
                    //TODO количество сетей

                    presentSSID = intent.getStringExtra(NET_SSID);
                    ArrayList<String> recs = intent.getStringArrayListExtra(NET_ARR_SSID);
                    int num = intent.getIntExtra(NET_NUM, 1);

                    if(!inVideo) toVideoStream();
                    break;

                case UPDATE_NETS:
                    netListAdapter.updateNets(intent.getStringArrayListExtra(NET_ARR_SSID),
                            intent.getIntegerArrayListExtra(NET_ARR_SIGNAL));
                    netListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    */

    public static class MessageHandler extends Handler {
        private final WeakReference<MainActivity> reference;
        MessageHandler(MainActivity mainActivity) {
            reference = new WeakReference<>(mainActivity);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = reference.get();
            if(activity != null) {
                NetList list = (NetList) msg.obj;

                switch (msg.what) {
                    case TO_VIDEO:
                        //TODO уведомление
                        //TODO количество сетей

                        activity.presentSSID = list.getPresent();
                        ArrayList<String> recs = list.getStringList();
                        int num = list.getSize();

                        if(!activity.inVideo) activity.toVideoStream();
                        break;

                    case UPDATE_NETS:
                        activity.netListAdapter.updateNets(list.getList());
                        activity.netListAdapter.notifyDataSetChanged();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }

        }
    }
}
