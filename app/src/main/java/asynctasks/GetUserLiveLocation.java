package asynctasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utils.Constants;
import utils.TrackerPoint;
import utils.WebUtils;

/**
 * Created by Luka on 23.7.2015.
 */
public class GetUserLiveLocation extends AsyncTask<String, Void, TrackerPoint[]> {

    final String url = Constants.BACKEND_URL + "/tracker";

    @Override
    protected TrackerPoint[] doInBackground(String... params) {
        ArrayList<TrackerPoint> lstPoints = new ArrayList<>();
        String res = WebUtils.executePost(url, "getPoints=true&idRoute=" + params[0] + "&timestamp=" + params[1]);

        try {
            JSONObject resJson = new JSONObject(res);
            JSONArray points = resJson.getJSONArray("points");

            for(int i = 0; i < points.length(); i++) {
                JSONObject point = points.getJSONObject(i);
                lstPoints.add(new TrackerPoint(point.getDouble("lat"), point.getDouble("lon"), point.getLong("time")));
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return lstPoints.toArray(new TrackerPoint[lstPoints.size()]);
    }
}
