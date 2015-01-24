package si.krivec.tracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.Session;


public class SelectionActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "SelectionActivity";

    private ImageButton btnLauncherStart;
    private ImageButton btnLauncherActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        btnLauncherStart = (ImageButton) findViewById(R.id.btnLauncherStart);
        btnLauncherStart.setOnClickListener(this);

        btnLauncherActivities = (ImageButton) findViewById(R.id.btnLauncherActivities);
        btnLauncherActivities.setOnClickListener(this);
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
            case R.id.action_plans:
                Intent plansActivity = new Intent(this, PlansActivity.class);
                startActivity(plansActivity);
                return true;
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
        }
    }
}
