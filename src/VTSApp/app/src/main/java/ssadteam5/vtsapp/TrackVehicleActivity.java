package ssadteam5.vtsapp;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class TrackVehicleActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private SlidingUpPanelLayout mLayout;
    UserSessionManager session;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    StompClient mStompClient;
    private String token;
    private String vehicle_id;
    private String vehicle_name; // only needed to display name on top of the activity
    private Marker marker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_vehicle);
        init();
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        session = new UserSessionManager(getApplicationContext());
        mapFrag.getMapAsync(this);
        Log.d("reached in here", "hello");

        token = session.getUserDetails().get(UserSessionManager.KEY_TOKEN); //fetching from the UserSessionManager
        vehicle_id = getIntent().getExtras().getString("vehicle_id");
        vehicle_name = getIntent().getExtras().getString("vehicle_name");
        getSupportActionBar().setTitle(vehicle_name);
        // Setting sliding panel text
        TextView mytextview = (TextView) findViewById(R.id.panel_text);
        mytextview.setText("More Information");

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
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        boolean New = true;
                        if(marker != null) {
                            if (deviceId.equals(marker.getTag().toString())) {
                                animateMarker(marker, new LatLng(lat, lon));
                                mGoogleMap.addCircle(new CircleOptions()
                                        .center(marker.getPosition())
                                        .radius(1)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.BLUE));
                                marker.setPosition(new LatLng(lat, lon));
                                New = false;
                            }
                        }
                        if (New)
                        {
                            if(vehicle_id.equals(deviceId)) {
                                marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(deviceId));
                                marker.setTag(deviceId);
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
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),18));
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
                    mGoogleMap.addCircle(new CircleOptions()
                            .center(oldPos)
                            .radius(2)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));
                }
            }
        });
    }
    public void init(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    @Override
    public void onBackPressed(){
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
}


