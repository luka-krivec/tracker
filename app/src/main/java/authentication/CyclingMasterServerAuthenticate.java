package authentication;


import android.util.Log;

import org.json.JSONObject;

import utils.Constants;
import utils.WebUtils;

public class CyclingMasterServerAuthenticate implements ServerAuthenticate {

    @Override
    public String userSignUp(String username, String email, String pass, String authType) throws Exception {
        String url = Constants.BACKEND_URL + "/users";
        String params = "userSignUp=true&username" + username + "&email=" + email + "&password=" + pass + "&authType=" + authType;

        String res = WebUtils.executePost(url, params);
        JSONObject jsonObject = new JSONObject(res);

        if(jsonObject == null) {
            return null;
        }

        if(jsonObject.getBoolean("success") == true) {
            return authType;
        }
        return null;
    }

    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {
        String url = Constants.BACKEND_URL + "/users";
        String params = "userlogin=true&email=" + user + "&pass=" + pass + "&authType=" + authType;
        // TODO: Handle sign in result
        WebUtils.executePost(url, params);
        return null;
    }
}
