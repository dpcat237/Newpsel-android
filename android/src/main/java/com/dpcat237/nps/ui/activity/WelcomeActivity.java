package com.dpcat237.nps.ui.activity;

import android.app.Activity;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Arrays;


public class WelcomeActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "NPS:WelcomeActivity";
	private Context mContext;
    private View mView;

    private static final int RC_SIGN_IN = 0;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean gSignInClicked = false;
    private ConnectionResult mConnectionResult;

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
		mView = this.findViewById(android.R.id.content).getRootView();
		setContentView(R.layout.activity_welcome);

        setFacebookButton((LoginButton) mView.findViewById(R.id.buttonFacebookSignIn), savedInstanceState);
        setGoogleButton((SignInButton) findViewById(R.id.buttonGoogleSignIn));
	}

    private void signUp(String email) {
        Log.d(TAG, "tut: signUp");
        SignUpTask task = new SignUpTask(this, mView, email, "");
        task.execute();
    }

    public void goSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void goSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
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
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (gSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
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

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg) {
        Log.d(TAG, "tut: G User is connected! gSignInClicked: "+gSignInClicked);
        if (!gSignInClicked) {
            signOutFromGplus();

            return;
        }
        gSignInClicked = false;

        // Get user's information
        getProfileInformation();
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                //String personName = currentPerson.getDisplayName();
                //String personPhotoUrl = currentPerson.getImage().getUrl();
                //String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                signUp(email);
            } else {
                Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                signInWithGplus();
                break;
        }
    }

    private void setGoogleButton(SignInButton btnSignIn) {
        btnSignIn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }


    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            gSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        Log.d(TAG, "tut: signOutFromGplus");
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    /**
     * Revoking access from google
     * */
    /*private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status arg0) {
                    Log.e(TAG, "User access revoked!");
                    mGoogleApiClient.connect();
                }

            });
        }
    }*/


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
                Log.i(TAG, "Error " + error.getMessage());
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
                    Log.d(TAG, "tut: You are not logged ");

                    return;
                }

                //Log.d(TAG, "tut: User ID " + user.getId());
                //Log.d(TAG, "tut: User getName " + user.getName());
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
        Log.d(TAG, "tut: signOutFromFacebook");
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
        Log.d(TAG, "tut: checkFacebookLogged");
        if (!fSignInClicked) {
            signOutFromFacebook();
        }
    }
}