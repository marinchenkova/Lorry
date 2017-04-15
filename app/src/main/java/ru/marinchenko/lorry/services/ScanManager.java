package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import ru.marinchenko.lorry.activities.MainActivity;

/**
 * Обновление списка доступных сетей по таймеру.
 */
public class ScanManager extends IntentService{

    private Timer timer;
    private UpdateTimerTask updateTask;

    private final IBinder mBinder = new LocalBinder();


    public ScanManager(){
        super("ScanManager");
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

    public class LocalBinder extends Binder {
        public ScanManager getService() {
            return ScanManager.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        updateTask = new UpdateTimerTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Запуск таймера.
     * @param sec время обновления списка в секундах
     */
    public void startTimer(int sec){
        stopTimer();
        timer = new Timer();
        timer.schedule(updateTask, 0, sec*1000);
    }

    /**
     * Остановка таймера.
     */
    public void stopTimer(){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Задача таймера - обновление списка доступных сетей.
     */
    private class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            Intent update = new Intent(MainActivity.UPDATE_NETS);
            sendBroadcast(update);

        }
    }
}
