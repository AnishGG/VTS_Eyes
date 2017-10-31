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

import static android.graphics.Typeface.BOLD;


public class IdleReport extends Fragment {
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.idle_report, container, false);
        TableLayout list = view.findViewById(R.id.idlereport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 0;
                int k = 1, flagi = 0, i, diffdate;
                long millis = 0, second, minute, hour = 0, differ;
                String engst, Starttime = "", Endtime = "", locstart = "0.000000" + "," + "0.000000", locend = "0.000000" + "," + "0.000000", time1, time2, timedur, st = "", et = "", datea, dateb;
                for (i = 0; i < jsonArray.length(); i++) {
                    TableRow tr1 = new TableRow(getActivity());tr1.setPadding(0,3,0,0);
                    TextView tv1 = new TextView(getActivity());
                    TextView tv2 = new TextView(getActivity());
                    TextView tv3 = new TextView(getActivity());
                    TextView tv4 = new TextView(getActivity());
                    TextView tv5 = new TextView(getActivity());
                    TextView tv6 = new TextView(getActivity());
                    JSONObject ob = jsonArray.getJSONObject(i);
                    engst = ob.getString("EngineStatus");
                    if (Objects.equals(engst, "OFF")) {
                        counti = 1;
                        if (flagi == 0) {
                            Starttime = ob.getString("GPSTimestamp");
                            locstart = ob.getString("Latitude") + "," + ob.getString("Longitude");
                            flagi = 1;
                        }
                        Log.d("after", "inoutif");
                    } else if (Objects.equals(engst, "ON") && counti == 1) {
                        counti = 0;
                        flagi = 0;
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
                        Log.d("days", String.valueOf(differ));
                        second = (millis / 1000) % 60;
                        minute = (millis / (1000 * 60)) % 60;
                        hour = (24 * differ + (millis / (1000 * 60 * 60)));
                        timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                        st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                        et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                        tv1.setText(String.valueOf(k));
                        tv1.setBackgroundResource(R.drawable.cellborder);
                        tv1.setHeight(75);
                        tv1.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv2.setText(ob.getString("DeviceId"));
                        tv2.setBackgroundResource(R.drawable.cellborder);
                        tv2.setHeight(75);
                        tv2.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv3.setText(st);
                        tv3.setBackgroundResource(R.drawable.cellborder);
                        tv3.setHeight(75);
                        tv3.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv4.setText(et);
                        tv4.setBackgroundResource(R.drawable.cellborder);
                        tv4.setHeight(75);
                        tv4.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv5.setText(locstart);
                        tv5.setBackgroundResource(R.drawable.cellborder);
                        tv5.setHeight(75);
                        tv5.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv6.setText(timedur);
                        tv6.setBackgroundResource(R.drawable.cellborder);
                        tv6.setHeight(75);
                        tv6.setTextAppearance(android.R.style.TextAppearance_Small);
                        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr1.addView(tv1);
                        tr1.addView(tv2);
                        tr1.addView(tv3);
                        tr1.addView(tv4);
                        tr1.addView(tv5);
                        tr1.addView(tv6);
                        list.addView(tr1);
                        k++;
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
                    st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                    et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                    tv1.setText(String.valueOf(k));
                    tv1.setBackgroundResource(R.drawable.cellborder);
                    tv1.setHeight(75);
                    tv1.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv2.setText(obj1.getString("DeviceId"));
                    tv2.setBackgroundResource(R.drawable.cellborder);
                    tv2.setHeight(75);
                    tv2.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv3.setText(st);
                    tv3.setBackgroundResource(R.drawable.cellborder);
                    tv3.setHeight(75);
                    tv3.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv4.setText(et);
                    tv4.setBackgroundResource(R.drawable.cellborder);
                    tv4.setHeight(75);
                    tv4.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv5.setText(locstart);
                    tv5.setBackgroundResource(R.drawable.cellborder);
                    tv5.setHeight(75);
                    tv5.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv6.setText(timedur);
                    tv6.setBackgroundResource(R.drawable.cellborder);
                    tv6.setHeight(75);
                    tv6.setTextAppearance(android.R.style.TextAppearance_Small);
                    tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv4.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv5.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv6.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr1.addView(tv1);
                    tr1.addView(tv2);
                    tr1.addView(tv3);
                    tr1.addView(tv4);
                    tr1.addView(tv5);
                    tr1.addView(tv6);
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
