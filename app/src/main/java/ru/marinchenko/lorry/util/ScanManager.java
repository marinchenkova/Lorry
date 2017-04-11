package ru.marinchenko.lorry.util;

import java.util.Timer;
import java.util.TimerTask;

import ru.marinchenko.lorry.MainActivity;
import ru.marinchenko.lorry.R;

/**
 * Обновление списка сетей по таймеру.
 */
public class ScanManager {

    private Timer timer = new Timer();
    private UpdateTimerTask update = new UpdateTimerTask();
    private MainActivity activity;
    private long updateTime;

    public ScanManager(MainActivity mainActivity, int upTime){
        activity = mainActivity;
        updateTime = upTime;
        timer.schedule(update, 0, updateTime);
    }

    public void setUpdateTime(int sec){
        updateTime = 1000*sec;
        timer.cancel();
        timer.schedule(update, 0, updateTime);
    }

    private class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            activity.updateNets(activity.findViewById(R.id.button_update));
        }
    }
}
