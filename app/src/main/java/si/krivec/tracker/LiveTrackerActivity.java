package si.krivec.tracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;

import utils.TrackerPoint;


public class LiveTrackerActivity extends ActionBarActivity {

    private GoogleMap mMap;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracker);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment_live);
        setUpMapIfNeeded();

        TrackerPoint point = (TrackerPoint) getIntent().getSerializableExtra("point");
        if(point != null) {
            setUpMap(new LatLng(point.getLat(), point.getLng()));
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
        int id = item.getItemId();

        if (id == R.id.action_selectUser) {
            // TODO: display friends
            return true;
        }

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


}
