package name.marinchenko.lorryvision.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import name.marinchenko.lorryvision.activities.main.MainActivity;
import name.marinchenko.lorryvision.util.net.Net;

import static name.marinchenko.lorryvision.services.NetScanService.MSG_SCANS;

/**
 * Service tracking lorry network signal level.
 */

public class NetLevelService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private static class IncomingHandler extends Handler {
        private final NetLevelService netLevelService;

        public IncomingHandler(NetLevelService netLevelService) {
            this.netLevelService = new WeakReference<>(netLevelService).get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCANS:

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
