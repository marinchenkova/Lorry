package name.marinchenko.lorryvision.activities.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.util.Initializer;

/**
 * Settings fragment to use SharedPreferences.
 */

public class SettingsFragment
        extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String PREF_KEY_LANGUAGE = "pref_key_language";
    public final static String PREF_KEY_AUTOCONNECT = "pref_key_autoconnect";
    public final static String PREF_KEY_AUTOUPDATE = "pref_key_autoupdate";
    public final static String PREF_KEY_DISPLAY_GENERAL = "pref_key_display_general";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        onSharedPreferenceChanged(
                PreferenceManager.getDefaultSharedPreferences(getActivity()),
                PREF_KEY_LANGUAGE
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }

        Initializer.initNetScanService(getActivity().getApplicationContext());
    }
}
