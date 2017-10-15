package ssadteam5.vtsapp;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment
{
    View view;
    private String token;
    public RecyclerView recyclerView;
    public VehicleCardAdapter vehicleCardAdapter;
    public List<VehicleCard> vehicleCardList;
    private DeviceFetchTask mFetchTask;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        token = getArguments().getString("token");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        vehicleCardList = new ArrayList<>();
//        vehicleCardList.add(new VehicleCard("name"));
//        vehicleCardList.add(new VehicleCard("name1"));
//        vehicleCardList.add(new VehicleCard("name2"));
        vehicleCardAdapter = new VehicleCardAdapter(getContext(),vehicleCardList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(vehicleCardAdapter);
        createSummaryTable();
        vehicleCardAdapter.notifyDataSetChanged();

        mFetchTask = new DeviceFetchTask(token);
        mFetchTask.execute((Void) null);
        return view;
    }

    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Dashboard");
    }

    private void createSummaryTable()
    {
        TableLayout tableLayout = view.findViewById(R.id.summary);
        TextView heading = new TextView(getActivity());
        heading.setText("Summary");
        heading.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);
        heading.setPadding(50,20,50,20);
        tableLayout.addView(heading);
        tableLayout.addView(createRow("Active Vehicles","-"));
        tableLayout.addView(createRow("Inactive Vehicles","-"));
        tableLayout.addView(createRow("Total Distance Covered","-"));
        tableLayout.addView(createRow("Alerts Generated","-"));

    }
    private TableRow createRow(String a, String b)
    {
        TableRow tr = new TableRow(getActivity());
        TextView tv1 = new TextView(getActivity());
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setText(a);
        tr.addView(tv1);
        TextView tv2 = new TextView(getActivity());
        tv2.setText(b);
        tr.addView(tv2);
        tr.setPadding(50, 20, 50, 20);
        return tr;
    }
    public class DeviceFetchTask extends AsyncTask<Void, Void, Boolean>
    {
        private final String mToken;
        DeviceFetchTask(String token)
        {
            mToken = token;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            HttpURLConnection conn;
            try {
                String response = "";
                URL url = new URL("http://eyedentifyapps.com:8080/api/auth/device/all/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept","*/*");
                conn.setRequestProperty("Authorization","Bearer " + mToken);
                InputStream in = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1)
                {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    response += current;
                }
                Log.d("resp",response);
                JSONObject obj=new JSONObject(response);
                JSONArray arr=obj.getJSONArray("deviceDTOS");
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject ob=arr.getJSONObject(i);
                    VehicleCard vehicleCard = new VehicleCard(ob.getString("name"));
                    vehicleCardList.add(vehicleCard);
                    Log.d("token","inside for");
//                    vehicleCardAdapter.notifyDataSetChanged();
                }
            }

            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success)
        {
            if(getActivity() != null)
            {
                Log.d("token","inside post exec");
                vehicleCardAdapter.notifyDataSetChanged();
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable()
//                {
//                    public void run()
//                    {
//                        vehicleCardAdapter.notifyDataSetChanged();
//                    }
//                });
            }
        }

        @Override
        protected void onCancelled()
        {
        }
    }
}
