package ssadteam5.vtsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ReportsFragment extends Fragment
{
    public static ReportsFragment newInstance()
    {
        ReportsFragment r = new ReportsFragment();
        return r;
    }

    View view;
    private String token;
    private String response;
    private FetchDevNo mFetchTask;
    private Spinner spinner;
    Context cont;
    List<String> list = new ArrayList<String>();
    private String[] vehicle_list;

    private TextView tvDisplayDate1;
    private TextView tvDisplayDate2;
    private Button btnChangeDate1;
    private Button btnChangeDate2;
    private Button btnsub;

    private int year;
    private int month;
    private int day;
    Boolean flag1 = false;
    Boolean flag2 = false;
    private String startdate = "";
    private String enddate = "";
    private String vehicle = "";
    private String spres = "";
    static final int DATE_DIALOG_ID = 999;
    UserData userData;
    private StringBuilder sdate;Bundle bund;
    ViewPager viewPager;PagerAdapter adapter;TabLayout tabLayout;
    //    StringBuffer sb = new StringBuffer();
    String sb = "";

//    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("started", "reportfrag");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        token = getArguments().getString("token");
        userData = new UserData(getActivity().getApplicationContext());
        Log.d("tokeni", token);
        tabLayout=(TabLayout)view.findViewById(R.id.tabl);
        tabLayout.addTab(tabLayout.newTab().setText("Trip report"));
        tabLayout.addTab(tabLayout.newTab().setText("Idle report"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //bund =new Bundle();
        //bund.putString("kes","keshav");

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(),bund);
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

        cont = getActivity();
        mFetchTask = new FetchDevNo(token);
        mFetchTask.execute((Void) null);
        setCurrentDateOnView();
        addListenerOnButton();
        btnsub = (Button) view.findViewById(R.id.btnsub);
        btnsub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showTables();
            }
        });
        return view;
    }

    private void showTables(){
        vehicle = spinner.getSelectedItem().toString();
        ReportsInfo mRepTask = new ReportsInfo(vehicle, startdate, enddate, token);

            mRepTask.execute((Void) null);


        Log.d("ok", "ready");
    }

    public void setCurrentDateOnView() {

        tvDisplayDate1 = (TextView) view.findViewById(R.id.e1);
        tvDisplayDate2 = (TextView) view.findViewById(R.id.e2);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        if((month + 1)/10 == 0){
            if(day/10 == 0){
                sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
            }
            else{
                sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
            }
        }
        else{
            if(day/10 == 0){
                sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
            }
            else{
                sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
            }
        }

        // set current date into textview
        tvDisplayDate1.setText(sdate);
        tvDisplayDate2.setText(sdate);

        startdate = tvDisplayDate1.getText().toString();
        enddate = tvDisplayDate2.getText().toString();
        Log.d("got", startdate);
        Log.d("got2", enddate);

//         set current date into datepicker
//        tvDisplayDate1.setText(null);
//        tvDisplayDate2.setText(null);

    }

    public void addListenerOnButton() {

        btnChangeDate1 = (Button) view.findViewById(R.id.btnChangeDate1);
        btnChangeDate2 = (Button) view.findViewById(R.id.btnChangeDate2);


        btnChangeDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag1 = true;
                flag2 = false;
                onCreateDialog(DATE_DIALOG_ID).show();
            }


        });
        btnChangeDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag2 = true;
                flag1 = false;
                onCreateDialog(DATE_DIALOG_ID).show();
//                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(getActivity(), datePickerListener,
                        year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            if (flag1) {
                if((month + 1)/10 == 0){
                    if(day/10 == 0){
                        sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
                    }
                    else{
                        sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
                    }
                }
                else{
                    if(day/10 == 0){
                        sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
                    }
                    else{
                        sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
                    }
                }
                tvDisplayDate1.setText(sdate);
            }
            if (flag2) {
                if((month + 1)/10 == 0){
                    if(day/10 == 0){
                        sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
                    }
                    else{
                        sdate=new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
                    }
                }
                else{
                    if(day/10 == 0){
                        sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
                    }
                    else{
                        sdate=new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
                    }
                }
                tvDisplayDate2.setText(sdate);
            }
            startdate = tvDisplayDate1.getText().toString();
            enddate = tvDisplayDate2.getText().toString();
            flag1 = false;
            flag2 = false;
            Log.d("got1", startdate);
            Log.d("got2", enddate);
        }
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("yolo", "yolo");
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reports");
    }

    public class FetchDevNo extends AsyncTask<Void, Void, Boolean> {
        private final String mToken;

        FetchDevNo(String token) {
            mToken = token;
        }

        @Override
        protected void onPreExecute() {
            Log.d("something", "something");
            spinner = (Spinner) view.findViewById(R.id.spinner);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(!userData.isDataFetched())
                {
                    userData.fetchData();

                }
                String response = userData.getResponse().get(UserData.KEY_RESPONSE);
                JSONObject obj = new JSONObject(response);
                JSONArray arr = obj.getJSONArray("deviceDTOS");
                Log.d("hellodakshsize", "" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject ob = arr.getJSONObject(i);
                        JSONObject hello = new JSONObject(ob.getString("vehicleDetailsDO"));
                        String number = hello.getString("vehicleNumber");
                        Log.d("dakshhello", number);
                        list.add(number);
                    } catch (Exception e) {
                        JSONObject obn = arr.getJSONObject(i);
                        String name = obn.getString("name");
                        Log.d("nameprint", name);
                        list.add(name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(final Boolean success) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(dataAdapter);
//            spres = (String) spinner.getItemAtPosition(0);
        }
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
                Log.d("json",jsonObject.toString());
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
                int count=0;
                BufferedReader ini = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while((temp=ini.readLine())!=null) {
                    count+=1;
                    Log.d("in-while", String.valueOf(count));
                    response += temp;
                }
                int counter=0;
                Log.d("response",response);


//                    JSONArray jsonArray = new JSONArray(response);
////                    for(int i=0;i<jsonArray.length();i++)
////                    {
////                        JSONObject ob = jsonArray.getJSONObject(i);
////                        Log.d("test",ob.toString());
////                        counter++;
////                        Log.d("counter", String.valueOf(counter));
////                    }
//                }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;

        }

        @Override
        protected void onPostExecute(String response){

            bund=new Bundle();
            bund.putString("resp",response);
            Log.d("resp",response);
            //viewPager.getAdapter().notifyDataSetChanged();
            adapter = new PagerAdapter
                    (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(),bund);
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
