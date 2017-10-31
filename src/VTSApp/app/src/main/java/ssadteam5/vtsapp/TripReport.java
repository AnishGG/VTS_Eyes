package ssadteam5.vtsapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_report, container, false);
        TableLayout list = view.findViewById(R.id.tripreport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 0, diffdate;
                int k = 1, l = 0, flagi = 0, i, dist = 0, speed = 0, Radius = 6371, kmInDec = 0, meterInDec = 0;
                double lat1, lat2, lon1, lon2, dLat, dLon, a, c, valueResult, km, meter, distance = 0;
                long millis, second, minute, hour, differ;
                String engst, Starttime = "", Endtime, locstart = "0.000000" + "," + "0.000000", locend, time1, time2, timedur, st, et, datea, dateb;
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
                            lat1 = Double.parseDouble(ob.getString("Latitude"));
                            lat2 = Double.parseDouble(obj2.getString("Latitude"));
                            lon1 = Double.parseDouble(ob.getString("Longitude"));
                            lon2 = Double.parseDouble(obj2.getString("Longitude"));
                            dLat = Math.toRadians(lat2 - lat1);
                            dLon = Math.toRadians(lon2 - lon1);
                            a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                    + Math.cos(Math.toRadians(lat1))
                                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                                    * Math.sin(dLon / 2);
                            c = 2 * Math.asin(Math.sqrt(a));
                            valueResult = Radius * c;
                            distance += valueResult;
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
                        datea = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"));
                        dateb = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"));
                        differ = java.lang.Math.abs((Long.parseLong(dateb.substring(8,10))-Long.parseLong(datea.substring(8,10))));
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = format.parse(time1);
                        Date date2 = format.parse(time2);
                        millis = java.lang.Math.abs(date2.getTime() - date1.getTime());
                        if(differ>1 && (date2.getTime() - date1.getTime())<0){
                            differ -= 1;
                        }
                        second = (millis / 1000) % 60;
                        minute = (millis / (1000 * 60)) % 60;
                        hour = (24 * differ + (millis / (1000 * 60 * 60)));
                        timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                        locend = obj1.getString("Latitude") + "," + obj1.getString("Longitude");
                        distance = Math.round(distance*100D)/100D;
                        st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                        et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                        tv1.setText(String.valueOf(k));
                        tv1.setBackgroundResource(R.drawable.cellborder);
                        tv1.setHeight(75);
                        tv1.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv2.setText(ob.getString("DeviceId"));
                        tv2.setBackgroundResource(R.drawable.cellborder);
                        tv2.setHeight(75);
                        tv2.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv3.setText(st);
                        tv3.setBackgroundResource(R.drawable.cellborder);
                        tv3.setHeight(75);
                        tv3.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv4.setText(et);
                        tv4.setBackgroundResource(R.drawable.cellborder);
                        tv4.setHeight(75);
                        tv4.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv5.setText(locstart);
                        tv5.setBackgroundResource(R.drawable.cellborder);
                        tv5.setHeight(75);
                        tv5.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv6.setText(locend);
                        tv6.setBackgroundResource(R.drawable.cellborder);
                        tv6.setHeight(75);
                        tv6.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv7.setText(timedur);
                        tv7.setBackgroundResource(R.drawable.cellborder);
                        tv7.setHeight(75);
                        tv7.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv8.setText(String.valueOf((distance)));
                        tv8.setBackgroundResource(R.drawable.cellborder);
                        tv8.setHeight(75);
                        tv8.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
                        tv9.setText(String.valueOf(speed));
                        tv9.setBackgroundResource(R.drawable.cellborder);
                        tv9.setHeight(75);
                        tv9.setTextAppearance(getContext(),android.R.style.TextAppearance_Small);
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
                        distance = 0;
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
                    datea = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"));
                    dateb = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"));
                    differ = java.lang.Math.abs((Long.parseLong(dateb.substring(8,10))-Long.parseLong(datea.substring(8,10))));
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date date1 = format.parse(time1);
                    Date date2 = format.parse(time2);
                    millis = java.lang.Math.abs(date2.getTime() - date1.getTime());
                    if(differ>1 && (date2.getTime() - date1.getTime())<0){
                        differ -= 1;
                    }
                    second = (millis / 1000) % 60;
                    minute = (millis / (1000 * 60)) % 60;
                    hour = (24 * differ + (millis / (1000 * 60 * 60)));
                    timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                    locend = obj1.getString("Latitude") + "," + obj1.getString("Longitude");
                    st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                    et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                    tv1.setText(String.valueOf(k));
                    tv2.setText(obj1.getString("DeviceId"));
                    tv3.setText(st);
                    tv4.setText(et);
                    tv5.setText(locstart);
                    tv6.setText(locend);
                    tv7.setText(timedur);
                    tv8.setText(String.valueOf(distance));
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
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
