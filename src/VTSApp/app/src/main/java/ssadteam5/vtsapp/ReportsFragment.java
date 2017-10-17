package ssadteam5.vtsapp;

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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

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



//    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("started","reportfrag");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        Log.d("inhere","reportthis");
        view = inflater.inflate(R.layout.fragment_reports, container, false);
        token = getArguments().getString("token");
//        AsyncTask myAsyncTask = new ReportsFragment(this);
        Log.d("tokeni", token);
        cont = getActivity();
        mFetchTask = new FetchDevNo(token);
//        new FetchDevNo(cont, token, view).execute();
        mFetchTask.execute((Void) null);


        return view;
    }

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
        protected void onPreExecute(){
            Log.d("something", "something");
            spinner = (Spinner) view.findViewById(R.id.spinner);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn;
            try {
                String response = "";
                URL url = new URL("http://eyedentifyapps.com:8080/api/auth/device/all/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Authorization", "Bearer " + mToken);
                InputStream in = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    response += current;
                }
                Log.d("resp2",response);
                JSONObject obj=new JSONObject(response);
                JSONArray arr=obj.getJSONArray("deviceDTOS");
                Log.d("hellodakshsize", ""+arr.length());
                for(int i=0;i<arr.length();i++){
//                    spinner.setOnItemSelectedListener(this);
                    try {
                        JSONObject ob = arr.getJSONObject(i);
                        JSONObject hello = new JSONObject(ob.getString("vehicleDetailsDO"));
                        String number = hello.getString("vehicleNumber");
                        Log.d("dakshhello", number);
                        list.add(number);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        protected void onPostExecute(final Boolean success) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(dataAdapter);
        }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
    }
}
