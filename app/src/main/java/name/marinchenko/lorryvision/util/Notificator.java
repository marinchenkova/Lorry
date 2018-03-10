package name.marinchenko.lorryvision.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.main.MainActivity;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;

import static android.app.Notification.DEFAULT_VIBRATE;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_JUMP;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_NETFOUND;

/**
 * Static methods for creating and showing notifications.
 */

public class Notificator {

    public static void notifyNetDetected(final Context context) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (appIsMinimized()) {
                            if (notificationAllowed(context)) {
                                showNotification(context, createNotification(context));
                                screenOn(context);
                            }

                            if (jumpToAppAllowed(context)) jumpToApp();

                        } else jumpToMainActivity();

                    }
                }
        );
    }

    private static void jumpToApp() {}

    private static void jumpToMainActivity() {}

    private static boolean appIsMinimized() { return true; }



    private static boolean notificationAllowed(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_NETFOUND, true);
    }

    private static boolean jumpToAppAllowed(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_JUMP, true);
    }

    private static void showNotification(final Context context,
                                         final Notification notification) {
        final NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(0, notification);
        }
    }

    private static Notification createNotification(final Context context) {
        final Intent intent = new Intent(
                context.getApplicationContext(),
                MainActivity.class
        );

        final PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                0
        );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context.getApplicationContext(),
                "notify_001"
        );

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Style big text");
        bigText.setBigContentTitle("Style big content title");
        bigText.setSummaryText("Style summary text");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Content title");
        mBuilder.setContentText("Content text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setDefaults(DEFAULT_VIBRATE);

        return mBuilder.build();
    }

    private static void screenOn(final Context context) {
        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.ON_AFTER_RELEASE,
                    "MyLock"
            );
            wakeLock.acquire(0);
            PowerManager.WakeLock wakeLockCpu = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "MyCpuLock"
            );

            wakeLockCpu.acquire(0);
        }
    }
}
