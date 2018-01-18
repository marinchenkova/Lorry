package name.marinchenko.lorryvision.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public abstract class ToolbarActivity extends AppCompatActivity {

    protected void initToolbar(final int toolbarId,
                               final int titleId,
                               final boolean up) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(titleId);
        ab.setDisplayHomeAsUpEnabled(up);
    }
}
