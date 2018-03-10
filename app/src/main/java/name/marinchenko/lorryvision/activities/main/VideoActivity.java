package name.marinchenko.lorryvision.activities.main;

import android.os.Bundle;
import android.view.View;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.util.Initializer;

import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_MAIN;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_VIDEO;

public class VideoActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        this.prefActivityTag = PREF_KEY_FOREGROUND_VIDEO;
        Initializer.Video.init(this);
    }

    public void onButtonExitClick(View view) {
        onBackPressed();
    }
}
