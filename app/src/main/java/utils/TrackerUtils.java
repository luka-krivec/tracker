package utils;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class TrackerUtils {

    public static void insertPointInDatabase(int idRoute, double lat, double lng, double altitude) {
        final String url = Constants.BACKEND_URL + "/tracker";
        final String paramsInsert = "idRoute=" + idRoute + "&lat=" + lat + "&lng=" + lng + "&altitude=" + altitude;

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
