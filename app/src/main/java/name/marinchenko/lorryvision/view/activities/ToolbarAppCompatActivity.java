package name.marinchenko.lorryvision.view.activities;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import name.marinchenko.lorryvision.R;

public abstract class ToolbarAppCompatActivity extends AppCompatActivity {

    public void initToolbar(@IdRes final int toolbarId,
                               @StringRes final int titleId,
                               final boolean up) {
        final Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(titleId);
        ab.setDisplayHomeAsUpEnabled(up);
        ab.setDisplayShowHomeEnabled(up);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
