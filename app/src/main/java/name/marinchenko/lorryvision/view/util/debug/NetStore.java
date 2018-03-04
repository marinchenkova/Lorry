package name.marinchenko.lorryvision.view.util.debug;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


/**
 * Network ID-Password storage.
 */

public class NetStore {

    public final static String KEY_ID = "key_id";

    public static void save(final Activity activity,
                            final String id,
                            final String password) {
        final SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(id, password);
        editor.apply();
    }

    public static String getPassword(final Activity activity,
                                     final String id) {
        final SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(id, "");
    }

    public static void deleteAll(final Activity activity) {
        final SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

}
