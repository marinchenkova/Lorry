package name.marinchenko.lorryvision.view.util;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import name.marinchenko.lorryvision.BuildConfig;
import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.AboutActivity;
import name.marinchenko.lorryvision.view.activities.InstructionActivity;
import name.marinchenko.lorryvision.view.activities.MainActivity;
import name.marinchenko.lorryvision.view.activities.SettingsActivity;
import name.marinchenko.lorryvision.view.activities.VideoActivity;
import name.marinchenko.lorryvision.view.util.net.NetlistAdapter;

/**
 * ActivityInitializer includes static methods for activity initialising.
 */

public class ActivityInitializer {

    public static class Main {

        public static void init(final MainActivity main) {
            main.initToolbar(
                    R.id.activity_main_toolbar,
                    R.string.app_name,
                    false
            );
            initDrawer(main);
            initNetlist(main);
        }

        /**
         * Initialising sidebar
         * @param main MainActivity
         */
        private static void initDrawer(final MainActivity main) {
            final DrawerLayout drawer = main.findViewById(R.id.activity_main);
            final Toolbar toolbar = main.findViewById(R.id.activity_main_toolbar);
            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    main,
                    drawer,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close
            );

            drawer.addDrawerListener(toggle);
            toggle.syncState();

            final NavigationView navigationView = main.findViewById(R.id.drawer_nav);
            navigationView.setNavigationItemSelectedListener(main);
            initVersion(main, navigationView);
        }

        /**
         * Initialising sidebar version textView
         * @param main MainActivity
         * @param view navigation view of sidebar
         */
        private static void initVersion(final MainActivity main,
                                        final NavigationView view) {
            TextView version = view.getHeaderView(0).findViewById(R.id.drawer_textView_version);
            version.setText(String.format(
                    "%s %s",
                    main.getString(R.string.app_version),
                    BuildConfig.VERSION_NAME
            ));
        }

        /**
         * Initialising ListView Netlist
         * @param main MainActivity
         */
        private static void initNetlist(final MainActivity main) {
            final ListView netlist = main.findViewById(R.id.netList_listView);
            netlist.setAdapter(new NetlistAdapter(main));
            netlist.setOnItemClickListener(main);
        }
    }

    public static class Video {

        public static void init(final VideoActivity video) {
            video.initToolbar(
                    R.id.activity_video_toolbar,
                    R.string.activity_video,
                    true
            );
        }

    }

    public static class About {

        public static void init(final AboutActivity about) {
            about.initToolbar(
                    R.id.activity_about_toolbar,
                    R.string.activity_about,
                    true
            );
        }

    }

    public static class Instruction {

        public static void init(final InstructionActivity instruction) {
            instruction.initToolbar(
                    R.id.activity_instruction_toolbar,
                    R.string.activity_instruction,
                    true
            );
        }

    }

    public static class Settings {

        public static void init(final SettingsActivity settings) {
            settings.initToolbar(
                    R.id.activity_settings_toolbar,
                    R.string.activity_settings,
                    true
            );
        }

    }

}
