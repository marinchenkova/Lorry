package name.marinchenko.lorryvision.view.util;

import android.app.Fragment;
import android.os.Bundle;


/**
 * RetainedFragment is used for saving data when activity configuration changes.
 */

public class RetainedFragment extends Fragment {

    private SavingBundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void saveBundle(final SavingBundle bundle) {
        this.bundle = bundle;
    }

    public SavingBundle getBundle() {
        return this.bundle;
    }
}
