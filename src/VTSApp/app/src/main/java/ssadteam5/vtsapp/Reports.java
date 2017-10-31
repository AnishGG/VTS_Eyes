package ssadteam5.vtsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Reports extends AppCompatActivity {
    private Bundle bund;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private TabLayout tabLayout;
    private String response;
    private UserData userData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_reports);
        Bundle b = getIntent().getExtras();
//        String token = getArguments().getString("token");
//        String startdate = getArguments().getString("startdate");
//        String enddate = getArguments().getString("enddate");
//        String vehicle = getArguments().getString("vehicle");


        String token = b.getString("token");
        String startdate = b.getString("startdate");
        String enddate = b.getString("enddate");
        String vehicle = b.getString("vehicle");
        Log.d("Dates", startdate + " " + enddate);

        tabLayout = findViewById(R.id.tabl);
        tabLayout.addTab(tabLayout.newTab().setText("Trip report"));
        tabLayout.addTab(tabLayout.newTab().setText("Idle report"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        userData = new UserData(Reports.this.getApplicationContext());


        viewPager = findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), bund);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ReportsInfo mRepTask = new ReportsInfo(vehicle, startdate, enddate, token);
        mRepTask.execute((Void) null);
        }
    public class ReportsInfo extends AsyncTask<Void, Void, String> {

        private final String mVehicleNo;
        private final String mStartDate;
        private final String mEndDate;
        private final String mToken;

        ReportsInfo(String vehicleNo, String startDate, String endDate, String token) {
            mVehicleNo = vehicleNo;
            mStartDate = startDate + "T18:30:00Z";
            mEndDate = endDate + "T18:30:00Z";
            mToken = token;
        }
        private final ProgressDialog Dialog = new ProgressDialog(Reports.this);

        @Override
        protected void onPreExecute()
        {
            Dialog.setMessage("Loading");
            Dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            Log.d("Fragment", "Reports");
            if(!userData.isReportFetched(mStartDate, mEndDate, mVehicleNo))
                userData.fetchReports(mStartDate, mEndDate, mVehicleNo, mToken);
            response = userData.getReports().get(UserData.KEY_REPORTS);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            Dialog.dismiss();
            bund = new Bundle();
            bund.putString("resp", response);
            Log.d("resp", response);
            //viewPager.getAdapter().notifyDataSetChanged();
            adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount(), bund);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
    }
    }
