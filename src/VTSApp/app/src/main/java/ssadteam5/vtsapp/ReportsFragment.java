package ssadteam5.vtsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class    ReportsFragment extends Fragment {
    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    private View view;
    private String token;
    private String response;
    private Spinner spinner;
    private final List<String> list = new ArrayList<>();
    private String[] vehicle_list;

    private EditText tvDisplayDate1;
    private EditText tvDisplayDate2;

    private int year;
    private int month;
    private int day;
    private Boolean flag1 = false;
    private Boolean flag2 = false;
    private String startdate = "";
    private String enddate = "";
    private String spres = "";
    private InputMethodManager inputMethodManager;
    private static final int DATE_DIALOG_ID = 999;
    private UserData userData;
    private StringBuilder sdate;
    Bundle bund;
    ViewPager viewPager;
    PagerAdapter adapter;
    TabLayout tabLayout;
    //    StringBuffer sb = new StringBuffer();
    String sb = "";

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
//        tabLayout = (TabLayout) view.findViewById(R.id.tabl);
//        tabLayout.addTab(tabLayout.newTab().setText("Trip report"));
//        tabLayout.addTab(tabLayout.newTab().setText("Idle report"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        viewPager = (ViewPager) view.findViewById(R.id.pager);
//        adapter = new PagerAdapter
//                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), bund);
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        Context cont = getActivity();
        FetchDevNo mFetchTask = new FetchDevNo(token);
        mFetchTask.execute((Void) null);
        setCurrentDateOnView();
        addListenerOnButton();
        Button btnsub = view.findViewById(R.id.btnsub);
        EditText changeDate2 = view.findViewById(R.id.e2);
        changeDate2.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.btnsub || id == EditorInfo.IME_NULL) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    getReports();
                    return true;
                }
                return false;
            }
        });
        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                getReports();
                //showTables();
            }
        });
        return view;
    }

    private void getReports()
    {
        String vehicle = spinner.getSelectedItem().toString();
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putString("token",token);
        bundle.putString("vehicle", vehicle);
        startdate = tvDisplayDate1.getText().toString();
        enddate = tvDisplayDate2.getText().toString();
        bundle.putString("startdate",startdate);
        bundle.putString("enddate",enddate);
        fragment = new Reports();
        fragment.setArguments(bundle);
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

    private void setCurrentDateOnView() {

        tvDisplayDate1 = view.findViewById(R.id.e1);
        tvDisplayDate2 = view.findViewById(R.id.e2);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        if ((month + 1) / 10 == 0) {
            if (day / 10 == 0) {
                sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
            } else {
                sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
            }
        } else {
            if (day / 10 == 0) {
                sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
            } else {
                sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
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

    private void addListenerOnButton() {

        Button btnChangeDate1 = view.findViewById(R.id.btnChangeDate1);
        Button btnChangeDate2 = view.findViewById(R.id.btnChangeDate2);


        btnChangeDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag1 = true;
                flag2 = false;
                onCreateDialog().show();
            }


        });
        btnChangeDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag2 = true;
                flag1 = false;
                onCreateDialog().show();
//                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    private Dialog onCreateDialog() {
        switch (ReportsFragment.DATE_DIALOG_ID) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(getActivity(), datePickerListener,
                        year, month, day);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            if (flag1) {
                if ((month + 1) / 10 == 0) {
                    if (day / 10 == 0) {
                        sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
                    } else {
                        sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
                    }
                } else {
                    if (day / 10 == 0) {
                        sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
                    } else {
                        sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
                    }
                }
                tvDisplayDate1.setText(sdate);
            }
            if (flag2) {
                if ((month + 1) / 10 == 0) {
                    if (day / 10 == 0) {
                        sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append("0").append(day);
                    } else {
                        sdate = new StringBuilder().append(year).append("-").append("0").append(month + 1).append("-").append(day);
                    }
                } else {
                    if (day / 10 == 0) {
                        sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append("0").append(day);
                    } else {
                        sdate = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day);
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
            spinner = view.findViewById(R.id.spinner);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (!userData.isDataFetched()) {
                    userData.fetchData();
                }
                String response = userData.getResponse().get(UserData.KEY_RESPONSE);
                JSONObject obj = new JSONObject(response);
                JSONArray arr = obj.getJSONArray("deviceDTOS");
                Log.d("hellodakshsize", "" + arr.length());
                list.clear();
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
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
            spinner.setAdapter(dataAdapter);
        }
    }


}
