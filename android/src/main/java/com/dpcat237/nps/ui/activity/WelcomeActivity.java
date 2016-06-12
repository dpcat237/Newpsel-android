package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignUpTask;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class WelcomeActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "NPS:WelcomeActivity";
	private Context mContext;

    private static final int RC_SIGN_IN = 0;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean gSignInClicked = false;
    private ConnectionResult mConnectionResult;
    /*private LoginButton loginButton;
    protected CallbackManager callbackManager;*/

    // Instance of Facebook Class
    private boolean fSignInClicked = false;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
        View view = this.findViewById(android.R.id.content).getRootView();
		setContentView(R.layout.activity_welcome);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        setFacebookButton((LoginButton) view.findViewById(R.id.buttonFacebookSignIn), savedInstanceState);
        setGoogleButton((SignInButton) view.findViewById(R.id.buttonGoogleSignIn));
	}

    private void getFacebookData(JSONObject object) {
        Log.d(TAG, "tut: getFacebookData :");
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

            if (object.has("email")) {
                Log.d(TAG, "tut: email: "+object.has("email"));

                //bundle.putString("email", object.getString("email"));
            } else {
                Log.d(TAG, "tut: no email");
            }


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void signUp(String email) {
        //Log.d(TAG, "tut: signUp");
        SignUpTask task = new SignUpTask(this, email, "");
        task.execute();
    }

    public void goSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void goSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (gSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        uiHelper.onActivityResult(requestCode, responseCode, intent);
        Log.d(TAG, "tut: onActivityResult");

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                gSignInClicked = false;
            }

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            try {
                signUp(account.getEmail());
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), R.string.error_google_email_null, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_google_sign_in, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(Bundle arg) { }

    @Override
    public void onConnectionSuspended(int arg) {
        mGoogleApiClient.connect();
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGoogleSignIn:
                signInWithGoogle();
                break;
        }
    }

    private void setGoogleButton(SignInButton btnSignIn) {
        btnSignIn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    /**
     * Sign-in into google
     * */
    private void signInWithGoogle() {
        Log.d(TAG, "tut: signInWithGoogle");

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGoogle() {
        //Log.d(TAG, "tut: signOutFromGoogle");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }


    /** Facebook login **/
    private void setFacebookButton(LoginButton facebookButton, Bundle savedInstanceState) {
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fSignInClicked = true;
            }
        });
        facebookButton.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error " + error.getMessage());
            }
        });

        facebookButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (!fSignInClicked && user != null) {
                    checkFacebookLogged();

                    return;
                }

                if (user == null) {
                    //Log.d(TAG, "tut: You are not logged ");

                    return;
                }

                //Log.d(TAG, "tut: User ID " + user.getId());
                //Log.d(TAG, "tut: User getName " + user.getName());
                Log.d(TAG, "tut: User email " + user.asMap().get("email").toString());
                signUp(user.asMap().get("email").toString());
            }
        });

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        checkFacebookLogged();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.d(TAG, "tut: Logged in...");
        } else if (state.isClosed()) {
            Log.d(TAG, "tut: Logged out...");
        }

        if (state.isOpened() && !fSignInClicked) {
            signOutFromFacebook();
        }
    }

    private void signOutFromFacebook() {
        //Log.d(TAG, "tut: signOutFromFacebook");
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        } else {
            Session session2 = Session.openActiveSession((Activity)mContext, false, null);
            if(session2 != null)
                session2.closeAndClearTokenInformation();
        }
        Session.setActiveSession(null);
    }

    private void checkFacebookLogged() {
        //Log.d(TAG, "tut: checkFacebookLogged");
        if (!fSignInClicked) {
            signOutFromFacebook();
        }
    }
}