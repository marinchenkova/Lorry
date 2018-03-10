package name.marinchenko.lorryvision.util;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.main.MainActivity;
import name.marinchenko.lorryvision.util.threading.DefaultExecutorSupplier;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static android.app.Notification.DEFAULT_ALL;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_MAIN;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_JUMP;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_NOTIFICATION_ALLOWED;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_SOUND;

/**
 * Static methods for creating and showing notifications.
 */

public class Notificator {

    private static final int NOTIFICATION_NET_FOUND_ID = 100;
    private static final int NOTIFICATION_NET_FOUND_JUMP_DELAY = 1000;

    private static final String NOTIFICATION_CHANNEL_ID = "lorry_vision_notification";
    private static final String NOTIFICATION_CHANNEL_TITLE = "LorryVision notification";

    private static final String TAG_WAKELOCK = "tag_wakelock";
    private static final String TAG_WAKELOCK_CPU = "tag_wakelock_cpu";

    public static void notifyNetDetected(final Context context) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        if (Initializer.isAppBackground(context)) {
                            if (notificationAllowed(context)) {
                                showNotification(context, createNotification(context));
                                final boolean on = screenOn(context);

                                if (jumpToAppAllowed(context)) {
                                    if (!on || !isLocked(context))
                                        jumpToApp(context);
                                }
                            }

                        } else jumpToMainActivity(context);

                    }
                }
        );
    }

    public static void removeNetDetectedNotification(final Context context) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final NotificationManager mNotificationManager = (NotificationManager)
                                context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (mNotificationManager != null) {
                            mNotificationManager.cancel(NOTIFICATION_NET_FOUND_ID);
                        }
                    }
                }
        );
    }

    private static void jumpToApp(final Context context) {
        final Timer timer = new Timer();
        final TimerTask jumpTask = new TimerTask() {
            @Override
            public void run() {
                final PackageManager manager = context.getPackageManager();
                try {
                    Intent intent = manager.getLaunchIntentForPackage(
                            context.getApplicationContext().getPackageName()
                    );

                    if (intent == null) return;


                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {}
            }
        };

        timer.schedule(jumpTask, NOTIFICATION_NET_FOUND_JUMP_DELAY);
    }

    private static void jumpToMainActivity(final Context context) {
        if (!Initializer.isActivityForeground(context, PREF_KEY_FOREGROUND_MAIN)) {
            ToastThread.postToastMessage(context, "mainActivity paused", Toast.LENGTH_SHORT);

            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mainActivityIntent);
        }
    }

    private static boolean isLocked(final Context context) {
        final KeyguardManager keyguardManager = (KeyguardManager)
                context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager != null && keyguardManager.inKeyguardRestrictedInputMode();
    }

    private static boolean notificationAllowed(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_NOTIFICATION_ALLOWED, true);
    }

    private static boolean jumpToAppAllowed(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_JUMP, true);
    }

    private static boolean soundAllowed(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_SOUND, true);
    }

    private static void showNotification(final Context context,
                                         final Notification notification) {
        final NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_TITLE,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_NET_FOUND_ID, notification);
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
                NOTIFICATION_CHANNEL_ID
        );

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(context.getString(R.string.notif_net_found_content_title));
        mBuilder.setContentText(context.getString(R.string.notif_net_found_content_text));
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        if (soundAllowed(context)) mBuilder.setDefaults(DEFAULT_ALL);

        return mBuilder.build();
    }

    private static boolean screenOn(final Context context) {
        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.ON_AFTER_RELEASE,
                    TAG_WAKELOCK
            );
            wakeLock.acquire(0);
            PowerManager.WakeLock wakeLockCpu = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    TAG_WAKELOCK_CPU
            );

            wakeLockCpu.acquire(0);
            return false;
        }
        return true;
    }
}
