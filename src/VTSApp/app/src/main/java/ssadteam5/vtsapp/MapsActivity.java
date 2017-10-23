package ssadteam5.vtsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
    UserSessionManager session;
    private String token;
    boolean isMarkerRotating = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        session = new UserSessionManager(getApplicationContext());
        token = session.getUserDetails().get(UserSessionManager.KEY_TOKEN);

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
                final Float courseOverGround = Float.parseFloat(payload.get("CourseOverGround").toString());
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
                                rotateMarker(markerList.get(i), courseOverGround);
                                animateMarker(markerList.get(i),new LatLng(lat,lon));
                                New = false;
                            }
                        }
                        if (New)
                        {
                            Marker amarker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon ))
                                    .title(deviceName));
                            int height = 160;
                            int width = 80;
                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.cars);
                            Bitmap b=bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            amarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            amarker.setTag(deviceName);
                            amarker.setAnchor(0.5f, 0.5f);
                            amarker.setInfoWindowAnchor(0.5f, 0.5f);
                            amarker.setRotation(courseOverGround);
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
                    mGoogleMap.addCircle(new CircleOptions()
                            .center(oldPos)
                            .radius(2)
                            .strokeColor(Color.RED)
                            .fillColor(Color.RED));
                }
            }
        });
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
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog()
    {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mGoogleMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
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
}

