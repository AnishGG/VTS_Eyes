package ssadteam5.vtsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private String token;
    private String[] menu;
    private String email;
    private String tenant;

    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState == null) {
            setContentView(R.layout.activity_drawer);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchMap();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            email = getIntent().getExtras().getString("email");
            tenant = getIntent().getExtras().getString("tenant");

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu navMenu = navigationView.getMenu();
            View hview = navigationView.getHeaderView(0);
            TextView navtenant = (TextView) hview.findViewById(R.id.tenantName);
            TextView navemail = (TextView) hview.findViewById(R.id.emailname);

            navtenant.setText(tenant);
            navemail.setText(email);
            menu = getIntent().getExtras().getStringArray("menu");
            for (int i = 0; i < menu.length; i++) {
                navMenu.add(Menu.NONE, i, i, menu[i]);
            }
            navigationView.setNavigationItemSelectedListener(this);

            token = getIntent().getExtras().getString("token");
            menu = getIntent().getExtras().getStringArray("menu");

            deviceFragment();
//        }
//        else{
//            Log.d("else mei gya", "wow");
//            deviceFragment();
//        }
    }
    private void launchMap()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("token",token);
        startActivity(intent);
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.commit();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void deviceFragment()
    {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("token",token);
        fragment = new DeviceList();
        fragment.setArguments(bundle);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, fragment);
        tx.commit();
        return ;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
     /**   if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        if(menu[id].equals("Devices")){
            deviceFragment();

        }
        else if(menu[id].equals("Dashboard")){
            Log.d("Nice", "activity launched");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
