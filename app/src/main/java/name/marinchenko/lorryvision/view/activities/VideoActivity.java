package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;
import android.view.View;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class VideoActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ActivityInitializer.Video.init(this);
    }

    public void onButtonExitClick(View view) {
        onBackPressed();
    }
}
