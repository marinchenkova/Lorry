package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;

public class AboutActivity extends ToolbarActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar(
                R.id.activity_about_toolbar,
                R.string.activity_about,
                true
        );
    }
}
