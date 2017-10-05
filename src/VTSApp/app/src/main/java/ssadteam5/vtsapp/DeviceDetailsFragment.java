package ssadteam5.vtsapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;
import java.util.HashMap;


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
        TableLayout tl=  view.findViewById(R.id.tl);
        tl.setPadding(75,50,0,0);
        try {
            JSONObject json = new JSONObject(vehicledetails);
            tl.addView(row("Vehicle name", json.getString("vehicleName")));
            tl.addView(row("Device ID",json.getString("device")));
            tl.addView(row("Vehicle Reg No.",json.getString("vehicleNumber")));
            tl.addView(row("Vehicle Type",json.getString("vehicleType")));
            tl.addView(row("Purchase Year",json.getString("purchaseYear")));
            tl.addView(row("Previous Service",json.getString("serviceOn")));
            tl.addView(row("Next Service",json.getString("nextServiceOn")));
            tl.addView(row("Notes",json.getString("notes")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("VEHICLE INFO");
        builder.setView(view);
        //builder.setMessage(vehicledetails);
        builder.setNeutralButton("Track",null );


        return builder.create();
    }
    private TableRow row(String a, String b){
        GradientDrawable gd=new GradientDrawable();
        gd.setStroke(2, Color.BLACK);
        TableRow tr= new TableRow(getActivity());
        TextView tv1= new TextView(getActivity());
        tv1.setTypeface(null,Typeface.BOLD);
        tv1.setText(a+": ");
        tr.addView(tv1);
        TextView tv2 = new TextView(getActivity());
        tv2.setText(b);
        tr.addView(tv2);
        tr.setPadding(50,20,100,20);
        tr.setBackgroundDrawable(gd);
        return tr;
    }

}
