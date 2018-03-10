package name.marinchenko.lorryvision.activities.main;

import android.os.Bundle;
import android.view.View;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.util.Initializer;


public class VideoActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Initializer.Video.init(this);
    }

    public void onButtonExitClick(View view) {
        onBackPressed();
    }
}
