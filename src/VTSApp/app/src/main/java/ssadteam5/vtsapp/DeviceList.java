package ssadteam5.vtsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

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
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class DeviceList extends Fragment
{
    View view;
    private String token;
    private DeviceFetchTask mFetchTask;
    private ArrayList<HashMap<String, String>> deviceDet = new ArrayList<>();

    private StompClient mStompClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_device_list, container, false);
        token = getArguments().getString("token");
        mFetchTask = new DeviceFetchTask(token);
        mFetchTask.execute((Void) null);
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
                        String details = parent.getAdapter().getItem(position).toString();
                        Log.d("pos", ""+position);
                        Bundle bundle = new Bundle();
                        bundle.putString("details",details);

                        DeviceDetailsFragment dialog = new DeviceDetailsFragment();
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(),"dialog");
                        Log.d("clicked","works");
                    }
                });


            }
        }

        @Override
        protected void onCancelled()
        {
        }
    }
}


