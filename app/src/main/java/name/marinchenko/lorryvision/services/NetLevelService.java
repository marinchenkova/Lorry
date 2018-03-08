package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import name.marinchenko.lorryvision.util.threading.ToastThread;


/**
 * Service tracking lorry network signal level.
 */

public class NetLevelService extends IntentService {

    private static final String CONSTRUCTOR = "net_level_service";

    public static final String ACTION_DETECT_START = "action_detect_start";
    public static final String ACTION_DETECT_UPDATE = "action_detect_update";
    public static final String ACTION_DETECT_STOP = "action_detect_stop";

    public NetLevelService() {
        super(CONSTRUCTOR);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            switch (intent.getAction() != null ? intent.getAction() : "") {
                case ACTION_DETECT_START:
                    ToastThread.postToastMessage(this, "start", Toast.LENGTH_SHORT);
                    break;

                case ACTION_DETECT_UPDATE:
                    ToastThread.postToastMessage(this, "update", Toast.LENGTH_SHORT);
                    break;

                case ACTION_DETECT_STOP:
                    ToastThread.postToastMessage(this, "stop", Toast.LENGTH_SHORT);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        ToastThread.postToastMessage(this, "onDestroy", Toast.LENGTH_SHORT);
        super.onDestroy();
    }
}
