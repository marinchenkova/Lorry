package name.marinchenko.lorryvision.util.debug;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Valentin on 07.03.2018.
 */

public class ToastHelper {
    public static void postToastMessage(final Context context,
                                        final String message,
                                        final int lenght) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, lenght).show();
            }
        });
    }
}
