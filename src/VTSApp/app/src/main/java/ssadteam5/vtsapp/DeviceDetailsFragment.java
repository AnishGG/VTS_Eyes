package ssadteam5.vtsapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
//         Use the Builder class for convenient dialog construction
        String details = getArguments().getString("details");
        Log.d("details",details);
        try
        {
            JSONObject det = new JSONObject(details);
            Log.d("details", det.getString("vehicleDetails"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Device Details");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_device_details, null));
        builder.setMessage(details);
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
