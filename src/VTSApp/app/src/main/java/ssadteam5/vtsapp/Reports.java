package ssadteam5.vtsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

public class Reports extends Fragment {
    private Bundle bund;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private TabLayout tabLayout;
    private String response;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_reports, container, false);
        String token = getArguments().getString("token");
        String startdate = getArguments().getString("startdate");
        String enddate = getArguments().getString("enddate");
        String vehicle = getArguments().getString("vehicle");


        tabLayout = view.findViewById(R.id.tabl);
        tabLayout.addTab(tabLayout.newTab().setText("Trip report"));
        tabLayout.addTab(tabLayout.newTab().setText("Idle report"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = view.findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), bund);
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




            return view;
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
        private final ProgressDialog Dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            Dialog.setMessage("Loading");
            Dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection conn;
            try {
                response = "";
                JSONObject jsonObject = new JSONObject();
                JSONObject jo = new JSONObject();
                JSONObject jo2 = new JSONObject();
                jo.put("$gt", mStartDate);
                jo.put("$lt", mEndDate);
                jo2.put("$gt", "0.0000");
                jsonObject.put("DeviceId", mVehicleNo);
                jsonObject.put("GPSTimestamp", jo);
                jsonObject.put("Latitude", jo2);
                Log.d("json", jsonObject.toString());
                URL url = new URL("http://eyedentifyapps.com:8080/api/native/query/APAC_EYES_GPS?orderBy=GPSTimestamp/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + mToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonObject.toString());
                wr.close();
                int count = 0;
                BufferedReader ini = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while ((temp = ini.readLine()) != null) {
                    count += 1;
                    Log.d("in-while", String.valueOf(count));
                    response += temp;
                }
                int counter = 0;
                Log.d("response", response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), bund);
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
