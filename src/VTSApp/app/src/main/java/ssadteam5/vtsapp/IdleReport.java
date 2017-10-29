package ssadteam5.vtsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by keshav on 26/10/17.
 */

public class IdleReport extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.idle_report, container, false);
        Bundle bundle=getArguments();
        if (bundle!= null){
        TableLayout list= view.findViewById(R.id.idlereport);

        String re = bundle.getString("resp");
            Log.d("responsevvvvv",re);
        String s = bundle.getString("0");
        try{
            JSONArray jsonArray = new JSONArray(re);
            int counti = 1;
            int k = 0;
            for(int i=0;i<jsonArray.length();i++)
            {Log.d("keshav","keshav");
                TableRow tr=new TableRow(getContext());
                TextView tvi1 = new TextView(getContext());
                TextView tvi2 = new TextView(getContext());
                TextView tvi3 = new TextView(getContext());
                TextView tvi4 = new TextView(getContext());
                TextView tvi5 = new TextView(getContext());
                TextView tvi6 = new TextView(getContext());
                JSONObject ob = jsonArray.getJSONObject(i);
                String engst = ob.getString("EngineStatus");
                if(Objects.equals(engst, "ON") && counti ==1){
                    tvi1.setText(k);
                    tvi2.setText(ob.getString("DeviceId"));
                    tvi3.setText(ob.getString("GPSTimestamp"));
                    tvi4.setText(ob.getString("GPSTimestamp"));
                    tvi5.setText(ob.getString("GPSTimestamp"));
                    tvi6.setText(ob.getString("GPSTimestamp"));
                    tr.addView(tvi1);
                    tr.addView(tvi2);
                    tr.addView(tvi3);
                    tr.addView(tvi4);
                    tr.addView(tvi5);
                    tr.addView(tvi6);
                    list.addView(tr);
                    counti = 0;
                    k++;
                    Log.d("hi","hiiii");
                }
                else if(Objects.equals(engst, "OFF") && counti ==0){
                    JSONObject jo = jsonArray.getJSONObject(i);
                    tvi1.setText(k);
                    tvi2.setText(jo.getString("DeviceId"));
                    tvi3.setText(jo.getString("GPSTimestamp"));
                    tvi4.setText(jo.getString("GPSTimestamp"));
                    tvi5.setText(jo.getString("GPSTimestamp"));
                    tvi6.setText(jo.getString("GPSTimestamp"));
                    tr.addView(tvi1);
                    tr.addView(tvi2);
                    tr.addView(tvi3);
                    tr.addView(tvi4);
                    tr.addView(tvi5);
                    tr.addView(tvi6);
                    list.addView(tr);
                    counti=1;
                    k++;
                    Log.d("hi","hiiii");
                }
                //Log.d("test",ob.toString());
//                counter++;
//                Log.d("counter", String.valueOf(counter));
            }
//            if(k%2){
//
//            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }}
//        try {
//            JSONObject jo = new JSONObject(s);
//            for(int i=0;i<6;i++) {
//                tvi.setText(jo.getString("DeviceId"));
//                tr.addView(tvi);
//            }
//            list.addView(tr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        for(int i=0;i<6;i++) {
//            TextView tv=new TextView(getContext());
//            tv.setText(getArguments().getString("kes"));
//            tr.addView(tv);
//        }list.addView(tr);
        Log.d("kesh","kesh");
        return view;
    }
}
