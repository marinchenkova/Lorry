package name.marinchenko.lorryvision.view.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.demoTest.TestBase;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;
import name.marinchenko.lorryvision.view.util.net.NetlistAdapter;

public class MainActivity
        extends ToolbarAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AdapterView.OnItemClickListener {


    private NetlistAdapter netlistAdapter;

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

        ActivityInitializer.Main.init(this);
        this.netlistAdapter = ActivityInitializer.Main.initNetlist(this);
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
        closeDrawer(false);
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

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) closeDrawer(true);
        else super.onBackPressed();
    }

    /**
     * Creating menu for toolbar
     * @param menu main menu
     * @return if created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_popup, menu);
        return true;
    }

    /**
     * Processing toolbar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_popup_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                return true;

            case R.id.toolbar_popup_about:
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            case R.id.toolbar_popup_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.toolbar_popup_website:
                //TODO website
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listening netlist events
     * @param adapterView parent view
     * @param view current list element
     * @param i number of the element in the list
     * @param l id of the element
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent videoIntent = new Intent(this, VideoActivity.class);
        startActivity(videoIntent);
    }

    /**
     * Processing sidebar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            // Group home
            case R.id.drawer_item_home:
                onBackPressed();
                break;

            // Group main
            case R.id.drawer_item_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                break;

            case R.id.drawer_item_about:
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.drawer_item_license:
                final Intent licenseIntent = new Intent(this, LicenseActivity.class);
                startActivity(licenseIntent);
                break;

            case R.id.drawer_item_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            // Group help
            case R.id.drawer_item_website:
                //TODO website
                break;

            case R.id.drawer_item_feedback:
                final Intent feedbackIntent = new Intent(this, FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;

            case R.id.drawer_item_bug:
                final Intent bugIntent = new Intent(this, BugActivity.class);
                startActivity(bugIntent);
                break;
        }
        return true;
    }

    /**
     * Updating resources when configuration changes
     * @param newConfig new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /*
     * Private methods
     */

    private boolean isDrawerOpen() {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    private void closeDrawer(final boolean animate) {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START, animate);
    }

    /*
     * Public methods
     */

    /**
     * Click update nets button
     * @param view button
     */
    public void onButtonUpdateClick(final View view) {
        this.netlistAdapter.update(TestBase.getNetlistForListViewTest());
        this.netlistAdapter.notifyDataSetChanged();
    }


}
