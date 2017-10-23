package ssadteam5.vtsapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
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

import static java.lang.StrictMath.asin;
import static java.lang.StrictMath.atan2;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.toDegrees;
import static java.lang.StrictMath.toRadians;

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
    Float courseOverGround = null;
    String organisationId;
    int flag;
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
        organisationId = claim.asString();

        mStompClient = Stomp.over(WebSocket.class,getString(R.string.websocket));
        mStompClient.connect();
        mStompClient.topic("/device/message" + organisationId).subscribe(topicMessage -> {
            JSONObject payload = new JSONObject(topicMessage.getPayload());
            try
            {
                final String deviceId = payload.get("DeviceId").toString();
                Double lat = Double.parseDouble(payload.get("Latitude").toString());
                final Double lon = Double.parseDouble(payload.get("Longitude").toString());
                final String speed = payload.get("Speed").toString();
                final String FuelLevel = payload.get("FuelLevel").toString();
                final String GSMStrength = payload.get("GSMStrength").toString();
                final String InternalBatteryVoltage = payload.get("InternalBatteryVoltage").toString();
                final String EngineStatus = payload.get("EngineStatus").toString();
                flag = 0;
                try {
                    courseOverGround = Float.parseFloat(payload.get("CourseOverGround").toString());
                }
                catch (Exception e){
                    flag = 1;
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
                                if(flag == 1)
                                    courseOverGround = getAngle(oldpos.getPosition(), new LatLng(lat, lon));
                                rotateMarker(marker, new LatLng(lat, lon), courseOverGround);
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
                            if(flag == 1)
                                courseOverGround = 0f;
                            if(deviceName.equals(deviceId)) {
                                marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
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
        else {
//            mStompClient.topic("/device/message" + organisationId).unsubscribeOn();
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mStompClient.disconnect();
        Log.d("stompinfo", mStompClient.isConnected()+"");
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

    public static float getAngle(LatLng source, LatLng destination) {

        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(
                destination.longitude - source.longitude, destination.latitude - source.latitude);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta -= Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return (float) angle + 90;
    }
}
