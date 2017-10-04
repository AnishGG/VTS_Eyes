package ssadteam5.vtsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import static android.util.Log.d;

public class DeviceDetailsFragment extends DialogFragment
{
    public static DeviceDetailsFragment newInstance()
    {
        DeviceDetailsFragment f = new DeviceDetailsFragment();
        return f;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater l= getActivity().getLayoutInflater();
        View view =l.inflate(R.layout.fragment_device_details,null);
        HashMap<String,String> details = (HashMap<String, String>) getArguments().getSerializable("details");
        String det=getArguments().getString("det");
        String vehicledetails = details.get("vehicleDetails");
        String driverdetails = details.get("driverDetails");
        Log.d("det",det);
        LinearLayout ll=  view.findViewById(R.id.linlay);
        try
        {

            Log.d("blaaa",vehicledetails);
            JSONObject jsonObject=new JSONObject(vehicledetails);

            Iterator<?> keys= jsonObject.keys();
            while(keys.hasNext()){
                String key=(String)keys.next();
                Log.d("key",key);
                TextView tv= new TextView(getActivity());
                tv.setPadding(50,0,0,0);
                tv.setText(key+" : "+jsonObject.get(key).toString());

                Log.d("log",jsonObject.get(key).toString());
                ll.addView(tv);
            }
            TextView tv= new TextView(getActivity());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tv.setPadding(75,0,0,0);
            tv.setText("Driver details");
            tv.setTypeface(null, Typeface.BOLD);
            ll.addView(tv);
            JSONObject json = new JSONObject(driverdetails);
            Iterator<?> Keys= json.keys();
            while(Keys.hasNext()){
                String key=(String) Keys.next();
                TextView tV = new TextView(getActivity());
                tV.setPadding(50,0,0,0);
                tV.setText(key+" : "+json.get(key).toString());

                ll.addView(tV);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Device Details");
        builder.setView(view);
        //builder.setMessage(vehicledetails);
        builder.setNeutralButton("Track",null );

//                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
        return builder.create();
    }
}
