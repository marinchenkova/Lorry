package name.marinchenko.lorryvision.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import name.marinchenko.lorryvision.R;

public class MainActivity extends ToolbarActivity {


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
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            case R.id.toolbar_popup_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                return true;

            case R.id.toolbar_popup_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case android.R.id.home:
                Toast.makeText(this, "Home sweet home", Toast.LENGTH_SHORT).show();
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
        initToolbar(
                R.id.toolbar_main,
                R.string.app_name,
                false
        );
    }


}
