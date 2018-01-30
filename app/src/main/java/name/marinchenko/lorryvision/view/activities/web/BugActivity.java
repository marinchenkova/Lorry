package name.marinchenko.lorryvision.view.activities.web;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class BugActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug);

        ActivityInitializer.Bug.init(this);
    }
}
