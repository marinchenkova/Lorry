package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;

public class SettingsActivity extends ToolbarActivity{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        initToolbar(
                R.id.toolbar_settings,
                R.string.activity_settings,
                true
        );
    }

}
