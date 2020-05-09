package si.krivec.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnLauncherStart;
    private ImageButton btnLauncherActivities;
    private Button btnLiveTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        FacebookSdk.sdkInitialize(this);

        btnLauncherStart = (ImageButton) findViewById(R.id.btnLauncherStart);
        btnLauncherStart.setOnClickListener(this);

        btnLauncherActivities = (ImageButton) findViewById(R.id.btnLauncherActivities);
        btnLauncherActivities.setOnClickListener(this);

        btnLiveTracker = (Button) findViewById(R.id.btnLiveTracker);
        btnLiveTracker.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);

        // AD targeting
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("2442541079195F1861C00CFC0724E833")
                //.addTestDevice("TEST_EMULATOR")
                .build();

        mAdView.loadAd(adRequest);
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
            //case R.id.action_settings:
            //    return true;
            case R.id.action_logout:
                LoginManager.getInstance().logOut();
                Intent loginActivity = new Intent(this, MainActivity.class);
                startActivity(loginActivity);
                return true;
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
                SharedPreferences pref = getApplicationContext().getSharedPreferences("TrackerConf", 0); // 0 - for private mode
                boolean signInWithEmailAndPassword = pref.getBoolean("signInWithEmailAndPassword", false);

                if(!signInWithEmailAndPassword) {
                    Intent friendsActivity = new Intent(this, FriendsActivity.class);
                    startActivity(friendsActivity);
                } else {
                    Toast.makeText(this, R.string.login_with_facebook, Toast.LENGTH_LONG).show();
                }

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
