package name.marinchenko.lorryvision.activities.info;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.util.Initializer;

import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_ABOUT;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_MAIN;

public class AboutActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.prefActivityTag = PREF_KEY_FOREGROUND_ABOUT;
        Initializer.About.init(this);
    }
}
