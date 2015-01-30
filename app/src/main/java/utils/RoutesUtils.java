package utils;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import si.krivec.tracker.TrackingActivity;

/**
 * Class for helping interacting with backend for Routes management.
 */
public class RoutesUtils {

    /**
     * Insert new route for Facebook user
     * @param idFacebook Id get from Facebook for specific user (Stored in database - table Users)
     */
    public static void insertNewRoute(String idFacebook) {

        final String url = Constants.BACKEND_URL + "/routes";
        final String paramsInsert =
                "insertNewRoute=true"
              + "&idFacebook=" + idFacebook;

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                String res = WebUtils.executePost(url, paramsInsert);

                try {
                    JSONObject resJson = new JSONObject(res);
                    TrackingActivity.idRoute = resJson.getInt("idRoute");
                } catch (JSONException ex) {

                }

                Log.d("ROUTES insertNewRoute", res);

                return null;
            }
        }.execute();
    }

    /**
     * Update route data.
     * @param idRoute
     * @param routeName
     * @param distance
     * @param avgSpeed
     * @param startTime
     * @param endTime
     */
    public static void updateRoute(int idRoute, String routeName, double distance, double avgSpeed, Timestamp startTime, Timestamp endTime) {
        SimpleDateFormat noMilliSecondsFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String url = Constants.BACKEND_URL + "/routes";
        final String paramsUpdate =
                "updateRoute=true"
              + "&idRoute=" + idRoute
              + "&name=" + routeName
              + "&distance=" + String.format(Locale.ENGLISH, "%.2f", distance)
              + "&averageSpeed=" + String.format(Locale.ENGLISH, "%.2f", avgSpeed)
              + "&startTime=" + noMilliSecondsFormatter.format(startTime)
              + "&endTime=" + noMilliSecondsFormatter.format(endTime);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                String res = WebUtils.executePost(url, paramsUpdate);
                Log.d("ROUTES updateRoute", res);

                return null;
            }
        }.execute();
    }
}
