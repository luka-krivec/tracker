package luka.cyclingmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import dialogs.SaveRouteDialogFragment;
import gpslogger.GpsLoggerService;
import gpslogger.ServiceGpxLoggerListener;
import utils.AndroidUtils;
import utils.Command;
import utils.DateUtilities;
import utils.DialogUtils;
import utils.StopWatch;
import utils.Utils;


public class TrackingActivity extends ActionBarActivity implements View.OnClickListener, SaveRouteDialogFragment.NoticeDialogListener {

    private final int MSG_STOP_TIMER = 1;
    private final int MSG_UPDATE_TIMER = 2;
    private final int REFRESH_RATE = 1; // refresh rate for timer in seconds
    private final int REFRESH_SPEED_AND_DISTANCE = 10; // refresh speed and distance on screen every n seconds

    private StopWatch timer;
    private TextView txtStopWatch;
    private TextView txtCurrentDistance;
    private TextView txtCurrentAvgSpeed;
    private ImageButton btnStopGPSLoggerService;
    private ImageButton btnPause;

    private File dirExternalStorageGpxStore;

    private boolean timerStopped = false;
    private int counterRefreshSpeedAndDistance; // to refresh speed and distance on REFRESH_SPEED_AND_DISTANCE seconds

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
            double distance = GpsLoggerService.currentDistance;
            double currentAvgSpeed = Utils.getAverageSpeed(distance, elapsed);
            txtCurrentDistance.setText(String.format("%.2f", distance / 1000));
            txtCurrentAvgSpeed.setText(String.format("%.2f", currentAvgSpeed));
            counterRefreshSpeedAndDistance = 0;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

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
            // Class that implements interface who is responisble for saving GPX file, when service is done
            ServiceGpxLoggerListener listener = new ServiceGpxLoggerListener(dirExternalStorageGpxStore);
            Intent intentGpsLoggerService = new Intent(this, GpsLoggerService.class);

            intentGpsLoggerService.putExtra("listener", listener);
            startService(intentGpsLoggerService);

            txtStopWatch = (TextView) findViewById(R.id.txtStopWatch);
            txtCurrentDistance = (TextView) findViewById(R.id.txtCurrentDistance);
            txtCurrentAvgSpeed = (TextView) findViewById(R.id.txtCurrentAvgSpeed);
            btnPause = (ImageButton) findViewById(R.id.btnPauseTimer);

            btnStopGPSLoggerService = (ImageButton) findViewById(R.id.btnGpsLoggerServiceStop);
            btnStopGPSLoggerService.setOnClickListener(this);
            btnPause.setOnClickListener(this);

            if (savedInstanceState != null) {
                timerStopped = savedInstanceState.getBoolean("timerStopped");
                timer = (StopWatch) savedInstanceState.getSerializable("timer");

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

                //timer = (StopWatch) savedInstanceState.getSerializable("timer");
                //timerStopped = savedInstanceState.getBoolean("timerStopped");

                // Resume timer
				/*if(timerStopped) {
					mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
					timerStopped = false;
				}*/
            } else {
                txtStopWatch.setText("00:00:00");
                txtCurrentDistance.setText("0,00");
                txtCurrentAvgSpeed.setText("0,00");

                counterRefreshSpeedAndDistance = 0;
                timer = new StopWatch();
                timer.start();

                mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
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
            case R.id.btnGpsLoggerServiceStop:
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

    public void pauseTimer() {
        if (timer != null) {
            if (btnPause.getTag() != null && btnPause.getTag().equals("Pause")) {
                timer.resume();
                btnPause.setTag("Start");
                btnPause.setImageResource(R.drawable.button_pause);
            } else {
                timer.pause();
                btnPause.setTag("Pause");
                btnPause.setImageResource(R.drawable.button_launcher_start);

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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String routeName) {
        stopSaveRouteService(true, routeName);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        stopSaveRouteService(false, "");
    }

    private void stopSaveRouteService(boolean save, String routeName) {
        SharedPreferences.Editor prefsEditor = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).edit();
        prefsEditor.putBoolean("serviceSave", save);
        prefsEditor.putString("savedRouteName", routeName);
        prefsEditor.putLong("timeDiff", timer.getElapsedTime());
        prefsEditor.putLong("startTime", timer.getStartTime());
        prefsEditor.putLong("stopTime", timer.getStopTime());
        prefsEditor.commit();

        // TODO: Chech why onDestroy from service is not called
        Intent intentGpsLoggerService = new Intent(this, GpsLoggerService.class);
        stopService(intentGpsLoggerService);
    }
}
