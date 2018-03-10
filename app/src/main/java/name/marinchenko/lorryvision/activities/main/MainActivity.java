package name.marinchenko.lorryvision.activities.main;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.activities.info.AboutActivity;
import name.marinchenko.lorryvision.activities.info.InstructionActivity;
import name.marinchenko.lorryvision.activities.info.LicenseActivity;
import name.marinchenko.lorryvision.activities.web.FeedbackActivity;
import name.marinchenko.lorryvision.services.ConnectService;
import name.marinchenko.lorryvision.services.NetScanService;
import name.marinchenko.lorryvision.util.Initializer;
import name.marinchenko.lorryvision.util.debug.LoginDialog;
import name.marinchenko.lorryvision.util.debug.NetStore;
import name.marinchenko.lorryvision.util.net.Net;
import name.marinchenko.lorryvision.util.net.NetlistAdapter;
import name.marinchenko.lorryvision.util.threading.ToastThread;

import static android.app.Notification.DEFAULT_VIBRATE;
import static android.support.v4.app.NotificationCompat.CATEGORY_ALARM;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;
import static name.marinchenko.lorryvision.services.ConnectService.ACTION_CONNECTING;
import static name.marinchenko.lorryvision.services.ConnectService.EXTRA_SSID;
import static name.marinchenko.lorryvision.services.NetScanService.ACTION_SCAN_SINGLE;
import static name.marinchenko.lorryvision.services.NetScanService.MSG_LORRIES_DETECTED;
import static name.marinchenko.lorryvision.services.NetScanService.MSG_SCANS;
import static name.marinchenko.lorryvision.services.NetScanService.MSG_СONNECT_START;

public class MainActivity
        extends ToolbarAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AdapterView.OnItemClickListener,
                   AdapterView.OnItemLongClickListener {

    private NetlistAdapter netlistAdapter;
    private Messenger mActivityMessenger;
    private List<Net> nets = new ArrayList<>();
    private boolean lorriesDetected = false;


    /*
     * Overridden methods
     */

    /**
     * Activity lifecycle: activity launched
     * @param savedInstanceState saved state of the activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.netlistAdapter = Initializer.Main.initNetlist(this);
        this.mActivityMessenger = new Messenger(new IncomingHandler(this));

        Initializer.Main.init(this);
    }

    /**
     * Activity lifecycle: after onStart() or onPause() when user returns to the activity
     */
    @Override
    protected void onResume() {
        closeDrawer(false);

        super.onResume();

        Initializer.Main.initAutoconnectCheckbox(this);
        Initializer.Main.initAutoUpdate(this, this.lorriesDetected);
    }

    /**
     * Activity lifecycle: after onStop() when user navigates to the activity
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Activity lifecycle: after onPause() when activity is no longer visible
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Activity lifecycle: after onStop() when activity is finishing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) closeDrawer(true);
        else super.onBackPressed();
    }

    /**
     * Creating menu for toolbar
     * @param menu main menu
     * @return if created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_popup, menu);
        return true;
    }

    /**
     * Processing toolbar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_popup_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                return true;

            case R.id.toolbar_popup_about:
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            case R.id.toolbar_popup_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listening netlist events
     * @param adapterView parent view
     * @param view current list element
     * @param i number of the element in the list
     * @param l id of the element
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Net net = (Net) this.netlistAdapter.getItem(i);
        final Intent connectingIntent = new Intent(this, NetScanService.class);

        connectingIntent.setAction(ACTION_CONNECTING);
        connectingIntent.putExtra(EXTRA_SSID, net.getSsid());
        startService(connectingIntent);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final LoginDialog dialog = new LoginDialog();
        final Bundle bundle = new Bundle();

        final String id = ((Net) adapterView.getItemAtPosition(i)).getSsid();

        bundle.putString(NetStore.KEY_ID, id);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "login");

        return true;
    }

    /**
     * Processing sidebar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            // Group home
            case R.id.drawer_item_home:
                onBackPressed();
                break;

            // Group main
            case R.id.drawer_item_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                break;

            case R.id.drawer_item_about:
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.drawer_item_license:
                final Intent licenseIntent = new Intent(this, LicenseActivity.class);
                startActivity(licenseIntent);
                break;

            case R.id.drawer_item_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            // Group help
            case R.id.drawer_item_website:
                //TODO website
                break;

            case R.id.drawer_item_feedback:
                final Intent feedbackIntent = new Intent(this, FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
        }
        return true;
    }

    /**
     * Updating resources when configuration changes
     * @param newConfig new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /*
     * Private methods
     */

    private boolean isDrawerOpen() {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    private void closeDrawer(final boolean animate) {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START, animate);
    }


    /*
     * Own methods
     */

    /**
     * Click update nets button
     * @param view button
     */
    public void onButtonUpdateClick(final View view) {
        requestScanResults();
        updateNetlist(this.nets);
        //createNotification();
    }

    public void onCheckboxAutoconnectClick(View view) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean autoConnect = ((CheckBox) view).isChecked();

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SettingsFragment.PREF_KEY_AUTOCONNECT, autoConnect);
        editor.apply();

        Initializer.initAutoConnect(this);
    }

    public Messenger getActivityMessenger() { return this.mActivityMessenger; }

    private void updateNetlist(final List<Net> newList) {
        this.netlistAdapter.update(this, newList);
        this.netlistAdapter.notifyDataSetChanged();
    }

    private void requestScanResults() {
        final Intent scanRequest = new Intent(this, NetScanService.class);
        scanRequest.setAction(ACTION_SCAN_SINGLE);
        startService(scanRequest);
    }


    private static class IncomingHandler extends Handler {
        private final MainActivity mainActivity;

        public IncomingHandler(MainActivity mainActivity) {
            this.mainActivity = new WeakReference<>(mainActivity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCANS:
                    mainActivity.updateNetlist((List<Net>) msg.obj);
                    mainActivity.lorriesDetected = (msg.arg1 == MSG_LORRIES_DETECTED);
                    Initializer.Main.initAutoUpdate(mainActivity, mainActivity.lorriesDetected);
                    break;

                case MSG_СONNECT_START:


                    ToastThread.postToastMessage(
                            mainActivity,
                            "Connect started",
                            Toast.LENGTH_SHORT
                    );
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
