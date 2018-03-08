package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import name.marinchenko.lorryvision.util.threading.ToastThread;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends IntentService {

    private final static String CONSTRUCTOR = "connect_service";

    public final static String KEY_CONFIG = "key_config";
    public final static int STABLE_CONNECT_TIME = 5;
    public final static int STABLE_CONNECT_LEVEL = -80;

    public ConnectService() {
        super(CONSTRUCTOR);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ToastThread.postToastMessage(this, "Connecting", Toast.LENGTH_SHORT);
    }
}
