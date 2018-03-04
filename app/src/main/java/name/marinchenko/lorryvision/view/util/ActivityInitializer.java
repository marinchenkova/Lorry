package name.marinchenko.lorryvision.view.util;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import name.marinchenko.lorryvision.BuildConfig;
import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.web.*;
import name.marinchenko.lorryvision.view.activities.info.*;
import name.marinchenko.lorryvision.view.activities.main.*;
import name.marinchenko.lorryvision.view.util.net.NetlistAdapter;
import name.marinchenko.lorryvision.view.util.threading.DefaultExecutorSupplier;


/**
 * ActivityInitializer includes static methods for activity initialising.
 */

public class ActivityInitializer {



    public static class Main {
        public static void init(final MainActivity mainActivity) {
            mainActivity.initToolbar(
                    R.id.activity_main_toolbar,
                    R.string.app_name,
                    false
            );
            initDrawer(mainActivity);
        }


        /**
         * Initialising sidebar
         * @param mainActivity MainActivity
         */
        private static void initDrawer(final MainActivity mainActivity) {
            final DrawerLayout drawer = mainActivity.findViewById(R.id.activity_main);
            final Toolbar toolbar = mainActivity.findViewById(R.id.activity_main_toolbar);
            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    mainActivity,
                    drawer,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close
            );

            drawer.addDrawerListener(toggle);
            toggle.syncState();

            final NavigationView navigationView = mainActivity.findViewById(R.id.drawer_nav);
            navigationView.setNavigationItemSelectedListener(mainActivity);
            initVersion(mainActivity, navigationView);
        }

        /**
         * Initialising sidebar version textView
         * @param mainActivity MainActivity
         * @param view navigation view of sidebar
         */
        private static void initVersion(final MainActivity mainActivity,
                                        final NavigationView view) {
            TextView version = view.getHeaderView(0).findViewById(R.id.drawer_textView_version);
            version.setText(String.format(
                    "%s %s",
                    mainActivity.getString(R.string.app_version),
                    BuildConfig.VERSION_NAME
            ));
        }

        /**
         * Initialising ListView Netlist
         * @param mainActivity MainActivity
         */
        public static NetlistAdapter initNetlist(final MainActivity mainActivity) {
            final ListView netlist = mainActivity.findViewById(R.id.netList_listView);
            final NetlistAdapter netlistAdapter = new NetlistAdapter(mainActivity);

            netlist.setAdapter(netlistAdapter);
            netlist.setOnItemClickListener(mainActivity);
            netlist.setOnItemLongClickListener(mainActivity);

            return netlistAdapter;
        }

        public static void initAutoconnectCheckbox(final MainActivity mainActivity) {
            final SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(mainActivity);
            ((CheckBox) mainActivity.findViewById(R.id.netList_checkbox_autoconnect)).
                    setChecked(pref.getBoolean(SettingsFragment.PREF_KEY_AUTOCONNECT, true));
        }
    }

    public static class Video {

        public static void init(final VideoActivity videoActivity) {
            videoActivity.initToolbar(
                    R.id.activity_video_toolbar,
                    R.string.activity_video,
                    true
            );
        }

    }

    public static class Instruction {
        public static void init(final InstructionActivity instructionActivity) {
            instructionActivity.initToolbar(
                    R.id.activity_instruction_toolbar,
                    R.string.activity_instruction,
                    true
            );
        }
    }

    public static class About {
        public static void init(final AboutActivity aboutActivity) {
            aboutActivity.initToolbar(
                    R.id.activity_about_toolbar,
                    R.string.activity_about,
                    true
            );
        }
    }

    public static class License {
        public static void init(final LicenseActivity licenseActivity) {
            licenseActivity.initToolbar(
                    R.id.activity_license_toolbar,
                    R.string.activity_license,
                    true
            );
        }
    }

    public static class Settings {

        public static void init(final SettingsActivity settingsActivity) {
            settingsActivity.initToolbar(
                    R.id.activity_settings_toolbar,
                    R.string.activity_settings,
                    true
            );
        }

    }

    public static class Feedback {
        public static void init(final FeedbackActivity feedbackActivity) {
            feedbackActivity.initToolbar(
                    R.id.activity_feedback_toolbar,
                    R.string.activity_feedback,
                    true
            );
        }

        public static Drawable getDisabledIcon(final FeedbackActivity feedbackActivity) {
            return ContextCompat.getDrawable(
                    feedbackActivity,
                    R.drawable.ic_action_send_msg_disabled
            );
        }

        public static Drawable getEnabledIcon(final FeedbackActivity feedbackActivity) {
            return ContextCompat.getDrawable(
                    feedbackActivity,
                    R.drawable.ic_action_send_msg_enabled
            );
        }
    }
}
