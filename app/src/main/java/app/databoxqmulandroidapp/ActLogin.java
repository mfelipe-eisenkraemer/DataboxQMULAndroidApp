package app.databoxqmulandroidapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * A login screen that offers login via email/password.
 */
public class ActLogin extends FragmentActivity {


    private FragLogin mFragLogin;
    private Context mContext;
    private AccessToken mAccessToken;
    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAccessToken = AccessToken.getCurrentAccessToken();
        mProfile = Profile.getCurrentProfile();
        mContext = getApplicationContext();

        TwitterSession sessionTwitter =
                Twitter.getSessionManager().getActiveSession();


        if( mAccessToken != null && mProfile != null && sessionTwitter != null ){

            Intent intent = new Intent(mContext, ActMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }else {

            setContentView(R.layout.act_login);

            if (savedInstanceState == null) {
                // Add the fragment on initial activity setup
                mFragLogin = new FragLogin();
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, mFragLogin).commit();
            } else {
                // Or set the fragment from restored state info
                mFragLogin = (FragLogin) getSupportFragmentManager()
                        .findFragmentById(android.R.id.content);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.
        if (mFragLogin != null) {
            mFragLogin.onActivityResult(requestCode, resultCode, data);
        }
    }
}