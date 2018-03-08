package name.marinchenko.lorryvision.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Service providing connection to the network.
 */

public class ConnectService extends IntentService {

    private final static String CONSTRUCTOR = "connect_service";

    public ConnectService() {
        super(CONSTRUCTOR);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
