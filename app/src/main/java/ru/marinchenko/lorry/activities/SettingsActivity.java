package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.services.WifiAgent;

import static ru.marinchenko.lorry.R.styleable.View;
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

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        /*
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor.putInt(PREF_TIMERUPDATE_VAL, progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //SharedPreferences.Editor editor = sharedPreferences.edit();

        if (key.equals(PREF_AUTOCONNECT)) {
            Intent autoConnection = new Intent(this, WifiAgent.class);
            autoConnection.setAction(AUTO_CONNECT);
            autoConnection.putExtra(AUTO_CONNECT, sharedPreferences.getBoolean(key, false));
            startService(autoConnection);
        }
        if (key.equals(PREF_AUTOUPDATE)) {
            Boolean on = sharedPreferences.getBoolean(key, false);

            Intent autoConnection = new Intent(this, WifiAgent.class);
            autoConnection.setAction(AUTO_UPDATE);
            autoConnection.putExtra(AUTO_UPDATE, on);
            startService(autoConnection);
            /*
            editor.putBoolean(PREF_MANUPDATE, false);
            editor.putBoolean(PREF_TIMERUPDATE, false);
            editor.apply();*/
        }
        if (key.equals(PREF_MANUPDATE)) {
            Intent autoConnection = new Intent(this, WifiAgent.class);
            autoConnection.setAction(AUTO_UPDATE);
            autoConnection.putExtra(AUTO_UPDATE, false);
            startService(autoConnection);
            /*
            editor.putBoolean(PREF_MANUPDATE, false);
            editor.putBoolean(PREF_TIMERUPDATE, false);
            editor.apply();*/
        }
        if (key.equals(PREF_TIMERUPDATE)) {
            Intent autoConnection = new Intent(this, WifiAgent.class);
            autoConnection.setAction(AUTO_UPDATE);
            autoConnection.putExtra(AUTO_UPDATE, false);
            startService(autoConnection);
            /*
            editor.putBoolean(PREF_MANUPDATE, false);
            editor.putBoolean(PREF_TIMERUPDATE, false);
            editor.apply();*/
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




