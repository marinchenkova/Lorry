package name.marinchenko.lorryvision.activities;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import name.marinchenko.lorryvision.util.Initializer;
import name.marinchenko.lorryvision.util.Notificator;
import name.marinchenko.lorryvision.util.net.WifiAgent;
import name.marinchenko.lorryvision.util.threading.ToastThread;

public abstract class ToolbarAppCompatActivity extends AppCompatActivity {

    private BroadcastReceiver wifiReceiver = new WifiAgent.WifiReceiver();
    protected String prefActivityTag;

    public void initToolbar(@IdRes final int toolbarId,
                            @StringRes final int titleId,
                            final boolean up) {
        final Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(titleId);
        ab.setDisplayHomeAsUpEnabled(up);
        ab.setDisplayShowHomeEnabled(up);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WifiAgent.enableWifi(this, false, true);
        final IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(this.wifiReceiver, filter);
        Notificator.removeNetDetectedNotification(this);
        Initializer.initForeground(this, this.prefActivityTag, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Initializer.initForeground(this, this.prefActivityTag, false);
        try {
            unregisterReceiver(this.wifiReceiver);
        } catch (IllegalArgumentException e) {
            Log.w("MyLog", e.getMessage(), e);
        }
    }

    protected String getPrefActivityTag() { return this.prefActivityTag; }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Hide keyboard if focus is out of any EditText
     * @param event touch
     * @return super
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
