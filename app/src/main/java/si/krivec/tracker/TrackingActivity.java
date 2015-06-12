package si.krivec.tracker;

import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Document;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import dialogs.ErrorConnectingDialogFragment;
import dialogs.SaveRouteDialogFragment;
import gpslogger.BackgroundLocationService;
import gpslogger.GpxWriter;
import gpslogger.LocationReceiver;
import utils.AndroidUtils;
import utils.Command;
import utils.DateUtilities;
import utils.DialogUtils;
import utils.RoutesUtils;
import utils.Utils;


public class TrackingActivity extends ActionBarActivity
        implements View.OnClickListener, SaveRouteDialogFragment.NoticeDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;
    private final int REFRESH_RATE = 1; // refresh rate for timer in seconds
    private final int REFRESH_SPEED_AND_DISTANCE = 10; // refresh speed and distance on screen every n seconds

    private TextView txtStopWatch;
    private TextView txtCurrentDistance;
    private TextView txtCurrentAvgSpeed;
    private ImageButton btnStopTracking;
    private ImageButton btnPause;
    private CheckBox chkEnableLiveTracking;

    private File dirExternalStorageGpxStore;

    private int counterRefreshSpeedAndDistance; // to refresh speed and distance on REFRESH_SPEED_AND_DISTANCE seconds

    public static GoogleApiClient mGoogleApiClient;
    public static boolean liveTracking = false;

    // Id route for further update route
    public static int idRoute = 0;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    public static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";
    private static final String STATE_LIVE_TRACKING = "live_tracking";

    private boolean mRequestingLocationUpdates = true;
    private boolean mResolvingError = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TIMER:
                    updateUIData();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE*1000); //text view is updated every REFRESH_RATE seconds,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    stopStopWatch();
                    updateUIData();
                    break;
            }
        }
    };

    private void stopStopWatch() {
        if(BackgroundLocationService.timer != null) {
            BackgroundLocationService.timer.stop();
            mHandler.removeMessages(MSG_UPDATE_TIMER); // No more updates.
        }
    }

    private void pauseStopWatch() {
        if(BackgroundLocationService.timer != null) {
            BackgroundLocationService.timer.pause();
            mHandler.removeMessages(MSG_UPDATE_TIMER); // No more updates.
        }
    }

    private void resumeStopWatch() {
        if(BackgroundLocationService.timer != null) {
            BackgroundLocationService.timer.resume();
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE * 1000); //text view is updated every REFRESH_RATE seconds,
        }
    }

    private void updateUIData() {
        if(BackgroundLocationService.timer != null) {
            long elapsed = BackgroundLocationService.timer.getElapsedTimeSecs();
            txtStopWatch.setText(DateUtilities.secondsTo_hhmmss(elapsed));

            counterRefreshSpeedAndDistance++;

            if (counterRefreshSpeedAndDistance == REFRESH_SPEED_AND_DISTANCE) {
                double currentDistance = LocationReceiver.currentDistance;
                double currentAvgSpeed = Utils.getAverageSpeed(currentDistance, elapsed);

                txtCurrentDistance.setText(String.format("%.1f", currentDistance / 1000));
                txtCurrentAvgSpeed.setText(String.format("%.1f", currentAvgSpeed));
                counterRefreshSpeedAndDistance = 0;

                Log.d("TRACKER", "Distance: " + currentDistance);
                Log.d("TRACKER", "Time: " + elapsed);
                Log.d("TRACKER", "Average speed: " + currentAvgSpeed);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        txtStopWatch = (TextView) findViewById(R.id.txtStopWatch);
        txtCurrentDistance = (TextView) findViewById(R.id.txtCurrentDistance);
        txtCurrentAvgSpeed = (TextView) findViewById(R.id.txtCurrentAvgSpeed);

        btnPause = (ImageButton) findViewById(R.id.btnPauseTimer);
        btnPause.setOnClickListener(this);

        btnStopTracking = (ImageButton) findViewById(R.id.btnStopTracking);
        btnStopTracking.setOnClickListener(this);

        chkEnableLiveTracking = (CheckBox) findViewById(R.id.chkEnableLiveTracking);
        chkEnableLiveTracking.setOnClickListener(this);

        updateValuesFromBundle(savedInstanceState);

        if(BackgroundLocationService.timer == null || BackgroundLocationService.timer.isRunning()) {
            btnPause.setTag("Start");
            btnPause.setImageResource(R.drawable.button_pause);
        } else {
            btnPause.setTag("Pause");
            btnPause.setImageResource(R.drawable.button_launcher_start_flat);
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

            // Screen timer update
            mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
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
                Log.d("GPS dialog show error: ", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
    }

    private void pauseLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationReceiver.class);
        PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), BackgroundLocationService.LOCATION_TRACKING_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, locationIntent);
        mRequestingLocationUpdates = false;

        pauseStopWatch();

        Log.d("TRACKING", "removeLocationUpdates()");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void resumeLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationReceiver.class);
        PendingIntent locationIntent = PendingIntent.getBroadcast(getApplicationContext(), BackgroundLocationService.LOCATION_TRACKING_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, BackgroundLocationService.mLocationRequest, locationIntent);
        mRequestingLocationUpdates = true;

        resumeStopWatch();

        Log.d("TRACKING", "requestLocationUpdates()");
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
        savedInstanceState.putBoolean(STATE_LIVE_TRACKING, chkEnableLiveTracking.isChecked());
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

            if (savedInstanceState.keySet().contains(STATE_RESOLVING_ERROR)) {
                mResolvingError = savedInstanceState.getBoolean(
                        STATE_RESOLVING_ERROR, false);
            }

            if (savedInstanceState.keySet().contains(STATE_LIVE_TRACKING)) {
                chkEnableLiveTracking.setChecked(savedInstanceState.getBoolean(
                        STATE_LIVE_TRACKING, false));
            }
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
                stopStopWatch(); // To save more exact stop time (otherwise timer is stopped when user enters route name and click save)
                btnPause.setEnabled(false);
                showSaveRouteDialog();
                break;
            case R.id.btnPauseTimer:
                pauseTimer();
                break;
            case R.id.chkEnableLiveTracking:
                liveTracking = chkEnableLiveTracking.isChecked();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        stopLocationService();

        if(save) {
            if(routeName.isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.route_name_empty), Toast.LENGTH_LONG).show();
            } else {
                long startTime = BackgroundLocationService.timer.getStartTime();
                long endTime = BackgroundLocationService.timer.getStopTime();
                long elapsed = BackgroundLocationService.timer.getElapsedTime();
                boolean saveStatus = saveTrackingData(LocationReceiver.loggedLocations, routeName, elapsed,
                        new Date(startTime), new Date(endTime));

                // Save route online
                RoutesUtils.updateRoute(1, routeName, LocationReceiver.currentDistance,
                        LocationReceiver.currentDistance/(elapsed/1000), new Timestamp(startTime), new Timestamp(endTime));

                if(saveStatus) {
                    Toast.makeText(this, getResources().getString(R.string.route_saved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.route_save_failed), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            RoutesUtils.deleteRoute(idRoute);
            Toast.makeText(this, getResources().getString(R.string.route_discared), Toast.LENGTH_LONG).show();
        }

        finish(); // Finish activity
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

        return gpxSaveSuccess && gpxDataSaveSuccess;
    }

    public void pauseTimer() {
        if (btnPause.getTag() != null && btnPause.getTag().equals("Pause")) {
            btnPause.setTag("Start");
            btnPause.setImageResource(R.drawable.button_pause);
            resumeLocationService();
        } else {
            btnPause.setTag("Pause");
            btnPause.setImageResource(R.drawable.button_launcher_start_flat);
            pauseLocationService();
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

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services!
        Log.d("Google Play Services", "CONNECTED");
        //Toast.makeText(this, "Google Play Services connected!", Toast.LENGTH_SHORT).show();

        if (mRequestingLocationUpdates) {
            startLocationService();
            mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);

        }
    }

    private void startLocationService() {
        Log.d("Tracker", "startLocationService()");
        //Toast.makeText(this, "startLocationService()", Toast.LENGTH_SHORT).show();

        // Insert new route in Database
        RoutesUtils.insertNewRoute(MainActivity.USER_FB_ID);

        Intent locationService = new Intent(this, BackgroundLocationService.class);
        startService(locationService);

        mRequestingLocationUpdates = true;
    }

    private void stopLocationService() {
        Log.d("Tracker", "stopLocationService()");
        //Toast.makeText(this, "stopLocationService()", Toast.LENGTH_SHORT).show();
        Intent locationService = new Intent(this, BackgroundLocationService.class);
        stopService(locationService);
        mRequestingLocationUpdates = false;
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
