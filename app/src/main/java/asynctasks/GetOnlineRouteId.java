package asynctasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;
import utils.WebUtils;

/**
 * Created by Luka on 23.7.2015.
 */
public class GetOnlineRouteId extends AsyncTask<String, Void, Integer> {

    final String url = Constants.BACKEND_URL + "/users";

    @Override
    protected Integer doInBackground(String... params) {
        int onlineRouteId = 0;
        String res = WebUtils.executePost(url, "idFacebook=" + params[0] + "&getOnlineRouteId=true");

        try {
            JSONObject resJson = new JSONObject(res);
            onlineRouteId = resJson.getInt("onlineRouteId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return onlineRouteId;
    }
}
