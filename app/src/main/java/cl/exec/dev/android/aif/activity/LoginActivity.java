package cl.exec.dev.android.aif.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import cl.exec.dev.android.aif.R;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "LTJD6SgdeI3gjJHlDFNn4hoMf";
    private static final String TWITTER_SECRET = "Iz4V7NNr4D2guhlXhwijT3XvMYxGZ81snhNZmrG7D8JgsBgcED";

    private String userName;
    private String userEmail;

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */

    private Button facebookLoginButton;
    private Button twitterLoginButton;
    private Button googleLoginButton;

    private CallbackManager callbackManager;
    public TwitterAuthClient twitterAuthClient;



    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * Twitter initialize
         */
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        twitterAuthClient= new TwitterAuthClient();


        /**
         * Facebook initialize
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });


        setContentView(R.layout.activity_login);
        mProgressDialog = initializeProgressDialog();

        facebookLoginButton = (Button) findViewById(R.id.button_facebook);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Facebook Login click");
                facebookLogin();
                finish();
            }
        });


        twitterLoginButton = (Button) findViewById(R.id.button_twitter);
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Twitter Login click");

                twitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.e(LOG_TAG, "ERROR: " + e.toString());
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });


        //google login
        googleLoginButton = (Button) findViewById(R.id.button_google);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "Google Login click");
                mGoogleApiClient.connect();
            }
        });
    }


    /**
     * Facebook login, first logout, then login
     */
    private void facebookLogin(){
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));



    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG_TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(LOG_TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            String userNameIntent = getUserName();
            String userEmailIntent = getUserEmail();

            if (userEmail!=null && userName!=null){
                intent.putExtra(userName, userNameIntent);
                intent.putExtra(userEmail, userEmailIntent);
                Log.d(LOG_TAG, userNameIntent);
                Log.d(LOG_TAG, userEmailIntent);
            }
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        retryConnecting();
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
            showProgressDialog();
        }
    }

    private ProgressDialog initializeProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.progress_dialog));
        return dialog;
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // get userEmail
    public String getUserEmail(){

        try {
            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)!=null) {
                userEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "getUserEmail Exception");
            e.printStackTrace();
        }
        return userEmail;
    }

    // get userName
    private String getUserName(){
        try {
            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)!=null){
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                userName = currentPerson.getDisplayName();
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "getUserName Exception");
            e.printStackTrace();
        }
        return userName;
    }
}
