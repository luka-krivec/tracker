package si.krivec.tracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import utils.GpxParser;


public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    private LatLng[] arrayLatLng;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        String gpxFile = getIntent().getStringExtra("gpxFile");
        GpxParser gpxParser = new GpxParser(gpxFile);
        arrayLatLng = gpxParser.getArrayLatLng();

        setUpMapIfNeeded();
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
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(arrayLatLng != null && arrayLatLng.length > 0) {
            LatLng startPosition = null;
            PolygonOptions routeDriven = new PolygonOptions();

            for(int i=0; i<arrayLatLng.length; i++) {
                if(i == 0)
                    startPosition = arrayLatLng[i];
                routeDriven.add(arrayLatLng[i]);
            }

            // Set marker on start of the route
            //mMap.addMarker(new MarkerOptions().position(startPosition).title("Start"));

            CameraPosition startCameraPosition = new CameraPosition.Builder()
                    .target(startPosition)
                    .zoom(10.5f)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(startCameraPosition));


            //  Drive route
            routeDriven.strokeColor(getResources().getColor(R.color.colorPrimary));
            routeDriven.strokeWidth(5);

            mMap.addPolygon(routeDriven);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
