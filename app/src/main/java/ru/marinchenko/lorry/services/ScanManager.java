package ru.marinchenko.lorry.services;

import android.app.Service;
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
public class ScanManager extends Service {

    public final static String START = "start";
    public final static String START_TIME = "startTime";
    public final static String RESET = "reset";

    private final static long TURN_TIME = 5000;
    private final IBinder mBinder = new LocalBinder();

    private Timer timerMain;
    private Timer timerPrepare;
    private UpdateTimerTask updateTask;
    private PrepareTimerTask prepareTask;


    @Override
    public void onCreate(){
        super.onCreate();
        updateTask = new UpdateTimerTask();
        prepareTask = new PrepareTimerTask();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()){
                case START:
                    startTimer(intent.getIntExtra(START_TIME, 10));
                    break;
                case RESET:
                    resetTimers();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

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
            timerPrepare = new Timer();
            timerPrepare.schedule(prepareTask, 0, sec * 1000);

            timerMain = new Timer();
            timerMain.schedule(updateTask, TURN_TIME, sec * 1000);

        } else {
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
    }


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
