package ssadteam5.vtsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class TripReport extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trip_report, container, false);
        TableLayout list = view.findViewById(R.id.tripreport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 0;
                int k = 1, l = 0, flagi = 0, i, dist = 0, speed = 0;
                long millis, second, minute, hour;
                String engst, Starttime = "", Endtime = "", locstart = "0.000000" + "," + "0.000000", locend = "0.000000" + "," + "0.000000", time1, time2, timedur;
                for (i = 0; i < jsonArray.length(); i++) {
                    TableRow tr1 = new TableRow(getActivity());
                    TextView tv1 = new TextView(getActivity());
                    TextView tv2 = new TextView(getActivity());
                    TextView tv3 = new TextView(getActivity());
                    TextView tv4 = new TextView(getActivity());
                    TextView tv5 = new TextView(getActivity());
                    TextView tv6 = new TextView(getActivity());
                    TextView tv7 = new TextView(getActivity());
                    TextView tv8 = new TextView(getActivity());
                    TextView tv9 = new TextView(getActivity());
                    JSONObject ob = jsonArray.getJSONObject(i);
                    engst = ob.getString("EngineStatus");
                    if (Objects.equals(engst, "ON")) {
                        l++;
                        counti = 1;
                        if (flagi == 0) {
                            Starttime = ob.getString("GPSTimestamp");
                            locstart = ob.getString("Latitude") + "," + ob.getString("Longitude");
                            flagi = 1;
                        }
                        Log.d("after", "inoutif");
                        if(l > 1) {
                            JSONObject obj2 = jsonArray.getJSONObject(i-1);
                            dist += (Integer.parseInt(ob.getString("Distance"))-Integer.parseInt(obj2.getString("Distance")));
                        }
                        speed = java.lang.Math.max(speed, Integer.parseInt(ob.getString("Speed")));
                    } else if (Objects.equals(engst, "OFF") && counti == 1) {
                        counti = 0;
                        flagi = 0;
                        l = 0;
                        JSONObject obj1 = jsonArray.getJSONObject(i - 1);
                        Endtime = obj1.getString("GPSTimestamp");
                        time1 = Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                        time2 = Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = format.parse(time1);
                        Date date2 = format.parse(time2);
                        millis = date2.getTime() - date1.getTime();
                        second = (millis / 1000) % 60;
                        minute = (millis / (1000 * 60)) % 60;
                        hour = (millis / (1000 * 60 * 60)) % 24;
                        timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                        locend = obj1.getString("Latitude") + "," + obj1.getString("Longitude");
                        tv1.setText(String.valueOf(k));
                        tv2.setText(ob.getString("DeviceId"));
                        tv3.setText(Starttime);
                        tv4.setText(Endtime);
                        tv5.setText(locstart);
                        tv6.setText(locend);
                        tv7.setText(timedur);
                        tv8.setText(String.valueOf(dist));
                        tv9.setText(String.valueOf(speed));
                        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv7.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv8.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv9.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr1.addView(tv1);
                        tr1.addView(tv2);
                        tr1.addView(tv3);
                        tr1.addView(tv4);
                        tr1.addView(tv5);
                        tr1.addView(tv6);
                        tr1.addView(tv7);
                        tr1.addView(tv8);
                        tr1.addView(tv9);
                        list.addView(tr1);
                        k++;
                        dist = 0;
                        speed = 0;
                    }
                }
                if(flagi == 1){
                    TableRow tr1 = new TableRow(getActivity());
                    TextView tv1 = new TextView(getActivity());
                    TextView tv2 = new TextView(getActivity());
                    TextView tv3 = new TextView(getActivity());
                    TextView tv4 = new TextView(getActivity());
                    TextView tv5 = new TextView(getActivity());
                    TextView tv6 = new TextView(getActivity());
                    TextView tv7 = new TextView(getActivity());
                    TextView tv8 = new TextView(getActivity());
                    TextView tv9 = new TextView(getActivity());
                    JSONObject obj1 = jsonArray.getJSONObject(i - 1);
                    Endtime = obj1.getString("GPSTimestamp");
                    time1 = Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                    time2 = Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date date1 = format.parse(time1);
                    Date date2 = format.parse(time2);
                    millis = date2.getTime() - date1.getTime();
                    second = (millis / 1000) % 60;
                    minute = (millis / (1000 * 60)) % 60;
                    hour = (millis / (1000 * 60 * 60)) % 24;
                    timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                    locend = obj1.getString("Latitude") + "," + obj1.getString("Longitude");
                    tv1.setText(String.valueOf(k));
                    tv2.setText(obj1.getString("DeviceId"));
                    tv3.setText(Starttime);
                    tv4.setText(Endtime);
                    tv5.setText(locstart);
                    tv6.setText(locend);
                    tv7.setText(timedur);
                    tv8.setText(String.valueOf(dist));
                    tv9.setText(String.valueOf(speed));
                    tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv7.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv8.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv9.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr1.addView(tv1);
                    tr1.addView(tv2);
                    tr1.addView(tv3);
                    tr1.addView(tv4);
                    tr1.addView(tv5);
                    tr1.addView(tv6);
                    tr1.addView(tv7);
                    tr1.addView(tv8);
                    tr1.addView(tv9);
                    list.addView(tr1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
