package dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.Session;

import si.krivec.tracker.ListRoutesActivity;
import si.krivec.tracker.PlansActivity;
import si.krivec.tracker.R;
import si.krivec.tracker.SignUpActivity;
import si.krivec.tracker.TrackingActivity;


public class SelectionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SelectionFragment";

    private ImageButton btnLauncherStart;
    private ImageButton btnLauncherActivities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_selection, container, false);

        btnLauncherStart = (ImageButton) view.findViewById(R.id.btnLauncherStart);
        btnLauncherStart.setOnClickListener(this);

        btnLauncherActivities = (ImageButton) view.findViewById(R.id.btnLauncherActivities);
        btnLauncherActivities.setOnClickListener(this);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLauncherStart:
                Intent trackingActivity = new Intent(getActivity().getApplicationContext(), TrackingActivity.class);
                startActivity(trackingActivity);
                break;
            case R.id.btnLauncherActivities:
                Intent listRoutesActivity = new Intent(getActivity().getApplicationContext(), ListRoutesActivity.class);
                startActivity(listRoutesActivity);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selection, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;
            /*case R.id.action_plans:
                Intent plansActivity = new Intent(getActivity().getApplicationContext(), PlansActivity.class);
                startActivity(plansActivity);
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }
}
