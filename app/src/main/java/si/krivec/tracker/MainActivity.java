package si.krivec.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import asynctasks.FacebookUserSignUp;


public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    CallbackManager callbackManager;
    public static String USER_FB_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                USER_FB_ID = currentProfile.getId();
                signUpUser(currentProfile);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // On AccessToken changes fetch the new profile which fires the event on
                // the ProfileTracker if the profile is different
                Profile.fetchProfileForCurrentAccessToken();
            }
        };

        if(AccessToken.getCurrentAccessToken() != null) {
            // Ensure that our profile is up to date
            Profile.fetchProfileForCurrentAccessToken();
            USER_FB_ID = Profile.getCurrentProfile().getId();
            Intent selectionActivity = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(selectionActivity);
        } else {
            loginButton.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Toast.makeText(getApplicationContext(), "Sucess login", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                                    Arrays.asList("public_profile", "user_friends"));

                            // Start app main screen
                            Intent selectionActivity = new Intent(MainActivity.this, SelectionActivity.class);
                            startActivity(selectionActivity);
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getApplicationContext(), "Cancel login", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Facebook Login error: ", e.getMessage());
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signUpUser(Profile profile) {
        USER_FB_ID = profile.getId();
        try {
            new FacebookUserSignUp().execute(profile).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("MainActivity login:", e.getMessage());
            e.printStackTrace();
        }
    }

}
