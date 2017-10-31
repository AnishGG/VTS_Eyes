package ssadteam5.vtsapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class TrackVehicleActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback
{
    private SlidingUpPanelLayout mLayout;
    private UserData userData;
    private GoogleMap mGoogleMap;
    private StompClient mStompClient;
    private String deviceName;
    private Marker marker = null;
    private Float courseOverGround = null;
    private Double Lat;
    private Double Lon;
    private int mapArea = 0;
    private int panelHeight;
    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_vehicle);
        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        UserSessionManager session = new UserSessionManager(getApplicationContext());
        userData = new UserData(getApplicationContext());
        mapFrag.getMapAsync(this);
        actionBar = getSupportActionBar();
        String token = session.getUserDetails().get(UserSessionManager.KEY_TOKEN);
        deviceName = getIntent().getExtras().getString("deviceName");
        getSupportActionBar().setTitle(deviceName);
        setPanelText();     // For setting slidding panel text
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("baby", "I am clicked");
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("TAG", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("TAG", "onPanelStateChanged " + newState);
            }

        });
        panelHeight = mLayout.getPanelHeight();

        JWT jwt = new JWT(token);
        Claim claim = jwt.getClaim("organisationId");
        String organisationId = claim.asString();

        mStompClient = Stomp.over(WebSocket.class,getString(R.string.web_socket));
        mStompClient.connect();
        mStompClient.topic("/device/message" + organisationId).subscribe(topicMessage -> {
            JSONObject payload = new JSONObject(topicMessage.getPayload());
            try {
                final String deviceId = payload.get("DeviceId").toString();
                Lat = Double.parseDouble(payload.get("Latitude").toString());
                Lon = Double.parseDouble(payload.get("Longitude").toString());
                final String speed = payload.get("Speed").toString();
                final String FuelLevel = payload.get("FuelLevel").toString();
                final String GSMStrength = payload.get("GSMStrength").toString();
                final String InternalBatteryVoltage = payload.get("InternalBatteryVoltage").toString();
                final String EngineStatus = payload.get("EngineStatus").toString();
                try {
                    courseOverGround = Float.parseFloat(payload.get("CourseOverGround").toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        boolean New = true;
                        if(marker != null) {
                            if (deviceId.equals(marker.getTag().toString())) {
                                Marker oldpos = marker;
                                oldpos.setSnippet("Engine Status: " + EngineStatus + "\n" + "Speed: " + speed + "Km/h" + "\n" + "Fuel Level: " + FuelLevel + "\n" + "Internal Battery Voltage: " + InternalBatteryVoltage + "\n" +
                                        "GSM Strength: " + GSMStrength);
                                /* To update the opened Info window */
                                if(oldpos.isInfoWindowShown()) {
                                    oldpos.hideInfoWindow();
                                    oldpos.showInfoWindow();
                                }
                                /**/
                                rotateMarker(marker, new LatLng(Lat, Lon), courseOverGround);
                                mGoogleMap.addCircle(new CircleOptions()
                                        .center(oldpos.getPosition())
                                        .radius(2)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.RED));
                                New = false;
                            }
                        }
                        if (New)
                        {
                            if(deviceName.equals(deviceId)) {
                                marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Lat, Lon))
                                        .title(deviceId));
                                int height = 140;
                                int width = 70;
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.car4);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                marker.setTag(deviceId);
                                marker.setSnippet("Engine Status: " + EngineStatus + "\n" + "Speed: " + speed + "Km/h" + "\n" + "Fuel Level: " + FuelLevel + "\n" + "Internal Battery Voltage: " + InternalBatteryVoltage + "\n" +
                                        "GSM Strength: " + GSMStrength);
                                marker.setAnchor(0.5f, 0.5f);
                                marker.setInfoWindowAnchor(0.5f, 0.5f);
                                marker.setRotation(courseOverGround);
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                            }
                        }
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMapClick(LatLng point){
        if(mapArea == 0){
            actionBar.hide();
            mapArea = 1;
            //            mLayout.setPanelHeight(0);
        }
        else{
            actionBar.show();
//            mLayout.setPanelHeight(panelHeight);
            Log.d("ThisIsTheHeight", panelHeight+"");
            mapArea = 0;
        }
        Log.d("ThePointIs", point.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(17.9,78);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
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
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        mGoogleMap.setOnMapClickListener(this);
    }

    private void init(){
        mLayout = findViewById(R.id.sliding_layout);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else {
//            mStompClient.topic("/device/message" + organisationId).unsubscribeOn();
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStompClient.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private void rotateMarker(final Marker marker, final LatLng destination, final float rotation) {

        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();
            final long start = SystemClock.uptimeMillis();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);
                        if(elapsed > 3000){
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;
        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
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

            TableLayout t = findViewById(R.id.vehicleInformationPanel);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//            lp.weight = 2f;
            row.setLayoutParams(lp);
            TextView qty = new TextView(this);
            qty.setText("Device: " + deviceName);
            qty.setTextAppearance(this,R.style.TextAppearance_AppCompat_Large);
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
                qty1.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
                row1.addView(qty1);

                TableRow row2 = new TableRow(this);
                row2.setLayoutParams(lp);
                TextView qty2 = new TextView(this);
                qty2.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
                row2.addView(qty2);

                TableRow row3 = new TableRow(this);
                row3.setLayoutParams(lp);
                TextView qty3 = new TextView(this);
                qty3.setText("Vehicle Type: " + vehicleType);
                qty3.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
                row3.addView(qty3);

                TableRow row4 = new TableRow(this);
                row4.setLayoutParams(lp);
                TextView qty4 = new TextView(this);
                qty4.setText("Make: " + make);
                qty4.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
                row4.addView(qty4);

                TableRow row5 = new TableRow(this);
                row5.setLayoutParams(lp);
                TextView qty5 = new TextView(this);
                qty5.setText("Next Service: " + nextService);
                qty5.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
                row5.addView(qty5);

                TableRow row6 = new TableRow(this);
                row6.setLayoutParams(lp);
                TextView qty6 = new TextView(this);
                qty6.setText("Notes: " + notes);
                qty6.setTextAppearance(this,R.style.TextAppearance_AppCompat_Small);
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
