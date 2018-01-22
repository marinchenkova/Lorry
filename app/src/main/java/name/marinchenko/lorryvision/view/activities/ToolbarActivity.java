package name.marinchenko.lorryvision.view.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import name.marinchenko.lorryvision.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    protected void initToolbar(final int toolbarId,
                               final int titleId,
                               final boolean up) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(titleId);
        ab.setDisplayHomeAsUpEnabled(up);
        if (!up) toolbar.setNavigationIcon(R.drawable.toolbar_menu);
    }
}
