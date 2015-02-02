package gpslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import si.krivec.tracker.TrackingActivity;
import utils.TrackerUtils;

public class LocationReceiver extends BroadcastReceiver {

    // All attributes accessed in activity must be static otherwise they are override on every location receive
    public static ArrayList<Location> loggedLocations = new ArrayList<>();
    public static ArrayList<LatLng> loggedLatLng = new ArrayList<>();
    public static Location lastLocation;
    public static String lastUpdateTime;
    public static double currentDistance = 0;
    public static double maxSpeed = 0;

    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        logLocation(location);
    }

    private void logLocation(Location loc) {

        if(loc != null) {

            if(loc.hasSpeed() && loc.getSpeed() < 2) {
                Log.d("TRACKER", "Point skipped, reason speed = " + loc.getSpeed());
                Toast.makeText(ctx, "Point skipped, reason speed = " + loc.getSpeed(), Toast.LENGTH_SHORT).show();
                return;
            }

            if(loc.hasAccuracy() && loc.getAccuracy() < 10) {
                Log.d("TRACKER", "Point skipped, reason accuracy = " + loc.getAccuracy());
                Toast.makeText(ctx, "Point skipped, reason accuracy = " + loc.getAccuracy(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Log max speed
            if(loc.hasSpeed() && loc.getSpeed() > maxSpeed) {
                maxSpeed = loc.getSpeed();
                Log.d("TRACKER", "Max speed: " + maxSpeed);
            }

            Log.d("MyLocationListener", "Latitude: " + loc.getLatitude() + ", Logitude: " + loc.getLongitude());
            Toast.makeText(ctx, "lat: " + loc.getLatitude()+ ", lon:" + loc.getLongitude() +
                    ", altitude:" + loc.getAltitude() + ", acc: " + loc.getAccuracy(), Toast.LENGTH_SHORT).show();

            if(lastLocation != null) {
                currentDistance += lastLocation.distanceTo(loc);
                Toast.makeText(ctx, "Distance+: " + lastLocation.distanceTo(loc), Toast.LENGTH_SHORT).show();
            }

            loggedLocations.add(loc);
            lastLocation = loc;
            lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            loggedLatLng.add(new LatLng(loc.getLatitude(), loc.getLongitude()));

            if(TrackingActivity.liveTracking) {
                TrackerUtils.insertPointInDatabase(TrackingActivity.idRoute, loc, ctx);
            }
        }
    }
}
