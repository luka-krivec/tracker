package asynctasks;

import android.os.AsyncTask;

import si.krivec.tracker.TrackingActivity;
import utils.Constants;
import utils.WebUtils;

/**
 * Created by Luka on 22.7.2015.
 */
public class UserSetOnline extends AsyncTask<Object, Void, Void> {

    final String url = Constants.BACKEND_URL + "/users";

    @Override
    protected Void doInBackground(Object... params) {
        WebUtils.executePost(url, "idFacebook=" + params[0] + "&setOnline=" + params[1] + "&onlineRouteId=" + TrackingActivity.idRoute);
        return null;
    }
}
