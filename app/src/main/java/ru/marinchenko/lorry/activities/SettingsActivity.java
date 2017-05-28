package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.services.WifiAgent;

import static ru.marinchenko.lorry.services.WifiAgent.AUTO_CONNECT;
import static ru.marinchenko.lorry.services.WifiAgent.AUTO_UPDATE;

public class SettingsActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String PREF_AUTOCONNECT = "pref_autoconnect";
    public final static String PREF_AUTOUPDATE = "pref_autoupdate";
    public final static String PREF_MANUPDATE = "pref_manupdate";
    public final static String PREF_TIMERUPDATE = "pref_timerupdate";
    public final static String PREF_TIMERUPDATE_VAL = "pref_timerupdate_val";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTimer();
        refresh();
    }

    private void initTimer() {
        LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inf.inflate(R.layout.update_mode_pref, null);
        SeekBar bar = (SeekBar) view.findViewById(R.id.seekbar_update);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        bar.setProgress(prefs.getInt(PREF_TIMERUPDATE_VAL, 0));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTimer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateTimer(int val){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_TIMERUPDATE_VAL, val);
        editor.apply();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void refresh() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_AUTOCONNECT)) {
            Intent autoConnection = new Intent(this, WifiAgent.class);
            autoConnection.setAction(AUTO_CONNECT);
            autoConnection.putExtra(AUTO_CONNECT, sharedPreferences.getBoolean(key, false));
            startService(autoConnection);

        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Intent autoUpdating = new Intent(this, WifiAgent.class);
            autoUpdating.setAction(AUTO_UPDATE);

            if(sharedPreferences.getBoolean(key, false)) {
                if (key.equals(PREF_AUTOUPDATE)) {
                    autoUpdating.putExtra(AUTO_UPDATE, true);

                    editor.putBoolean(PREF_MANUPDATE, false);
                    editor.putBoolean(PREF_TIMERUPDATE, false);
                }

                if (key.equals(PREF_MANUPDATE)) {
                    autoUpdating.putExtra(AUTO_UPDATE, false);

                    editor.putBoolean(PREF_AUTOUPDATE, false);
                    editor.putBoolean(PREF_TIMERUPDATE, false);
                }

                if (key.equals(PREF_TIMERUPDATE)) {
                    autoUpdating.putExtra(AUTO_UPDATE, false);

                    editor.putBoolean(PREF_AUTOUPDATE, false);
                    editor.putBoolean(PREF_MANUPDATE, false);
                }

            } else if(!sharedPreferences.getBoolean(PREF_AUTOUPDATE, false) &&
                    !sharedPreferences.getBoolean(PREF_MANUPDATE, false) &&
                    !sharedPreferences.getBoolean(PREF_TIMERUPDATE, false)) {
                editor.putBoolean(key, true);
            }

            refresh();
            editor.apply();
            startService(autoUpdating);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}




