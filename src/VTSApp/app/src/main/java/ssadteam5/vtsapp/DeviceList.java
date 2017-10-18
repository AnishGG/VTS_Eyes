package ssadteam5.vtsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceList extends Fragment
{
    View view;
    SwipeRefreshLayout swipeLayout;
    private DeviceFetchTask mFetchTask;
    private ArrayList<HashMap<String, String>> deviceDet = new ArrayList<>();
    UserData userData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_device_list, container, false);
        userData = new UserData(getActivity().getApplicationContext());
        mFetchTask = new DeviceFetchTask();
        mFetchTask.execute((Void) null);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshDeviceList);
        swipeLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // This method performs the actual data-refresh operation.
                    // The method calls setRefreshing(false) when it's finished.
                    deviceDet.clear();
                    userData.destroyResponse();
                    mFetchTask = new DeviceFetchTask();
                    mFetchTask.execute((Void) null);
                }
            }
    );
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Devices");
    }
    public class DeviceFetchTask extends AsyncTask<Void, Void, Boolean>
    {
        DeviceFetchTask() {
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            try {
                if(!userData.isDataFetched())
                    userData.fetchData();
                String response = userData.getResponse().get(UserData.KEY_RESPONSE);
                JSONObject obj=new JSONObject(response);
                JSONArray arr=obj.getJSONArray("deviceDTOS");
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject ob=arr.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("account",ob.getString("account"));
                    map.put("name",ob.getString("name"));
                    map.put("description",ob.getString("description"));
                    map.put("driverDetails",ob.getString("driverDetailsDO"));
                    map.put("vehicleDetails",ob.getString("vehicleDetailsDO"));
//                    Log.d("test",ob.getString("vehicleDetailsDO"));
                    deviceDet.add(map);
                }
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
                ListAdapter adapter = new SimpleAdapter(getContext(), deviceDet, R.layout.list_item,
                        new String[]{"account", "name", "description"},
                        new int[]{R.id.account, R.id.name, R.id.description});
                ListView listView = view.findViewById(R.id.listview);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
//                        Object o = listView.getItemAtPosition(position);
                        HashMap<String,String> details = (HashMap<String, String>) parent.getAdapter().getItem(position);
                        String det=parent.getAdapter().getItem(position).toString();
                        Log.d("pos", ""+position);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("details",details);
                        bundle.putString("det",det);
                        DeviceDetailsFragment dialog = new DeviceDetailsFragment();
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(),"dialog");
                        Log.d("clicked","works");
                    }
                });
                swipeLayout.setRefreshing(false);
            }
        }

        @Override
        protected void onCancelled()
        {
        }
    }



}


