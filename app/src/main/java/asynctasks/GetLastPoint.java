package asynctasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;
import utils.TrackerPoint;
import utils.WebUtils;

/**
 * Created by Luka on 11.4.2015.
 */
public class GetLastPoint  extends AsyncTask<Integer, Integer, TrackerPoint> {

    private int idUser;
    final String url = Constants.BACKEND_URL + "/tracker";
    final String paramsSelect = "getLastPoint=true&idUser=";

    @Override
    protected TrackerPoint doInBackground(Integer... params) {
        idUser = params[0];
        String res = WebUtils.executePost(url, paramsSelect+idUser);

        try {
            JSONObject resJson = new JSONObject(res);
            return new TrackerPoint(resJson.getDouble("lat"), resJson.getDouble("lng"));

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
