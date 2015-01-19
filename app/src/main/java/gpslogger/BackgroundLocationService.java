package gpslogger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import luka.cyclingmaster.TrackingActivity;
import utils.Constants;
import utils.StopWatch;

/**
 * BackgroundLocationService used for tracking user location in the background.
 *
 */
public class BackgroundLocationService extends Service {

    IBinder mBinder = new LocalBinder();

    public static LocationRequest mLocationRequest;
    public static StopWatch timer;

    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    public static final int LOCATION_TRACKING_CODE = 2864;

    public class LocalBinder extends Binder {
        public BackgroundLocationService getServerInstance() {
            return BackgroundLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInProgress = false;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);

        timer = new StopWatch();

        servicesAvailable = servicesConnected();
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        return ConnectionResult.SUCCESS == resultCode;
    }

    public int onStartCommand (Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        Log.d("TRACKER", "onStartCommand");

        if(servicesAvailable && TrackingActivity.mGoogleApiClient != null && TrackingActivity.mGoogleApiClient.isConnected() && !mInProgress) {
            startLocationUpdates();
            mInProgress = true;
            Toast.makeText(getApplicationContext(), "Logger service started!", Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }

        if(TrackingActivity.mGoogleApiClient == null || !TrackingActivity.mGoogleApiClient.isConnected() || !TrackingActivity.mGoogleApiClient.isConnecting() && !mInProgress)
        {
            //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Started", Constants.LOG_FILE);
            Log.d("TRACKING", DateFormat.getDateTimeInstance().format(new Date()) + ": NOT CONNECTED !!!");
            mInProgress = false;
        }

        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public String getTime() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mDateFormat.format(new Date());
    }

    public void appendLog(String text, String filename)
    {
        File logFile = new File(filename);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        // Turn off the request flag
        mInProgress = false;
        if(servicesAvailable && TrackingActivity.mGoogleApiClient != null) {
            stopLocationUpdates();
        }

        timer = null; // Destroy timer

        // Display the connection status
        Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        //appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Stopped", Constants.LOG_FILE);
        Log.d("TRACKING", DateFormat.getDateTimeInstance().format(new Date()) + ": Stopped");
        super.onDestroy();
    }

    protected void startLocationUpdates() {
        timer.start();

        Log.d("TRACKING", "startLocationUpdates()");
        Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": startLocationUpdates", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), LocationReceiver.class);
        PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), LOCATION_TRACKING_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                TrackingActivity.mGoogleApiClient, mLocationRequest, locationIntent);
    }

    protected void stopLocationUpdates() {
        timer.stop();

        Log.d("TRACKING", "stopLocationUpdates()");
        Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": stopLocationUpdates", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), LocationReceiver.class);
        PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), LOCATION_TRACKING_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        LocationServices.FusedLocationApi.removeLocationUpdates(
                TrackingActivity.mGoogleApiClient, locationIntent);
    }

}
