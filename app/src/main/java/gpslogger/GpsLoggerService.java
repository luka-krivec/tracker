package gpslogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

import luka.cyclingmaster.R;


public class GpsLoggerService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocationManager mLocationManager;
    private ArrayList<Location> loggedLocations;
    private ArrayList<LatLng> loggedLatLng;
    private Location lastLocation;

    private ServiceGpxLoggerListener serviceGpxLoggerListener;

    private static final int TWO_SECONDS = 2000;
    private static final int ONE_METER = 1;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static double currentDistance = 0;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            setupLocationListeners();
        }
    }


    @Override
    public void onCreate() {
        Log.d("MyGPSLogger", "service creating");
        //Toast.makeText(this, "service creating", Toast.LENGTH_SHORT).show();

        // Ustvarimo GPS poslusalca in ustvarimo seznam za shranitev lokacij od poslusalca
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loggedLocations = new ArrayList<Location>();
        loggedLatLng = new ArrayList<LatLng>();
        currentDistance = 0;

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle extras = intent.getExtras();

            //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
            Log.d("MyGPSLogger", "service starting");

            serviceGpxLoggerListener = (ServiceGpxLoggerListener) extras.getSerializable("listener");

            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    // Set up fine and coarse location providers
    private void setupLocationListeners() {
        Location gpsLocation = null;
        Location networkLocation = null;
        mLocationManager.removeUpdates(listener);

        // Get coarse and fine location updates.
        // Request updates from both fine (gps) and coarse (network) providers.
        gpsLocation = requestUpdatesFromProvider(
                LocationManager.GPS_PROVIDER, R.string.not_support_gps);
        networkLocation = requestUpdatesFromProvider(
                LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

        // If both providers return last known locations, compare the two and use the better
        // one to update the UI.  If only one provider returns a location, use it.
        if (gpsLocation != null && networkLocation != null) {
            logLocation(getBetterLocation(gpsLocation, networkLocation));
        } else if (gpsLocation != null) {
            logLocation(gpsLocation);
        } else if (networkLocation != null) {
            logLocation(networkLocation);
        }
    }

    private void logLocation(Location loc) {
        if(loc != null) {
            Log.d("MyLocationListener", "Latitude: " + loc.getLatitude() + ", Logitude: " + loc.getLongitude());
            //Toast.makeText(this, "Latitude: " + loc.getLatitude() + ", Logitude: " + loc.getLongitude(), Toast.LENGTH_LONG).show();

            if(lastLocation != null)
                currentDistance += lastLocation.distanceTo(loc);

            loggedLocations.add(loc);
            lastLocation = loc;

            loggedLatLng.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }
    }


    /**
     * Method to register location updates with a desired location provider.  If the requested
     * provider is not available on the device, the app displays a Toast with a message referenced
     * by a resource id.
     *
     * @param provider Name of the requested provider.
     * @param errorResId Resource id for the string message to be displayed if the provider does
     *                   not exist on the device.
     * @return A previously returned {@link android.location.Location} from the requested provider,
     *         if exists.
     */
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, TWO_SECONDS, ONE_METER, listener);
            location = mLocationManager.getLastKnownLocation(provider);
        } else {
            //Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
            Log.d("requestUpdatesFromProvider", provider + " not enabled");
        }
        return location;
    }


    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            logLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    /** Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new
     *        one
     * @return The better Location object based on recency and accuracy.
     */
    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // We don't provide binding, so return null
        return null;
    }


    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(listener);

        SharedPreferences prefs = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        boolean save = prefs.getBoolean("serviceSave", false);
        String routeName = prefs.getString("savedRouteName", "default");
        long timeDiff = prefs.getLong("timeDiff", 0);
        Date dateStart = new Date(prefs.getLong("startTime", 0));
        Date dateEnd = new Date(prefs.getLong("stopTime", 0));

        if(save) {
            serviceGpxLoggerListener.onServiceCompleted(loggedLocations, routeName, timeDiff, dateStart, dateEnd);

            boolean status = serviceGpxLoggerListener.saveStatus;

            if(status)
                Toast.makeText(getApplicationContext(), "Pot uspešno shranjena", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Prišlo je do napake pri shranjevanju. Preverite ali imate dovolj prostora na pomnilniški kartici.", Toast.LENGTH_LONG).show();
        }

        Log.d("MyGPSLogger", "service done");
    }

}
