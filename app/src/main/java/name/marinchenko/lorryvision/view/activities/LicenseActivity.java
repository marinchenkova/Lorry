package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class LicenseActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        ActivityInitializer.License.init(this);
    }
}
