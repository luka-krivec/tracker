package utils;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class TrackerUtils {

    public static void insertPointInDatabase(int idRoute, Location loc, final Context ctx) {
        final String url = Constants.MONGODB_URL + "/tracker";
        final String paramsInsert =
                  "idRoute=" + idRoute
                + "&lat=" + loc.getLatitude()
                + "&lng=" + loc.getLongitude()
                + (loc.hasAltitude() ? "&altitude=" + loc.getAltitude() : "")
                + (loc.hasSpeed() ? "&speed=" + loc.getSpeed() : "")
                + (loc.hasBearing() ? "&bearing=" + loc.getBearing() : "")
                + (loc.hasAccuracy() ? "&accuracy=" + loc.getAccuracy() : "");

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                String res = WebUtils.executePost(url, paramsInsert);
                Log.d("TRACKER insertPoint", res);

                return null;
            }
        }.execute();
    }

    /**
     * Insert last N points in MongoDB. N is defined in Contants.java
     * @param idRoute
     * @param loggedLocations
     */
    public static void insertPointsInDatabase(final int idRoute, ArrayList<Location> loggedLocations) {
        final String url = Constants.MONGODB_URL + "/tracker";

        StringBuilder latsBuilder = new StringBuilder();
        StringBuilder lonsBuilder = new StringBuilder();
        int n = 0;

        for(int i = loggedLocations.size()-1; i >= 0; i--) {
            if(n == Constants.INSERT_N_POINTS_WRITE_DB) {
                break;
            }
            Location loc = loggedLocations.get(i);
            latsBuilder.append(loc.getLatitude() + ",");
            lonsBuilder.append(loc.getLongitude() + ",");
            n++;
        }

        String lats = latsBuilder.toString().substring(0, latsBuilder.length()-1);
        String lons = lonsBuilder.toString().substring(0, lonsBuilder.length()-1);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                String paramsInsert =
                        "idRoute=" + idRoute
                        + "&lats=" + params[0]
                        + "&lons=" + params[1];
                String res = WebUtils.executePost(url, paramsInsert);
                Log.d("TRACKER insertPoints", res);

                return null;
            }
        }.execute(lats, lons);
    }
}
