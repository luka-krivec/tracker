package si.krivec.tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.util.concurrent.ExecutionException;

import asynctasks.GetOnlineRouteId;
import asynctasks.GetUserLiveLocation;
import utils.Constants;
import utils.TrackerPoint;


public class LiveTrackerActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private MapFragment mapFragment;
    private String idFacebook;
    private Handler mHandler;

    private final int MSG_UPDATE_MAP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracker);

        AdBuddiz.showAd(this);

        idFacebook = getIntent().getExtras().getString("userFbId");
        int onlineRouteId = 0;
        TrackerPoint firstPoint = null;

        try {
            onlineRouteId = new GetOnlineRouteId().execute(idFacebook).get();
            TrackerPoint[] points = new GetUserLiveLocation().execute(onlineRouteId + "", "0").get();
            if(points != null && points.length > 0) {
                firstPoint = points[0];
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment_live);
        setUpMapIfNeeded();

        if(firstPoint != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(firstPoint.getLat(), firstPoint.getLng()))
                    .zoom(10.5f)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        if(onlineRouteId > 0) {
            Intent liveTrackingService = new Intent(this, LiveTrackingService.class);
            liveTrackingService.putExtra("onlineRouteId", onlineRouteId);

            startService(liveTrackingService);

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_UPDATE_MAP:
                            PolylineOptions routeDriven = new PolylineOptions();
                            if(LiveTrackingService.livePoints != null && LiveTrackingService.livePoints.length > 0) {
                                for (int i = 0; i < LiveTrackingService.livePoints.length; i++) {
                                    TrackerPoint point = LiveTrackingService.livePoints[i];
                                    routeDriven.add(new LatLng(point.getLat(), point.getLng()));
                                }

                                TrackerPoint lastPoint = LiveTrackingService.livePoints[LiveTrackingService.livePoints.length-1];
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(lastPoint.getLat(), lastPoint.getLng()))
                                        .zoom(mMap.getCameraPosition().zoom)
                                        .build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                routeDriven.color(Color.RED);
                                routeDriven.width(5);
                                mMap.addPolyline(routeDriven);
                            }
                            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_MAP, Constants.LIVE_TRACKING_UPDATE_INTERVAL*1000);
                            break;
                    }
                }
            };

            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_MAP, Constants.LIVE_TRACKING_UPDATE_INTERVAL*1000);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
            }
        }
    }

    private void setUpMap(LatLng startPosition) {
        CameraPosition startCameraPosition = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(10.5f)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(startCameraPosition));
        mMap.addMarker(new MarkerOptions().position(startPosition));
    }

    @Override
    protected void onDestroy() {
        Intent liveTrackingService = new Intent(this, LiveTrackingService.class);
        stopService(liveTrackingService);
        super.onDestroy();
    }
}
