package ssadteam5.vtsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import org.json.JSONArray;
import org.json.JSONException;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private String token;
    private String[] menu;
    private String email;
    private String tenant;

    UserSessionManager session;
    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        // First this activity will be opened and if user is logged in, then it is okay, else login activity will be displayed
        session = new UserSessionManager(getApplicationContext());
        // Check user login (this is the important point)
        // If User is not logged in , This will redirect user to LoginActivity
        // and finish current activity from activity stack.
        if(session.checkLogin())
        {
            Log.d("check","finish");
            finish();
        }
        else
        {
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
            email = session.getUserDetails().get(UserSessionManager.KEY_EMAIL);
            tenant = session.getUserDetails().get(UserSessionManager.KEY_TENANT);
            token = session.getUserDetails().get(UserSessionManager.KEY_TOKEN);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu navMenu = navigationView.getMenu();
            View hview = navigationView.getHeaderView(0);
            TextView navtenant = hview.findViewById(R.id.tenantName);
            TextView navemail = hview.findViewById(R.id.emailname);

            navtenant.setText(tenant);
            navemail.setText(email);

            menu = new String[4];
            menu[0] = "Dashboard";
            menu[1] = "Devices";
            menu[2] = "Reports";
            menu[3] = "Logout";
            for (int i = 0; i < menu.length-1; i++)
            {
                navMenu.add(Menu.NONE, i, i, menu[i]);
            }
            navMenu.add(Menu.CATEGORY_CONTAINER,menu.length -1,menu.length-1,menu[menu.length -1]);
            navigationView.setNavigationItemSelectedListener(this);
            if(savedInstanceState == null)
            {
                dashboardFragment();
            }
        }
    }
    private void launchMap()
    {
        Intent intent = new Intent(this, MapsActivity.class);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        if (item.getItemId() == R.id.action_logout)
        {
           logout();
        }

        return super.onOptionsItemSelected(item);
    }
    public void logout()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.commit();
        session.logoutUser();
        finishAfterTransition();
    }
    private void deviceListFragment()
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
    private void reportsFragment()
    {
        Fragment fragment = new ReportsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("token",token);
        fragment.setArguments(bundle);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, fragment);
        tx.commit();
        return ;
    }
    private void dashboardFragment()
    {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("token",token);
        fragment = new DashboardFragment();
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
        if(menu[id].equals("Devices"))
        {
            deviceListFragment();
        }
        else if(menu[id].equals("Dashboard"))
        {
            dashboardFragment();
        }
        else if(menu[id].equals("Reports"))
        {
            reportsFragment();
        }
        else if(menu[id].equals("Logout"))
        {
            logout();
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
