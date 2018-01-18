package name.marinchenko.lorryvision.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import name.marinchenko.lorryvision.R;

public class MainActivity extends AppCompatActivity {


    /*
     * Overridden methods
     */

    /**
     * Activity lifecycle: activity launched
     * @param savedInstanceState saved state of the activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * Activity lifecycle: after onCreate() or onRestart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Activity lifecycle: after onStart() or onPause() when user returns to the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Activity lifecycle: after onResume() when another activity comes into the foreground
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Activity lifecycle: after onStop() when user navigates to the activity
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Activity lifecycle: after onPause() when activity is no longer visible
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Activity lifecycle: after onStop() when activity is finishing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Creating menu for toolbar
     * @param menu main menu
     * @return if created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Processing item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_popup_about:
                //TODO go to About Activity
                return true;

            case R.id.toolbar_popup_instruction:
                //TODO go to Instruction Activity
                return true;

            case R.id.toolbar_popup_settings:
                //TODO go to Settings Activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * Private methods
     */

    /**
     * Initialising with onCreate()
     */
    private void init() {
        initToolbar();
    }

    /**
     * Setting toolbar as actionbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }
}
