package app.databoxqmulandroidapp.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 *
 * @author Thiago Locatelli <thiago.locatelli@gmail.com>
 * @author Lorensius W. L T <lorenz@londatiga.net>
 *
 */
public class InstagramApp {

    private InstagramSession mSession;
    private InstagramDialog mDialog;
    private OAuthAuthenticationListener mListener;
    private ProgressDialog mProgress;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private Context mCtx;

    private String mClientId;
    private String mClientSecret;


    private static int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "";
    public static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    public static final String API_URL = "https://api.instagram.com/v1";

    private static final String TAG = "InstagramAPI";

    public InstagramApp(Context context, String clientId, String clientSecret,
                        String callbackUrl) {

        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mSession = new InstagramSession(context);
        mAccessToken = getmSession().getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
                + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";

        InstagramDialog.OAuthDialogListener listener = new InstagramDialog.OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                mListener.onFail("Authorization failed");
            }
        };

        mDialog = new InstagramDialog(context, mAuthUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    private void getAccessToken(final String code) {
        mProgress.setMessage("Getting access token ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                int what = WHAT_FETCH_INFO;
                try {
                    URL url = new URL(TOKEN_URL);
                    //URL url = new URL(mTokenUrl + "&code=" + code);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    //urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write("client_id=" + mClientId +
                            "&client_secret=" + mClientSecret +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + mCallbackUrl +
                            "&code=" + code);
                    writer.flush();
                    String response = streamToString(urlConnection.getInputStream());
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

                    mAccessToken = jsonObj.getString("access_token");

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString("username");
                    String name = jsonObj.getJSONObject("user").getString("full_name");

                    getmSession().storeAccessToken(mAccessToken, id, user, name);

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }

    private void fetchUserName() {
        mProgress.setMessage("Finalizing ...");

        new Thread() {
            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    URL url = new URL(API_URL + "/users/" + getmSession().getId() + "/?access_token=" + mAccessToken);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    String name = jsonObj.getJSONObject("data").getString("full_name");
                    String bio = jsonObj.getJSONObject("data").getString("bio");
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();

    }




    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss();
                if(msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                }
                else if(msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            }
            else if(msg.what == WHAT_FETCH_INFO) {
                fetchUserName();
            }
            else {
                mProgress.dismiss();
                mListener.onSuccess();
            }
        }
    };

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }

    public String getUserName() {
        return getmSession().getUsername();
    }

    public String getId() {
        return getmSession().getId();
    }

    public String getName() {
        return getmSession().getName();
    }

    public void authorize() {
        //Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        //webAuthIntent.setData(Uri.parse(AUTH_URL));
        //mCtx.startActivity(webAuthIntent);
        mDialog.show();
    }

    public String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            getmSession().resetAccessToken();
            mAccessToken = null;
        }
    }

    public InstagramSession getmSession() {
        return mSession;
    }

    public interface OAuthAuthenticationListener {
        public abstract void onSuccess();

        public abstract void onFail(String error);
    }


}