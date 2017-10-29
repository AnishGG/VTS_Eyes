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


public class IdleReport extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.idle_report, container, false);
        TableLayout list = view.findViewById(R.id.idlereport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 1;
                int k = 0, l = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    TableRow tr1 = new TableRow(getActivity());
                    TextView tv1 = new TextView(getActivity());
                    TextView tv2 = new TextView(getActivity());
                    TextView tv3 = new TextView(getActivity());
                    TextView tv4 = new TextView(getActivity());
                    TextView tv5 = new TextView(getActivity());
                    TextView tv6 = new TextView(getActivity());
                    JSONObject ob = jsonArray.getJSONObject(i);
                    String engst = ob.getString("EngineStatus");
                    if (Objects.equals(engst, "ON") && counti == 1) {
                        tv1.setText(String.valueOf(k));
                        tv2.setText(ob.getString("DeviceId"));
                        tv3.setText(ob.getString("GPSTimestamp"));
                        tv4.setText(ob.getString("GPSTimestamp"));
                        tv5.setText(ob.getString("GPSTimestamp"));
                        tv6.setText(ob.getString("GPSTimestamp"));
                        tr1.addView(tv1);
                        tr1.addView(tv2);
                        tr1.addView(tv3);
                        tr1.addView(tv4);
                        tr1.addView(tv5);
                        tr1.addView(tv6);
                        list.addView(tr1);
                        counti = 0;
                        k++;
                    } else if (Objects.equals(engst, "OFF") && counti == 0) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        tv1.setText(String.valueOf(l));
                        tv2.setText(jo.getString("DeviceId"));
                        tv3.setText(jo.getString("GPSTimestamp"));
                        tv4.setText(jo.getString("GPSTimestamp"));
                        tv5.setText(jo.getString("GPSTimestamp"));
                        tv6.setText(jo.getString("GPSTimestamp"));
                        tr1.addView(tv1);
                        tr1.addView(tv2);
                        tr1.addView(tv3);
                        tr1.addView(tv4);
                        tr1.addView(tv5);
                        tr1.addView(tv6);
                        list.addView(tr1);
                        counti = 1;
                        l++;
                    }
                    //                    Log.d("test", ob.toString());
                    //                counter++;
                    //                Log.d("counter", String.valueOf(counter));
                }
                //            if(k%2){
                //
                //            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
