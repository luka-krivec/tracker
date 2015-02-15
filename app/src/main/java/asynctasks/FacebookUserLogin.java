package asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import si.krivec.tracker.MainActivity;
import utils.Constants;
import utils.WebUtils;


public class FacebookUserLogin extends AsyncTask<Void, Integer, Void> {

    private String userName;
    private String userBirthday;
    private Session fbSession;

    public FacebookUserLogin(Session fbSession) {
        this.fbSession = fbSession;
    }

    @Override
    protected Void doInBackground(Void... params) {
        makeMeRequest(fbSession);
        return null;
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                userName = user.getFirstName() + (user.getMiddleName() != null ? user.getMiddleName() + " " : " ") +  user.getLastName();
                                userBirthday = user.getBirthday();
                                MainActivity.USER_FB_ID = user.getId();

                                // User sign up if necessary
                                userSignUp();
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAndWait();
    }

    private void userSignUp() {
        if (MainActivity.USER_FB_ID != null) {
            String parameters = "userFbSignUp=true"
                    + "&username=" + userName
                    + "&idFacebook=" + MainActivity.USER_FB_ID
                    + "&birthday=" + userBirthday;

            String response = WebUtils.executePost(Constants.BACKEND_URL + "/users", parameters);

            if(response != null) {
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
