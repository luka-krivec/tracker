package asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;
import utils.WebUtils;


public class FacebookUserSignUp extends AsyncTask<Profile, Integer, Void> {

    @Override
    protected Void doInBackground(Profile... params) {
        userSignUp(params[0]);
        return null;
    }

    private void userSignUp(Profile profile) {
        if(profile != null) {
            String parameters = "userFbSignUp=true"
                    + "&username=" + profile.getFirstName() +
                    (profile.getMiddleName() != null ? profile.getMiddleName() + " " : " ") + profile.getLastName()
                    + "&idFacebook=" + profile.getId();

            String response = WebUtils.executePost(Constants.BACKEND_URL + "/users", parameters);

            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.getBoolean("success")) {
                        Log.d("FacebookUserLogin", "SUCCESS: " + jsonResponse.toString());
                    } else {
                        Log.d("FacebookUserLogin", "WARNING: " + jsonResponse.toString());
                    }
                } catch (JSONException jex) {
                    Log.d("FacebookUserLogin", "ERROR: " + jex.getMessage());
                }
            }
        }
    }

}
