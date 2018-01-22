package name.marinchenko.lorryvision.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import name.marinchenko.lorryvision.BuildConfig;
import name.marinchenko.lorryvision.R;

public class MainActivity
        extends ToolbarActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
     * Processing toolbar item selection
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Processing sidebar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_home:
                onBackPressed();
                break;

            case R.id.drawer_item_about:
                final Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.drawer_item_instruction:
                final Intent instructionIntent = new Intent(this, InstructionActivity.class);
                startActivity(instructionIntent);
                break;

            case R.id.drawer_item_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
        //final DrawerLayout drawer = findViewById(R.id.activity_main);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
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
        initDrawer();
        //initNetlist();
    }

    /**
     * Initialising sidebar
     */
    private void initDrawer() {
        final DrawerLayout drawer = findViewById(R.id.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar_main);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.drawer_nav);
        navigationView.setNavigationItemSelectedListener(this);
        initVersion(navigationView);
    }

    /**
     * Initialising sidebar version textView
     * @param view navigation view of sidebar
     */
    private void initVersion(final NavigationView view) {
        TextView version = view.getHeaderView(0).findViewById(R.id.home_header_version);
        version.setText(String.format(
                "%s %s",
                getString(R.string.version),
                BuildConfig.VERSION_NAME
        ));
    }

    private void initNetlist(final String[] nets) {
        final ListView netlist = findViewById(R.id.listView_netList);
        //netlist.setAdapter(new NetlistAdapter());

        // Stub
        netlist.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nets));
    }


    /*
     * Public methods
     */

    int counter = 0;
    final static String nets3[] = new String[]{"Net 1", "Net 2", "Net 3"};
    final static String nets6[] = new String[]{"Net 1", "Net 2", "Net 3", "Net 4", "Net 5", "Net 6"};
    final static String nets9[] = new String[]{"Net 1", "Net 2", "Net 3", "Net 4", "Net 5", "Net 6",
            "Net 7", "Net 8", "Net 9"};
    final static String nets12[] = new String[]{"Net 1", "Net 2", "Net 3", "Net 4", "Net 5", "Net 6",
            "Net 7", "Net 8", "Net 9", "Net 10", "Net 11", "Net 12"};
    final ArrayList<String[]> arrayList = new ArrayList<>();

    /**
     * Click update nets button
     * @param view button
     */
    public void onButtonUpdateClick(final View view) {
        Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();

        // Stub
        if (counter == 4) counter = 0;
        arrayList.add(nets3);
        arrayList.add(nets6);
        arrayList.add(nets9);
        arrayList.add(nets12);
        initNetlist(arrayList.get(counter++));
    }
}
