package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Обновление списка доступных сетей по таймеру.
 */
public class ScanManager extends IntentService{

    private Timer timer;
    private UpdateTimerTask updateTask;
    private boolean onTimer = false;

    private final IBinder mBinder = new LocalBinder();


    public ScanManager(){ super("ScanManager"); }

    @Override
    public void onCreate(){
        super.onCreate();
        updateTask = new UpdateTimerTask();
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

    public class LocalBinder extends Binder {
        public ScanManager getService() { return ScanManager.this; }
    }

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    /**
     * Запуск таймера.
     * @param sec время обновления списка в секундах
     */
    public void startTimer(int sec) {
        resetTimer();

        if (sec == 0) {
            Intent autoUpdate = new Intent(WifiAgent.AUTO_UPDATE);
            autoUpdate.putExtra("flag", true);
            sendLocalBroadcastMessage(autoUpdate);

        } else if (sec <= 900) {
            onTimer = true;
            timer = new Timer();
            timer.schedule(updateTask, 0, sec*1000);
        }
    }

    /**
     * Остановка таймера.
     */
    public void resetTimer(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        onTimer = false;
    }

    /**
     * Выполнение задачи таймера вне расписания.
     */
    public void scan(){
        updateTask.run();
    }

    /**
     * Проверка, работает ли обновление списка сетей по таймеру.
     * @return {@code true} если обновление происходит по таймеру
     */
    public boolean isOnTimer(){ return onTimer; }

    /**
     * Отправка сообщений внутри приложения.
     * @param intent сообщение
     */
    private void sendLocalBroadcastMessage(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Задача таймера - обновление списка доступных сетей.
     */
    private class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            Intent update = new Intent(WifiAgent.RETURN_NETS);
            sendLocalBroadcastMessage(update);
        }
    }
}
