package utils;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TrackerUtils {

    public static void insertPointInDatabase(int idRoute, Location loc, final Context ctx) {
        final String url = Constants.BACKEND_URL + "/tracker";
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

}
