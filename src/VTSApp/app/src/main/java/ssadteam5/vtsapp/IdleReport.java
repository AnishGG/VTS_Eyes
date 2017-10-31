package ssadteam5.vtsapp;

import android.app.Activity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;


public class IdleReport extends Fragment {
    private View view;
    private IdleFetchTask mFetchTask;
    List<tableText> trip = new ArrayList<tableText>();
    private ProgressDialog Dialog;
    private Activity mActivity;
    private UserData userData;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.idle_report, container, false);
        mActivity = getActivity();
        userData = new UserData(getActivity().getApplicationContext());
        TableLayout list = view.findViewById(R.id.idlereport);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFetchTask = new IdleFetchTask(list);
            mFetchTask.execute((Void) null);
        }
        return view;
    }
    public class IdleFetchTask extends AsyncTask<Void, Void, Boolean>
    {
        TableLayout list;
        IdleFetchTask(TableLayout mylist) {
            list = mylist;
        }

        @Override
        protected void onPreExecute(){
            Log.d("Fragment", "IdleReport");
            Dialog = new ProgressDialog(mActivity);
            Dialog.setTitle("Finalising Data...");
            Dialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params){
            try {
                JSONArray jsonArray = new JSONArray(getArguments().getString("resp"));
                int counti = 0;
                int k = 1, flagi = 0, i, diffdate;
                long millis = 0, second, minute, hour = 0, differ;
                String engst, Starttime = "", Endtime = "", locstart = "0.000000" + "," + "0.000000", locend = "0.000000" + "," + "0.000000", time1, time2, timedur, st = "", et = "", datea, dateb;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject ob = jsonArray.getJSONObject(i);
                    engst = ob.getString("EngineStatus");
                    if (Objects.equals(engst, "OFF")) {
                        counti = 1;
                        if (flagi == 0) {
                            Starttime = ob.getString("GPSTimestamp");
                            locstart = ob.getString("Latitude") + "," + ob.getString("Longitude");
                            flagi = 1;
                        }
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
                        second = (millis / 1000) % 60;
                        minute = (millis / (1000 * 60)) % 60;
                        hour = (24 * differ + (millis / (1000 * 60 * 60)));
                        timedur = String.format("%02d:%02d:%02d", hour, minute, second);
                        st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                        et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                        tableText mynewtext = new tableText();
                        mynewtext.setString(String.valueOf(k), 0);
                        mynewtext.setString(obj1.getString("DeviceId"), 1);
                        mynewtext.setString(st, 2);
                        mynewtext.setString(et, 3);
                        mynewtext.setString(locstart, 4);
                        mynewtext.setString(timedur, 5);
                        mynewtext.setCounti(counti);
                        mynewtext.setFlagi(flagi);
                        trip.add(mynewtext);
                        k++;
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
                    st  = Starttime.substring(Starttime.indexOf(0)+1, Starttime.indexOf("T"))+"   "+Starttime.substring(Starttime.indexOf("T")+1, Starttime.indexOf("Z"));
                    et  = Endtime.substring(Endtime.indexOf(0)+1, Endtime.indexOf("T"))+"   "+Endtime.substring(Endtime.indexOf("T")+1, Endtime.indexOf("Z"));
                    tableText mynewtext = new tableText();
                    mynewtext.setString(String.valueOf(k), 0);
                    mynewtext.setString(obj1.getString("DeviceId"), 1);
                    mynewtext.setString(st, 2);
                    mynewtext.setString(et, 3);
                    mynewtext.setString(locstart, 4);
                    mynewtext.setString(timedur, 5);
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
                TableRow tr1 = new TableRow(mActivity);
                for(int j = 0; j < 6; j++){
                    tv[j] = new TextView(mActivity);
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
        private String[] text = new String[6];
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
