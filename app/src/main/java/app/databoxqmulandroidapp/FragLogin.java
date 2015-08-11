package app.databoxqmulandroidapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import app.databoxqmulandroidapp.instagram.InstagramApp;

/**
 * Created by MateusFelipe on 13/05/2015.
 */
public class FragLogin extends Fragment {

    private CallbackManager mCallbackManager;
    private TextView mTextDetails;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private Profile mProfile;
    private AccessToken mAccessToken;

    private LoginButton mButtonLoginFacebook;
    private TwitterLoginButton mButtonLoginTwitter;
    private TwitterSession mSessionTwitter;

    private Button mButtonLoginInstagram;
    private InstagramApp instagramApp;

    public FragLogin() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        // Tracks accessToken changes
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {

            }
        };

        // Tracks profile changes
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayWelcomeMessage();
            }
        };

        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();

        // Instagram Implementation
        instagramApp = new InstagramApp(getActivity(), MyApplication.INSTAGRAM_CLIENT_ID,
                MyApplication.INSTAGRAM_CLIENT_SECRET, MyApplication.INSTAGRAM_CALLBACK_URL);

        InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {

                if( mAccessToken != null  && mSessionTwitter != null){
                    initiateMainActivity();
                }else {
                    displayWelcomeMessage();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT)
                        .show();
            }
        };

        instagramApp.setListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextDetails = ( TextView ) view.findViewById( R.id.text_welcome_login );

        mButtonLoginFacebook = ( LoginButton ) view.findViewById( R.id.login_button_facebook);

        mButtonLoginFacebook.setReadPermissions("public_profile");
        mButtonLoginFacebook.setReadPermissions("user_friends");
        mButtonLoginFacebook.setReadPermissions("email");

        // requires review...
        //mButtonLoginFacebook.setReadPermissions("user_likes");

        mButtonLoginFacebook.setFragment(this);
        mButtonLoginFacebook.registerCallback(mCallbackManager, mFacebookCallback);

        mButtonLoginInstagram = ( Button ) view.findViewById( R.id.login_button_instagram );
        mButtonLoginInstagram.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if( isNetworkAvailable() ){
                    instagramApp.authorize();
                }else{
                    Toast.makeText(getActivity(), "Sorry, something went wrong with your Instagram authentication, please try again in a few minutes.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonLoginTwitter = (TwitterLoginButton)
                view.findViewById(R.id.login_button_twitter);

        mButtonLoginTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a
                // TwitterSession for making API calls
                mSessionTwitter = Twitter.getSessionManager().getActiveSession();

                // check if user is already signed with facebook
                if (mAccessToken != null && mProfile != null && instagramApp.getId() != null) {
                    initiateMainActivity();
                }else{
                    displayWelcomeMessage();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(), "Sorry, something went wrong with your Twitter authentication, please try again in a few minutes.", Toast.LENGTH_SHORT).show();
                //Log.i("ERR",exception.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        mButtonLoginTwitter.onActivityResult(requestCode, resultCode,
                data);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAccessToken = AccessToken.getCurrentAccessToken();
        mProfile = Profile.getCurrentProfile();
        mSessionTwitter = Twitter.getSessionManager().getActiveSession();

        if( mAccessToken != null && mProfile != null && mSessionTwitter != null && instagramApp.getId() != null){
            initiateMainActivity();
        }else {
            displayWelcomeMessage();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            mAccessToken = loginResult.getAccessToken();
            mProfile = Profile.getCurrentProfile();
            displayWelcomeMessage();
            mButtonLoginFacebook.setVisibility(View.INVISIBLE);

            // check if user is already signed with twitter
            if( mSessionTwitter != null && instagramApp.getId() != null ){
                initiateMainActivity();
            }

        }

        @Override
        public void onCancel() {}

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getActivity(), "Sorry, something went wrong with your Facebook authentication, please try again in a few minutes.", Toast.LENGTH_SHORT).show();

        }
    };

    public void displayWelcomeMessage(){

        String name = "";
        if( mProfile != null ){
            name = mProfile.getName();
            mButtonLoginFacebook.setVisibility(View.INVISIBLE);
        }else if( mSessionTwitter != null ){
            name = mSessionTwitter.getUserName();
            mButtonLoginTwitter.setVisibility(View.INVISIBLE);
        }else if( instagramApp.getId() != null ){
            name = instagramApp.getName();
            mButtonLoginInstagram.setVisibility(View.INVISIBLE);
        }

        if( mProfile != null || mSessionTwitter != null || instagramApp.getId() != null){
            mTextDetails.setText("Hello " + name + ", complete the sign in to proceed.");
        }
    }

    public void initiateMainActivity(){
        Intent intent = new Intent( getActivity().getApplicationContext(), ActMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        getActivity().finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
