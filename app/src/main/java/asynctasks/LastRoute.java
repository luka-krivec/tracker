package asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import si.krivec.tracker.MainActivity;
import si.krivec.tracker.R;
import utils.Constants;
import utils.WebUtils;


public class LastRoute extends AsyncTask<Void, Integer, Boolean> {

    Activity selectionActivity;
    private final String url = Constants.BACKEND_URL + "/routes";

    private TextView txtStartRouteNameValue;
    private TextView txtStartRouteDistanceValue;
    private TextView txtStartRouteAvgSpeedValue;
    private TextView txtStartRouteStartTimeValue;
    private TextView txtStartRouteEndTimeValue;

    public LastRoute(Activity selectionActivity) {
        this.selectionActivity = selectionActivity;

        /*txtStartRouteNameValue = (TextView) selectionActivity.findViewById(R.id.txtStartRouteNameValue);
        txtStartRouteDistanceValue = (TextView) selectionActivity.findViewById(R.id.txtStartRouteDistanceValue);
        txtStartRouteAvgSpeedValue = (TextView) selectionActivity.findViewById(R.id.txtStartRouteAvgSpeedValue);
        txtStartRouteStartTimeValue = (TextView) selectionActivity.findViewById(R.id.txtStartRouteStartTimeValue);
        txtStartRouteEndTimeValue = (TextView) selectionActivity.findViewById(R.id.txtStartRouteEndTimeValue);*/
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean result = false;
        String paramsGet = "getLastRoute=true" + "&idFacebook=" + MainActivity.USER_FB_ID;

        // Get user last route
        final String res = WebUtils.executePost(url, paramsGet);

        try {
            JSONObject json = new JSONObject(res);

            if(json != null && json.getString("success") != null && json.getString("success").length() > 0) {
                selectionActivity.runOnUiThread(new Runnable() {
                    JSONObject json = new JSONObject(res);
                    SimpleDateFormat formatter = new SimpleDateFormat("d.M.yyyy HH:mm");
                    SimpleDateFormat parseFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

                    @Override
                    public void run() {
                        try {
                            txtStartRouteNameValue.setText(json.getString("routeName"));
                            txtStartRouteDistanceValue.setText(String.format("%.2f", json.getDouble("distance")));
                            txtStartRouteAvgSpeedValue.setText(String.format("%.2f", json.getDouble("averageSpeed")));
                            txtStartRouteStartTimeValue.setText(formatter.format(parseFormatter.parse(json.getString("startTime").replace("\"", ""))));
                            txtStartRouteEndTimeValue.setText(formatter.format(parseFormatter.parse(json.getString("endTime").replace("\"", ""))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException pex) {
                            pex.printStackTrace();
                        }

                    }
                });

            }

            result = true;
        } catch (JSONException ex) {
            Log.d("RouteUtils", ex.getMessage());
        }

        return result;
    }

}
