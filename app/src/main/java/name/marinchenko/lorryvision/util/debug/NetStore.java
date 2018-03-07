package name.marinchenko.lorryvision.util.debug;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Network ID-Password storage.
 */

public class NetStore {

    public final static String KEY_ID = "key_id";

    public static void save(final Context context,
                            final String id,
                            final String password) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(id, password);
        editor.apply();
    }

    public static String getPassword(final Context context,
                                     final String id) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(id, "");
    }

    public static void deleteAll(final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
