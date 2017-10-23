package ssadteam5.vtsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class TrackVehicleActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private SlidingUpPanelLayout mLayout;
    UserSessionManager session;
    UserData userData;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    StompClient mStompClient;
    private String token;
    private String deviceName;
    private Marker marker = null;
    boolean isMarkerRotating = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_vehicle);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        session = new UserSessionManager(getApplicationContext());
        userData = new UserData(getApplicationContext());
        mapFrag.getMapAsync(this);

        token = session.getUserDetails().get(UserSessionManager.KEY_TOKEN); //fetching from the UserSessionManager
        deviceName = getIntent().getExtras().getString("deviceName");
        getSupportActionBar().setTitle(deviceName);
        // Setting sliding panel text
        setPanelText();


        JWT jwt = new JWT(token);
        Claim claim = jwt.getClaim("organisationId");
        String organisationId = claim.asString();

        mStompClient = Stomp.over(WebSocket.class,getString(R.string.websocket));
        mStompClient.connect();
        mStompClient.topic("/device/message" + organisationId).subscribe(topicMessage -> {
            JSONObject payload = new JSONObject(topicMessage.getPayload());
            try
            {
                final String deviceId = payload.get("DeviceId").toString();
                Double lat = Double.parseDouble(payload.get("Latitude").toString());
                final Double lon = Double.parseDouble(payload.get("Longitude").toString());
                final Float courseOverGround = Float.parseFloat(payload.get("CourseOverGround").toString());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        boolean New = true;
                        if(marker != null) {
                            if (deviceId.equals(marker.getTag().toString())) {
                                rotateMarker(marker, courseOverGround);
                                animateMarker(marker, new LatLng(lat, lon));
                                New = false;
                            }
                        }
                        if (New)
                        {
                            if(deviceName.equals(deviceId)) {
                                marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(deviceId));
                                int height = 140;
                                int width = 70;
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.cars);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                marker.setTag(deviceId);
                                marker.setAnchor(0.5f, 0.5f);
                                marker.setInfoWindowAnchor(0.5f, 0.5f);
                                marker.setRotation(courseOverGround);
                            }
                        }
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(17.9,78);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                marker.showInfoWindow();
                return true;
            }
        });
    }
    public void animateMarker(final Marker marker, final LatLng toPosition)
    {
        final Handler handler = new Handler();
        final LatLng oldPos = marker.getPosition();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mGoogleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0)
                {
                    handler.postDelayed(this, 16);
                }
                else
                {
                    marker.setVisible(true);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                    mGoogleMap.addCircle(new CircleOptions()
                            .center(oldPos)
                            .radius(2)
                            .strokeColor(Color.RED)
                            .fillColor(Color.RED));
                }
            }
        });
    }
    public void init(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    @Override
    public void onBackPressed()
    {
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
            super.onBackPressed();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mStompClient.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private void setPanelText(){
        String response = userData.getResponse().get(UserData.KEY_RESPONSE);
        Log.d("myresp", response);
        try {
            String vehicleDetailsDO;
            int idx = 0;
            JSONObject obj = new JSONObject(response);
            JSONArray arr = obj.getJSONArray("deviceDTOS");
            for(int i = 0;i < arr.length(); i++){
                JSONObject ob=arr.getJSONObject(i);
                if(ob.getString("name").equals(deviceName)) {
                    idx = i;
                }
            }
            vehicleDetailsDO = arr.getJSONObject(idx).getString("vehicleDetailsDO");

            TableLayout t = (TableLayout) findViewById(R.id.vehicleInformationPanel);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView qty = new TextView(this);
            qty.setText("Device: " + deviceName);
            row.addView(qty);
            t.addView(row);

            if(!vehicleDetailsDO.equals("null")) {
                JSONObject vehicleObj = new JSONObject(vehicleDetailsDO);
//                String vehicleColor = vehicleObj.getString("color");
                String make = vehicleObj.getString("make");
                String nextService = vehicleObj.getString("nextServiceOn");
                String notes = vehicleObj.getString("notes");
                String vehicleNumber = vehicleObj.getString("vehicleNumber");
                String vehicleType = vehicleObj.getString("vehicleType");
                String vehicleName = vehicleObj.getString("vehicleName");
                TableRow row1 = new TableRow(this);
                row1.setLayoutParams(lp);
                TextView qty1 = new TextView(this);
                qty1.setText("Vehicle Name: " + vehicleName);
                row1.addView(qty1);

                TableRow row2 = new TableRow(this);
                row2.setLayoutParams(lp);
                TextView qty2 = new TextView(this);
                qty2.setText("Vehicle Number: " + vehicleNumber);
                row2.addView(qty2);

                TableRow row3 = new TableRow(this);
                row3.setLayoutParams(lp);
                TextView qty3 = new TextView(this);
                qty3.setText("Vehicle Type: " + vehicleType);
                row3.addView(qty3);

                TableRow row4 = new TableRow(this);
                row4.setLayoutParams(lp);
                TextView qty4 = new TextView(this);
                qty4.setText("Make: " + make);
                row4.addView(qty4);

                TableRow row5 = new TableRow(this);
                row5.setLayoutParams(lp);
                TextView qty5 = new TextView(this);
                qty5.setText("Next Service: " + nextService);
                row5.addView(qty5);

                TableRow row6 = new TableRow(this);
                row6.setLayoutParams(lp);
                TextView qty6 = new TextView(this);
                qty6.setText("Notes: " + notes);
                row6.addView(qty6);

                t.addView(row1);
                t.addView(row2);
                t.addView(row3);
                t.addView(row4);
                t.addView(row5);
                t.addView(row6);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}


