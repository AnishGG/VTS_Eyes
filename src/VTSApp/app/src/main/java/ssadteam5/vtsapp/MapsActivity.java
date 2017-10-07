package ssadteam5.vtsapp;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    StompClient mStompClient;
    ArrayList<Marker> markerList = new ArrayList<Marker>();
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setTitle("Map");
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        token = getIntent().getExtras().getString("token");
        JWT jwt = new JWT(token);
        Claim claim = jwt.getClaim("organisationId");
        String organisationId = claim.asString();

        mStompClient = Stomp.over(WebSocket.class,getString(R.string.websocket));
        mStompClient.connect();
        mStompClient.topic("/device/message" + organisationId).subscribe(topicMessage -> {
            JSONObject payload = new JSONObject(topicMessage.getPayload());
            try
            {
                final String deviceName = payload.get("DeviceId").toString();
                Double lat = Double.parseDouble(payload.get("Latitude").toString());
                Double lon = Double.parseDouble(payload.get("Longitude").toString());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        boolean New = true;
                        for (int i = 0; i < markerList.size(); i++)
                        {
                            Log.d("test", deviceName);
                            Log.d("test", markerList.get(i).getTag().toString());
                            if (deviceName.equals(markerList.get(i).getTag().toString()))
                            {
                                animateMarker(markerList.get(i),new LatLng(lat,lon));
                                New = false;
                            }
                        }
                        if (New)
                        {
                            Marker amarker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title(deviceName));
                            amarker.setTag(deviceName);
                            markerList.add(amarker);
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
                Log.d("test","marker clicked");
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
                    mGoogleMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .add(oldPos,marker.getPosition()));
                }
            }
        });
    }
}

