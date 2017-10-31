package ssadteam5.vtsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class TripReport extends Fragment {
    private View view;
    private ReportsFetchTask mFetchTask;
    List<tableText> trip = new ArrayList<tableText>();
    private ProgressDialog Dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trip_report, container, false);
        TableLayout list = view.findViewById(R.id.tripreport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFetchTask = new ReportsFetchTask(list);
            mFetchTask.execute((Void) null);
        }
        return view;
    }

    public class ReportsFetchTask extends AsyncTask<Void, Void, Boolean>
    {
        TableLayout list;
        ReportsFetchTask(TableLayout mylist) {
            list = mylist;
        }

        @Override
        protected void onPreExecute(){
            Dialog = new ProgressDialog(getActivity());
            Dialog.setMessage("Finalising Data...");
            Dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 0, diffdate;
                int k = 1, l = 0, flagi = 0, i, dist = 0, speed = 0, Radius = 6371, kmInDec = 0, meterInDec = 0;
                double lat1, lat2, lon1, lon2, dLat, dLon, a, c, valueResult = 0, km, meter, distance = 0;
                long millis = 0, second, minute, hour = 0, differ;
                String engst, Starttime = "", Endtime = "", locstart = "0.000000" + "," + "0.000000", locend = "0.000000" + "," + "0.000000", time1, time2, timedur, st = "", et = "", datea, dateb;
                for (i = 0; i < jsonArray.length(); i++) {
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
                        tableText mynewtext = new tableText();
                        mynewtext.setString(String.valueOf(k), 0);
                        mynewtext.setString(obj1.getString("DeviceId"), 1);
                        mynewtext.setString(st, 2);
                        mynewtext.setString(et, 3);
                        mynewtext.setString(locstart, 4);
                        mynewtext.setString(locend, 5);
                        mynewtext.setString(timedur, 6);
                        mynewtext.setString(String.valueOf(distance), 7);
                        mynewtext.setString(String.valueOf(speed), 8);
                        mynewtext.setCounti(counti);
                        mynewtext.setFlagi(flagi);
                        trip.add(mynewtext);
                        k++;
                        distance = 0;
                        speed = 0;
                    }
                }
                if(flagi == 1){
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
                    tableText mynewtext = new tableText();
                    mynewtext.setString(String.valueOf(k), 0);
                    mynewtext.setString(obj1.getString("DeviceId"), 1);
                    mynewtext.setString(st, 2);
                    mynewtext.setString(et, 3);
                    mynewtext.setString(locstart, 4);
                    mynewtext.setString(locend, 5);
                    mynewtext.setString(timedur, 6);
                    mynewtext.setString(String.valueOf(distance), 7);
                    mynewtext.setString(String.valueOf(speed), 8);
                    mynewtext.setCounti(counti);
                    mynewtext.setFlagi(flagi);
                    trip.add(mynewtext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            TextView[] tv = new TextView[9];
            for(int i = 0;i < trip.size(); i++){
                TableRow tr1 = new TableRow(getActivity());
                for(int j = 0; j < 9; j++){
                    tv[j] = new TextView(getActivity());
                    tv[j].setText(trip.get(i).getString(j));
                    tv[j].setBackgroundResource(R.drawable.cellborder);
                    tv[j].setHeight(75);
                    tv[j].setTextAppearance(android.R.style.TextAppearance_Small);
                    tv[j].setGravity(Gravity.CENTER_HORIZONTAL);
                    tr1.addView(tv[j]);
                }
                if(trip.get(i).getCount() == 1)
                    tr1.setPadding(0, 3, 0, 0);
                list.addView(tr1);
            }
            Dialog.dismiss();
        }
    }

    private class tableText{
        private String[] text = new String[9];
        private int flagi, counti;
        public tableText(){};
        public void setString(String s, int idx){
            text[idx] = s;
        }
        public void setFlagi(int flag){
            flagi = flag;
        }
        public void setCounti(int count){
            counti = count;
        }
        public String getString(int idx){
            return text[idx];
        }
        public int getFlag(){
            return flagi;
        }
        public int getCount(){
            return counti;
        }
    }
}
