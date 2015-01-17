package gpslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import luka.cyclingmaster.TrackingActivity;
import utils.TrackerUtils;
import utils.WebUtils;

public class LocationReceiver extends BroadcastReceiver {

    public static ArrayList<Location> loggedLocations = new ArrayList<>();
    private ArrayList<LatLng> loggedLatLng = new ArrayList<>();
    private Location lastLocation;
    private String lastUpdateTime;
    public static double currentDistance = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        logLocation(location);
    }

    private void logLocation(Location loc) {
        if(loc != null) {
            Log.d("MyLocationListener", "Latitude: " + loc.getLatitude() + ", Logitude: " + loc.getLongitude());
            //Toast.makeText(this, "Latitude: " + loc.getLatitude() + ", Logitude: " + loc.getLongitude(), Toast.LENGTH_LONG).show();

            if(lastLocation != null) {
                currentDistance += lastLocation.distanceTo(loc);
            }

            loggedLocations.add(loc);
            lastLocation = loc;
            lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            loggedLatLng.add(new LatLng(loc.getLatitude(), loc.getLongitude()));

            if(TrackingActivity.liveTracking) {
                TrackerUtils.insertPointInDatabase(TrackingActivity.idRoute, loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
            }
        }
    }
}
