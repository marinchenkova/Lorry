package ru.marinchenko.lorry.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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
    public void onDestroy() {
        resetTimer();
        super.onDestroy();
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

        Intent autoUpdate = new Intent(this, WifiAgent.class);
        autoUpdate.setAction(WifiAgent.AUTO_UPDATE);
        autoUpdate.putExtra("flag", sec == 0);
        startService(autoUpdate);

        if (sec != 0 && sec <= 900) {
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
    public void scan(){ updateTask.run(); }

    /**
     * Проверка, работает ли обновление списка сетей по таймеру.
     * @return {@code true} если обновление происходит по таймеру
     */
    public boolean isOnTimer(){ return onTimer; }

    /**
     * Задача таймера - обновление списка доступных сетей.
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
