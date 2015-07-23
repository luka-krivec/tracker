package asynctasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;
import utils.WebUtils;

/**
 * Created by Luka on 22.7.2015.
 */
public class UserIsOnline extends AsyncTask<Object, Void, Integer> {

    final String url = Constants.BACKEND_URL + "/users";

    @Override
    protected Integer doInBackground(Object... params) {
        int online = 0;
        String res = WebUtils.executePost(url, "idFacebook=" + params[0] + "&isOnline=true");

        try {
            JSONObject resJson = new JSONObject(res);
            online = resJson.getInt("online");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return online;
    }
}
