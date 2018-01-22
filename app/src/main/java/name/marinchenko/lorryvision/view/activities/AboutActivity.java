package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;

public class AboutActivity extends ToolbarActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar(
                R.id.toolbar_about,
                R.string.activity_about,
                true
        );
    }
}
