package luka.cyclingmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import dialogs.ErrorConnectingDialogFragment;
import dialogs.SaveRouteDialogFragment;
import gpslogger.GpxWriter;
import utils.AndroidUtils;
import utils.Command;
import utils.DateUtilities;
import utils.DialogUtils;
import utils.StopWatch;
import utils.Utils;


public class TrackingActivity extends ActionBarActivity
        implements View.OnClickListener, SaveRouteDialogFragment.NoticeDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;
    private final int REFRESH_RATE = 1; // refresh rate for timer in seconds
    private final int REFRESH_SPEED_AND_DISTANCE = 10; // refresh speed and distance on screen every n seconds

    private StopWatch timer;
    private TextView txtStopWatch;
    private TextView txtCurrentDistance;
    private TextView txtCurrentAvgSpeed;
    private ImageButton btnStopTracking;
    private ImageButton btnPause;

    private File dirExternalStorageGpxStore;

    private boolean timerStopped = false;
    private int counterRefreshSpeedAndDistance; // to refresh speed and distance on REFRESH_SPEED_AND_DISTANCE seconds

    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";
    private static final String LOCATION_KEY = "location";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_update_time_string";

    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    private ArrayList<Location> loggedLocations;
    private ArrayList<LatLng> loggedLatLng;
    private Location lastLocation;
    private String lastUpdateTime;
    private double currentDistance = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TIMER:
                    updateUIData();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE*1000); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop(); //stop timer
                    updateUIData();
                    timerStopped = true;

                    break;
            }
        }
    };

    private void updateUIData() {
        long elapsed = timer.getElapsedTimeSecs();
        txtStopWatch.setText(DateUtilities.secondsTo_hhmmss(elapsed));

        counterRefreshSpeedAndDistance++;

        if (counterRefreshSpeedAndDistance == REFRESH_SPEED_AND_DISTANCE) {
            double currentAvgSpeed = Utils.getAverageSpeed(currentDistance, elapsed);
            txtCurrentDistance.setText(String.format("%.2f", currentDistance / 1000));
            txtCurrentAvgSpeed.setText(String.format("%.2f", currentAvgSpeed));
            counterRefreshSpeedAndDistance = 0;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        createLocationRequest();
        updateValuesFromBundle(savedInstanceState);

        txtStopWatch = (TextView) findViewById(R.id.txtStopWatch);
        txtCurrentDistance = (TextView) findViewById(R.id.txtCurrentDistance);
        txtCurrentAvgSpeed = (TextView) findViewById(R.id.txtCurrentAvgSpeed);

        btnPause = (ImageButton) findViewById(R.id.btnPauseTimer);
        btnPause.setOnClickListener(this);

        btnStopTracking = (ImageButton) findViewById(R.id.btnStopTracking);
        btnStopTracking.setOnClickListener(this);

        if(loggedLocations == null) {
            loggedLocations = new ArrayList<>();
        }
        if(loggedLatLng == null) {
            loggedLatLng = new ArrayList<>();
        }

        // Initialize folder on SD card for saving GPX files
        if(AndroidUtils.isExternalStorageWritable()) {
            File dirExternalStorageRoot = getExternalFilesDir(null);
            dirExternalStorageGpxStore = new File(dirExternalStorageRoot.getAbsolutePath() + "/gpx");
            dirExternalStorageGpxStore.mkdirs();
        } else {
            Toast.makeText(this, R.string.external_storage_cant_write, Toast.LENGTH_LONG).show();
            finish();
        }


        // Check if GPS is enabled
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            buildGoogleApiClient();

            if (savedInstanceState != null) {
                Log.i("TrackingActivity: timer elapsed time = ", timer.getElapsedTime() + " s");

                if (timerStopped) {
                    timer.resume();
                    timerStopped = false;
                }

                // Screen timer update
                mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);

                txtStopWatch.setText(savedInstanceState.getString("currentTime"));
                Log.i("TrackingActivity: read time = ", txtStopWatch.getText().toString());
                Log.i("TrackingActivity: timer time = ", timer.getElapsedTimeSecs() + "");
                txtCurrentDistance.setText(savedInstanceState.getString("currentDistance"));
                txtCurrentAvgSpeed.setText(savedInstanceState.getString("currentAvgSpeed"));
            } else {
                txtStopWatch.setText("00:00:00");
                txtCurrentDistance.setText("0,00");
                txtCurrentAvgSpeed.setText("0,00");

                counterRefreshSpeedAndDistance = 0;
                timer = new StopWatch();
            }
        } else {
            Command command = new Command() {
                public void execute() {
                    // Execute code on YES click
                    enableLocationSettings();
                    finish();
                }
            };

            String gpsDisabledWarningMessage = getApplicationContext().getResources().getString(R.string.gps_disabled_settings_open);
            AlertDialog alertDialog = DialogUtils.createWaitDialog(TrackingActivity.this,gpsDisabledWarningMessage, command);

            try {
                alertDialog.show();
            } catch (Exception ex) {
                Log.d("GPSLoggerService - enable GPS dialog show error: ", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, lastLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, lastUpdateTime);

        if(timer != null) {

            // Stop timer
            if(!timerStopped) {
                timer.stop();
                mHandler.sendEmptyMessage(MSG_STOP_TIMER);
                timerStopped = true;
            }

            savedInstanceState.putString("currentTime", txtStopWatch.getText().toString());
            Log.i("TrackingActivity: saved time = ", txtStopWatch.getText().toString());
            Log.i("TrackingActivity: timer time = ", timer.getElapsedTimeSecs() + "");
            savedInstanceState.putString("currentDistance", txtCurrentDistance.getText().toString());
            savedInstanceState.putString("currentAvgSpeed", txtCurrentAvgSpeed.getText().toString());
            savedInstanceState.putSerializable("timer", timer);
            savedInstanceState.putBoolean("timerStopped", timerStopped);
        }

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // lastLocation not null.
                lastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of lastUpdateTime from the Bundle.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                lastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(STATE_RESOLVING_ERROR)) {
                mResolvingError = savedInstanceState.getBoolean(
                        STATE_RESOLVING_ERROR, false);
            }

            timerStopped = savedInstanceState.getBoolean("timerStopped");
            timer = (StopWatch) savedInstanceState.getSerializable("timer");
        }
    }

    private void setButtonsEnabledState() {
        btnPause.setEnabled(true);
        btnStopTracking.setEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracking, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStopTracking:
                if (!timerStopped) {
                    timer.stop();
                    mHandler.sendEmptyMessage(MSG_STOP_TIMER);
                    showSaveRouteDialog();
                }
                break;
            case R.id.btnPauseTimer:
                pauseTimer();
                break;
        }
    }

    private void showSaveRouteDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SaveRouteDialogFragment saveRouteDialog = new SaveRouteDialogFragment();
        saveRouteDialog.show(fm, "fragment_save_route");
    }

    @Override
    public void onDialogSaveRoutePositiveClick(DialogFragment dialog, String routeName) {
        stopTracking(true, routeName);
    }

    @Override
    public void onDialogSaveRouteNegativeClick(DialogFragment dialog) {
        stopTracking(false, "");
    }

    private void stopTracking(boolean save, String routeName) {
        stopLocationUpdates();

        if(save) {
            boolean saveStatus = saveTrackingData(loggedLocations, routeName, timer.getElapsedTime(),
                    new Date(timer.getStartTime()), new Date(timer.getStopTime()));

            if(saveStatus) {
                Toast.makeText(this, getResources().getString(R.string.route_saved), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.route_save_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean saveTrackingData(ArrayList<Location> locations, String fileName, long timeDiff, Date dateStart, Date dateEnd) {

        String currentDate =  DateUtilities.getCurrentDate();

        // Create folder with current date, if it not exists
        File savedDir = new File(dirExternalStorageGpxStore, currentDate);
        savedDir.mkdirs();

        // Save GPX file in previous created folder
        File savedGpx = new File(savedDir, fileName + ".gpx");
        Document gpxDoc = GpxWriter.CreateGpxFile(locations, timeDiff, dateStart, dateEnd, fileName + ".gpx", "");
        boolean gpxSaveSuccess = GpxWriter.writeGpxToExternalStorage(gpxDoc, savedGpx);

        // Saved calculated data
        File savedGpxData = new File(savedDir, fileName + ".dat");
        boolean gpxDataSaveSuccess = GpxWriter.saveCyclingRouteData(savedGpxData, GpxWriter.cyclingRoute);

        if(gpxSaveSuccess) {
            Log.d("Tracking completed", "File gpx/" + currentDate + "/" + fileName + ".gpx saved.");
        } else {
            Log.d("Tracking completed", "ERROR !!! " + GpxWriter.GPX_ERROR);
        }

        if(gpxDataSaveSuccess) {
            Log.d("Service completed", "File gpx/" + currentDate + "/" + fileName + ".dat saved.");
        } else {
            Log.d("Service completed", "ERROR !!! " + GpxWriter.GPX_DATA_ERROR);
        }

        boolean saveStatus = gpxSaveSuccess && gpxDataSaveSuccess;
        return saveStatus;
    }

    public void pauseTimer() {
        if (timer != null) {
            if (btnPause.getTag() != null && btnPause.getTag().equals("Pause")) {
                timer.resume();
                btnPause.setTag("Start");
                btnPause.setImageResource(R.drawable.button_pause);
            } else {
                timer.pause();
                btnPause.setTag("Pause");
                btnPause.setImageResource(R.drawable.button_launcher_start_flat);

            }
        } else {
            Log.d("TrackingActivity timer is null", "NULL TIMER WARNING");
        }
    }

    // Open settings for enable GPS
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }


    // Google API Client methods
    // ******
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services!
        Log.d("Google Play Services", "CONNECTED");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
            timer.start();
            mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
        }
    }

    protected void startLocationUpdates() {
        Log.d("Tracking", "startLocationUpdates()");

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
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
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.d("Google Play Services", "CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Google Play Services", "CONNECTION FAILED");

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorConnectingDialogFragment dialogFragment = new ErrorConnectingDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorConnectingDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }
    // ******

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

}
