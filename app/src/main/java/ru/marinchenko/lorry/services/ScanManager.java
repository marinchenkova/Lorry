package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Сервис предназначен для обновления списка доступных сетей в
 * {@link ru.marinchenko.lorry.activities.MainActivity}. Обновление может производиться по кнопке,
 * по таймеру или автоматически.
 */
public class ScanManager extends IntentService{

    private final IBinder mBinder = new LocalBinder();

    private final static long TURN_TIME = 5000;
    private boolean onTimer = false;

    private Timer timerMain;
    private Timer timerPrepare;
    private UpdateTimerTask updateTask;
    private PrepareTimerTask prepareTask;

    public ScanManager(){ super("ScanManager"); }

    @Override
    public void onCreate(){
        super.onCreate();
        updateTask = new UpdateTimerTask();
        prepareTask = new PrepareTimerTask();
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

    @Override
    public void onDestroy() {
        resetTimers();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    /**
     * Запуск таймера.
     * @param sec время обновления списка в секундах
     */
    public void startTimer(int sec) {
        resetTimers();

        Intent autoUpdate = new Intent(this, WifiAgent.class);
        autoUpdate.setAction(WifiAgent.AUTO_UPDATE);
        autoUpdate.putExtra(WifiAgent.AUTO_UPDATE, sec == 0);
        startService(autoUpdate);

        if (sec != 0 && sec <= 900) {
            onTimer = true;

            timerPrepare = new Timer();
            timerPrepare.schedule(prepareTask, 0, sec * 1000);

            timerMain = new Timer();
            timerMain.schedule(updateTask, TURN_TIME, sec * 1000);

        } else {
            onTimer = false;

            timerPrepare = new Timer();
            timerPrepare.schedule(prepareTask, 0);

            timerMain = new Timer();
            timerMain.schedule(updateTask, TURN_TIME);
        }

    }

    /**
     * Остановка таймеров.
     */
    public void resetTimers(){
        if(timerPrepare != null){
            timerPrepare.cancel();
            timerPrepare = null;
        }

        if(timerMain != null){
            timerMain.cancel();
            timerMain = null;
        }

        prepareTask = new PrepareTimerTask();
        updateTask = new UpdateTimerTask();
        onTimer = false;
    }

    /**
     * Проверка, работает ли обновление списка сетей по таймеру.
     * @return {@code true} если обновление происходит по таймеру
     */
    public boolean isOnTimer(){ return onTimer; }


    public class LocalBinder extends Binder {
        public ScanManager getService() { return ScanManager.this; }
    }

    /**
     * Задача вспомогательного таймера {@link ScanManager#timerPrepare} - предварительное включение
     * Wi-Fi сервиса, если он был выключен, иначе происходит обновление списка доступных сетей.
     */
    private class PrepareTimerTask extends TimerTask{
        @Override
        public void run() {
            Intent update = new Intent(ScanManager.this, WifiAgent.class);
            update.setAction(WifiAgent.PREPARE_RETURN_NETS);
            startService(update);
        }
    }

    /**
     * Задача таймера {@link ScanManager#timerMain} - обновление списка доступных сетей.
     */
    private class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            Intent update = new Intent(ScanManager.this, WifiAgent.class);
            update.setAction(WifiAgent.RETURN_NETS);
            startService(update);
        }
    }
}
