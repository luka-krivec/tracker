package si.krivec.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import analytics.AnalyticsApplication;
import asynctasks.FacebookUserSignUp;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginButton loginButton;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private Button logoutButton;

    // Google Analytics
    private Tracker analyticsTracker;
    private static final String TAG = "Google Analytics";
    private static final String name = "MainActivity";

    CallbackManager callbackManager;
    public static String USER_FB_ID;

    private static final String ADDBUDDIZ_PUBLISHER_KEY = "579dda59-d218-483e-ad84-79e1fd885d2a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init Facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //Init Google Analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        analyticsTracker = application.getDefaultTracker();

        // Enable Advertising Features.
        analyticsTracker.enableAdvertisingIdCollection(true);

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("onCreate")
                .setAction("Application opened")
                .build());

        // AdBuddiz
        AdBuddiz.setPublisherKey(ADDBUDDIZ_PUBLISHER_KEY);
        AdBuddiz.cacheAds(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //logoutButton = (Button) findViewById(R.id.btnLogout);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("onCreate")
                        .setAction("onCurrentProfileChanged")
                        .build());

                USER_FB_ID = currentProfile != null ? currentProfile.getId(): "";
                signUpUser(currentProfile);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("onCreate")
                        .setAction("onCurrentAccessTokenChanged")
                        .build());

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
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Login")
                                    .setAction("Facebook login SUCCESS")
                                    .build());

                            //Toast.makeText(getApplicationContext(), "Sucess login", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                                    Arrays.asList("public_profile", "user_friends"));

                            // Start app main screen
                            Intent selectionActivity = new Intent(MainActivity.this, SelectionActivity.class);
                            startActivity(selectionActivity);
                        }

                        @Override
                        public void onCancel() {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Login")
                                    .setAction("CANCEL login")
                                    .build());
                            //Toast.makeText(getApplicationContext(), "Cancel login", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException e) {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Login")
                                    .setAction("Facebook login error: " + e.getMessage())
                                    .build());
                            //Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Facebook Login ERROR: ", e.getMessage());
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
        USER_FB_ID = profile != null ? profile.getId(): "";
        try {
            new FacebookUserSignUp().execute(profile).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("MainActivity:", e.getMessage());
            e.printStackTrace();
        }

        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Login")
                .setAction("User SIGN UP")
                .build());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == logoutButton.getId()) {
            analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Login")
                    .setAction("User LOGOUT")
                    .build());

            LoginManager.getInstance().logOut();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + name);
        analyticsTracker.setScreenName("Image~" + name);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
