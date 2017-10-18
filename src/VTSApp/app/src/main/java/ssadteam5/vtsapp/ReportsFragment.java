package ssadteam5.vtsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class ReportsFragment extends Fragment {
    public static ReportsFragment newInstance() {
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
    private String spres = "";
    static final int DATE_DIALOG_ID = 999;
    UserData userData;


//    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("started", "reportfrag");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        token = getArguments().getString("token");
        userData = new UserData(getActivity().getApplicationContext());
        Log.d("tokeni", token);
        cont = getActivity();
        mFetchTask = new FetchDevNo(token);
        mFetchTask.execute((Void) null);
        setCurrentDateOnView();
        addListenerOnButton();
        ReportsInfo mRepTask = new ReportsInfo("1", "2", "3", token);
        mRepTask.execute((Void) null);
        Log.d("ok", "ready");
        return view;
    }

    public void setCurrentDateOnView() {

        tvDisplayDate1 = (TextView) view.findViewById(R.id.e1);
        tvDisplayDate2 = (TextView) view.findViewById(R.id.e2);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate1.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(month + 1).append("-").append(day).append(" ")
        );
        tvDisplayDate2.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(month + 1).append("-").append(day).append(" ")
        );

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
        btnsub = (Button) view.findViewById(R.id.btnsub);

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
                tvDisplayDate1.setText(new StringBuilder().append(year)
                        .append("-").append(month + 1)
                        .append("-").append(day).append(" "));
                flag1 = false;
                flag2 = false;
            }
            if (flag2) {
                tvDisplayDate2.setText(new StringBuilder().append(year)
                        .append("-").append(month + 1)
                        .append("-").append(day).append(" "));
            }
            startdate = tvDisplayDate1.getText().toString();
            enddate = tvDisplayDate2.getText().toString();
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
            HttpURLConnection conn;
            try {
                if(!userData.isDataFetched())
                {
                    userData.fetchData();
                    Log.d("MyNameisVTS", "isthatso");
                }
                String response = userData.getResponse().get(UserData.KEY_RESPONSE);
                JSONObject obj = new JSONObject(response);
                JSONArray arr = obj.getJSONArray("deviceDTOS");
                Log.d("hellodakshsize", "" + arr.length());
                for (int i = 0; i < arr.length(); i++) {
//                    spinner.setOnItemSelectedListener(this);
                    try {
                        JSONObject ob = arr.getJSONObject(i);
                        JSONObject hello = new JSONObject(ob.getString("vehicleDetailsDO"));
                        String number = hello.getString("vehicleNumber");
                        Log.d("dakshhello", number);
                        list.add(number);
                    } catch (Exception e) {
//                        e.printStackTrace();
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

    public class ReportsInfo extends AsyncTask<Void, Void, Boolean> {

        private final String mVehicleNo;
        private final String mStartDate;
        private final String mEndDate;
        private final String mToken;

        ReportsInfo(String vehicleNo, String startDate, String endDate, String token) {
            mVehicleNo = "868325025608599";
            mStartDate = "2017-09-30";
            mEndDate = "2017-10-16";
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection conn;
            try {
                String response = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("deviceId", mVehicleNo);
                jsonObject.put("fromDate", mStartDate);
                jsonObject.put("toDate", mEndDate);
                URL url = new URL("http://eyedentifyapps.com:8080/api/report/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "/");
                conn.setRequestProperty("Authorization", "Bearer " + mToken);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonObject.toString());
                wr.close();
                InputStream in = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    response += current;
                }
                Log.d("respveh", response);
                JSONObject repObj = new JSONObject(response);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

//        @Override
//        protected void onPostExecute(final Boolean success)
//        {
//            mAuthTask = null;
//            showProgress(false);
//
//            if (success)
//            {
//                if(status.equals("SUCCESS"))
//                {
//                    launchDrawer();
//                    finish();
//                    //launchMap();
//                }
//                else if(status.equals("FAILURE"))
//                {
//                    if(errorCode.equals("3116"))
//                    {
//                        mEmailView.setError(errorMessage);
//                        mEmailView.requestFocus();
//                    }
//                    else if(errorCode.equals("4103"))
//                    {
//                        mPasswordView.setError(errorMessage);
//                        mPasswordView.requestFocus();
//                    }
//                    else if(errorCode.equals("4104"))
//                    {
//                        mTenantIdView.setError(errorMessage);
//                        mTenantIdView.requestFocus();
//                    }
//                }
//
//                //finish();
//            }
//            else
//            {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled()
//        {
//            mAuthTask = null;
//            showProgress(false);
//        }
    }
}