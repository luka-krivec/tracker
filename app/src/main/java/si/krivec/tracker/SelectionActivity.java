package si.krivec.tracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Session;

import org.json.JSONException;
import org.json.JSONObject;

import asynctasks.LastRoute;
import maps.LiveTracker;
import utils.Constants;
import utils.RoutesUtils;
import utils.WebUtils;


public class SelectionActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageButton btnLauncherStart;
    private ImageButton btnLauncherActivities;
    private Button btnLiveTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btnLauncherStart = (ImageButton) findViewById(R.id.btnLauncherStart);
        btnLauncherStart.setOnClickListener(this);

        btnLauncherActivities = (ImageButton) findViewById(R.id.btnLauncherActivities);
        btnLauncherActivities.setOnClickListener(this);

        btnLiveTracker = (Button) findViewById(R.id.btnLiveTracker);
        btnLiveTracker.setOnClickListener(this);

        // Fill last route data
        //LastRoute lastRouteAsync = new LastRoute(SelectionActivity.this);
        //lastRouteAsync.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;
            /*case R.id.action_plans:
                Intent plansActivity = new Intent(this, PlansActivity.class);
                startActivity(plansActivity);
                return true; */
            case R.id.action_logout:
                Session.getActiveSession().closeAndClearTokenInformation();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLauncherStart:
                Intent trackingActivity = new Intent(this, TrackingActivity.class);
                startActivity(trackingActivity);
                break;
            case R.id.btnLauncherActivities:
                Intent listRoutesActivity = new Intent(this, ListRoutesActivity.class);
                startActivity(listRoutesActivity);
                break;
            case R.id.btnLiveTracker:
                Intent liveTrackerActivity = new Intent(this, LiveTrackerActivity.class);
                startActivity(liveTrackerActivity);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Move app in background on back button pressed
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
