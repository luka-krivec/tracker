package authentication;


import utils.WebUtils;

public class CyclingMasterServerAuthenticate implements ServerAuthenticate {

    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
        String url = "http://cyclingmaster-mobilebackend.rhcloud.com/users";
        String params = "usersignup=true&username" + name + "&email=" + email + "&pass=" + pass + "&authType=" + authType;
        // TODO: Handle sign up result
        WebUtils.excutePost(url, params);
        return null;
    }

    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {
        String url = "http://cyclingmaster-mobilebackend.rhcloud.com/users";
        String params = "userloginp=true&email=" + user + "&pass=" + pass + "&authType=" + authType;
        // TODO: Handle sign in result
        WebUtils.excutePost(url, params);
        return null;
    }
}
