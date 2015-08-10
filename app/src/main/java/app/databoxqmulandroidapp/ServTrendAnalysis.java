package app.databoxqmulandroidapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import app.databoxqmulandroidapp.model.PersonalData;
import app.databoxqmulandroidapp.model.TweetSentiment;
import app.databoxqmulandroidapp.rest.RestClient;
import app.databoxqmulandroidapp.database.DbTrend;
import app.databoxqmulandroidapp.instagram.InstagramApp;
import app.databoxqmulandroidapp.model.GoogleFit;
import app.databoxqmulandroidapp.model.GoogleFitDataSource;
import app.databoxqmulandroidapp.model.PersonalDataDataSource;
import app.databoxqmulandroidapp.model.Trend;
import app.databoxqmulandroidapp.model.TrendDataSource;
import app.databoxqmulandroidapp.model.TweetSentimentDataSource;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class ServTrendAnalysis extends Service {


    private static final String TAG = "Serv Trend";
    private TwitterApiClient mTwitterApiClient;
    private StatusesService mStatusesService;
    private TwitterSession mSessionTwitter;
    private AccessToken mAccessToken;
    private Context mContext;
    private TrendDataSource mTrendDataSource;
    private TweetSentimentDataSource mTweetSentimentDataSource;
    private PersonalDataDataSource mPersonalDataDataSource;


    private InstagramApp instagramApp;

    List<String> stopWords;
    Set<String> stopWordsSet = new LinkedHashSet<String>();

    private static final int REQUEST_OAUTH = 1;


    public static final String URL_MASHABLE = "https://community-sentiment.p.mashape.com";
    private GoogleApiClient mGoogleApiClient;
    private GoogleFitDataSource mGoogleFitDataSource;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If there is internet
        if( isNetworkAvailable() ){

            mSessionTwitter = Twitter.getSessionManager().getActiveSession();
            mAccessToken = AccessToken.getCurrentAccessToken();
            mTwitterApiClient = TwitterCore.getInstance().getApiClient();
            mStatusesService = mTwitterApiClient.getStatusesService();
            mContext = getApplicationContext();

            // Instagram Implementation
            instagramApp = new InstagramApp(getApplicationContext(), MyApplication.INSTAGRAM_CLIENT_ID,
                    MyApplication.INSTAGRAM_CLIENT_SECRET, MyApplication.INSTAGRAM_CALLBACK_URL);

            stopWords =  Arrays.asList(getResources().getStringArray(R.array.stop_words));
            for(String word : stopWords) {
                stopWordsSet.add(word);
            }

            mTweetSentimentDataSource.open();
            int count = mTweetSentimentDataSource.getSentimentCount();
            mTweetSentimentDataSource.close();

            mPersonalDataDataSource.open();
            PersonalData firstData = mPersonalDataDataSource.getFirstPersonalData();
            mPersonalDataDataSource.close();

            if(firstData == null){
                //Assync task to get Facebook data
                getFacebookUserData();
            }

            // If table is empty
            if( count == 0 ){

                // Assync task to download and analyze the first tweet
                new TaskRetrieveFirstTweetSentiment().execute();

            }else{
                // Assync task to download and analyze the latest tweet
                new TaskRetrieveLatestTweetSentiment().execute();
            }

            getHomeTimeLine();
            new TaskGetInstagramTrends().execute();

            mGoogleFitDataSource.open();
            int countGoogleFitRecords = mGoogleFitDataSource.getGoogleFitCount();
            mGoogleFitDataSource.close();

            // Check if there is records
            if( countGoogleFitRecords > 0 ){

                String orderQuery = " ASC ";
                mGoogleFitDataSource.open();
                ArrayList<GoogleFit> googleFitList = mGoogleFitDataSource.getAllGoogleFit( orderQuery );
                mGoogleFitDataSource.close();

                // Get all the dates between the start and today
                GoogleFit firstGoogleFit = googleFitList.get(0);
                Date startDate = new Date(firstGoogleFit.getmDateMiliseconds());
                Date endDate = MyApplication.getTodayDate();

                // Removes the dates already in the database
                List<Long> rangeDateList = MyApplication.getDaysBetweenDates(startDate,endDate);
                for ( GoogleFit googleFit : googleFitList ){
                    if( rangeDateList.contains( googleFit.getmDateMiliseconds() ) ){
                        rangeDateList.remove( googleFit.getmDateMiliseconds() );
                    }
                }

                // Insert all the date which arent in the database yet.
                if( rangeDateList.size() > 0 ) {
                    buildFitnessClient();
                    mGoogleApiClient.connect();
                    for ( Long dateToRetrieve : rangeDateList ) {

                        Date date = new Date(dateToRetrieve);

                        GoogleFit googleFit = new GoogleFit();
                        saveStepsByDate(mGoogleApiClient, date, mGoogleFitDataSource);
                    }
                }

            }
        }
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Manages the google fit integration for the first time
        mGoogleFitDataSource = new GoogleFitDataSource(getApplicationContext());
        mTrendDataSource = new TrendDataSource(getApplicationContext());
        mTweetSentimentDataSource = new TweetSentimentDataSource(getApplicationContext());
        mPersonalDataDataSource = new PersonalDataDataSource(getApplicationContext());

    }

    private void getHomeTimeLine() {

        Long since_id = null;


        mTrendDataSource.open();
        Trend trendSinceId = mTrendDataSource.getTwitterSinceId();
        mTrendDataSource.close();

        if( trendSinceId != null ){
            since_id = trendSinceId.getmIdTweet();
        }

        //Log.i("Since ID", since_id + "");
        // Get most recent tweet from user
        mStatusesService.homeTimeline(200, since_id, null, false, false, false, false, new Callback<List<Tweet>>() {

            Long highestId = null;

            @Override
            public void success(final Result<List<Tweet>> result) {
                for (final Tweet tweet : result.data) {

                    if (highestId == null || highestId < tweet.id) {
                        highestId = tweet.id;
                    }

                    List<Trend> trendsToAdd = removeStopWords(tweet.text);

                    mTrendDataSource.open();
                    mTrendDataSource.insertMultipleTrends(trendsToAdd);
                    mTrendDataSource.close();

                    //Log.i(TAG, tweet.text);
                }

                if( highestId != null) {

                    Trend sinceIdTrend = new Trend();
                    sinceIdTrend.setmIdTweet(highestId);
                    new TaskAddSinceIdTrend().execute(sinceIdTrend);

                }
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }

    private void addTrends(List<Trend> trendsToAdd) {

    }

    private List<Trend> removeStopWords(String text) {
        text = text.trim().replaceAll("\\text+", " ");
        String[] words = text.split(" ");

        List<Trend> trendsList = new ArrayList<Trend>();
        for (String word : words) {
            if( !stopWordsSet.contains(word.toUpperCase()) && !word.equals("&amp;") ){

                if (word.trim().length() > 0){
                    Trend trend = new Trend();
                    trend.setmTerm(word);

                    Calendar c = new GregorianCalendar();
                    c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    Date date = c.getTime(); //the midnight, that's the first second of the day.

                    trend.setmDateMiliseconds(date.getTime());
                    trendsList.add(trend);
                }

            }
        }

        return trendsList;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in two hours
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 60 * 2  ), // two hour
                PendingIntent.getService(this, 0, new Intent(this, ServTrendAnalysis.class), 0)
        );
    }

    private class TaskAddSinceIdTrend extends AsyncTask<Trend, Void, Void> {

        @Override
        protected Void doInBackground(Trend... trendsToAdd) {


            Trend add = trendsToAdd[0];
            mTrendDataSource.open();
            mTrendDataSource.insertTwitterSinceId(add);
            mTrendDataSource.close();

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {}
        protected void onPostExecute(Long result) {}
    }

    private class TaskRetrieveFirstTweetSentiment extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... userId) {

            // Get most recent tweet from user
            mStatusesService.userTimeline(mSessionTwitter.getUserId(), null, 1, null, null, true, true, false, true, new Callback<List<Tweet>>() {
                @Override
                public void success(final Result<List<Tweet>> result) {
                    for (final Tweet tweet : result.data) {

                        final TweetSentiment tweetSentiment = new TweetSentiment();
                        tweetSentiment.setmIdTweet(Long.valueOf(tweet.idStr));

                        RestClient.get(URL_MASHABLE).getSentiment(tweet.text, new retrofit.Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement jsonElement, Response response) {

                                // success!
                                JsonObject object = jsonElement.getAsJsonObject();
                                String sentiment = object.get("result").getAsJsonObject().get("sentiment").toString();
                                float confidence = object.get("result").getAsJsonObject().get("confidence").getAsFloat();
                                //Log.i(TAG, sentiment);

                                tweetSentiment.setmSentiment(sentiment);
                                tweetSentiment.setmConfidence(confidence);
                                tweetSentiment.setmTextTweet(tweet.text);
                                tweetSentiment.setIsAvaiableForAnalysis(0);

                                // save in the database
                                mTweetSentimentDataSource.open();
                                mTweetSentimentDataSource.insertTweetSentiment(tweetSentiment);
                                mTweetSentimentDataSource.close();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // something went wrong
                            }
                        });

                    }
                }

                public void failure(TwitterException exception) {
                    //Do something on failure
                }
            });

            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
        }
        protected void onPostExecute(Long result) {
        }
    }


    private class TaskRetrieveLatestTweetSentiment extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... userId) {

            mTweetSentimentDataSource.open();
            TweetSentiment tweetSentiment = mTweetSentimentDataSource.getLastTweetSentiment();
            mTweetSentimentDataSource.close();

            // Compare the dates of last read
            Date date = new Date();

            // Try to get new tweets
            // if( getDateDiff(date, tweetSentiment.getmDate(), TimeUnit.HOURS) > 5 ) {

            // Get most recent tweet from user
            mStatusesService.userTimeline(mSessionTwitter.getUserId(), null, null, tweetSentiment.getmIdTweet(), null, true, true, false, true, new Callback<List<Tweet>>() {
                @Override
                public void success(final Result<List<Tweet>> result) {
                    for (final Tweet tweet : result.data) {

                        final TweetSentiment tweetSentiment = new TweetSentiment();
                        tweetSentiment.setmIdTweet(Long.valueOf(tweet.idStr));

                        RestClient.get(URL_MASHABLE).getSentiment(tweet.text, new retrofit.Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement jsonElement, Response response) {

                                // success!
                                JsonObject object = jsonElement.getAsJsonObject();
                                String sentiment = object.get("result").getAsJsonObject().get("sentiment").toString();
                                float confidence = object.get("result").getAsJsonObject().get("confidence").getAsFloat();
                                //Log.i(TAG, sentiment);

                                tweetSentiment.setmTextTweet(tweet.text);
                                tweetSentiment.setmSentiment(sentiment);
                                tweetSentiment.setmConfidence(confidence);

                                // save in the database
                                mTweetSentimentDataSource.open();
                                mTweetSentimentDataSource.insertTweetSentiment(tweetSentiment);
                                mTweetSentimentDataSource.close();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // something went wrong
                            }
                        });

                    }
                }

                public void failure(TwitterException exception) {
                    //Do something on failure
                }
            });
            //}

            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
        }
        protected void onPostExecute(Long result) {
        }
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void getFacebookUserData() {

        // get user details
        GraphRequest request = GraphRequest.newMeRequest(
                mAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject user,
                            GraphResponse response) {

                        JSONObject object = response.getJSONObject();
                        JSONObject taggableFriends = null;
                        JSONArray friendsList = null;
                        JSONObject ageRange = null;

                        String name = null;
                        String gender = null;
                        String locale = null;
                        String link = null;
                        String email = null;

                        int minAgeRange = 0;
                        int maxAgeRange = 0;

                        try {
                            name = object.getString("name");
                        } catch (JSONException e) {}

                        try {
                            gender = object.getString("gender");
                        } catch (JSONException e) {}

                        try {
                            locale = object.getString("locale");
                        } catch (JSONException e) {}

                        try {
                            link = object.getString("link");
                        } catch (JSONException e) {}

                        try {
                            email = object.getString("email");
                        } catch (JSONException e) {}

                        try {
                            ageRange = object.getJSONObject("age_range");
                            minAgeRange = ageRange.getInt("min");
                            maxAgeRange = ageRange.getInt("max");
                        } catch (JSONException e) {}

                        PersonalData personalData = new PersonalData();
                        personalData.setmName(name);
                        personalData.setmGender(gender);
                        personalData.setmLocale(locale);
                        personalData.setmLink(link);
                        personalData.setmEmail(email);
                        personalData.setmAgeMin(minAgeRange);
                        personalData.setmAgeMax(maxAgeRange);

                        mPersonalDataDataSource.open();
                        mPersonalDataDataSource.insertPersonalData(personalData);
                        mPersonalDataDataSource.close();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, gender, locale, link, email, age_range"); // , taggable_friends
        request.setParameters(parameters);
        request.executeAsync();
    }

    private class TaskGetInstagramTrends extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... trendsToAdd) {

            try {
                //URL url = new URL(API_URL + "/users/" + mSession.getId() + "/media/recent/?access_token=" + mAccessToken);
                URL url = new URL( instagramApp.API_URL + "/users/self/feed?access_token=" + instagramApp.getmSession().getAccessToken() + "&COUNT=50&min_id=" + instagramApp.getmSession().getMinId() );
                //https://api.instagram.com/v1/users/self/feed?access_token=ACCESS-TOKEN

                //Log.d(TAG, "Opening URL " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                String response = instagramApp.streamToString(urlConnection.getInputStream());
                System.out.println(response);
                urlConnection.disconnect();

                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray data = jsonObj.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {
                    JSONObject instagramObject = data.getJSONObject(i);

                    if( i == 0 ){
                        String nextMinId = instagramObject.getString("id");
                        //Log.d(TAG, "nextMinId " + nextMinId);
                        instagramApp.getmSession().storeMinId(nextMinId);
                    }
                    if( instagramObject.has("caption") && !instagramObject.isNull("caption") ){
                        JSONObject instagramCaption = instagramObject.getJSONObject("caption");
                        String instagramText = instagramCaption.getString("text");
                        List<Trend> instagramTrendsToAdd = removeStopWords(instagramText);

                        mTrendDataSource.open();
                        mTrendDataSource.insertMultipleTrends(instagramTrendsToAdd);
                        mTrendDataSource.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {}
        protected void onPostExecute(Long result) {}
    }


    private void buildFitnessClient() {
        // Create the Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                //Log.i(TAG, "Connected!!!");

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    //Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    //Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                //Log.i(TAG, "Connection failed. Cause: " + result.toString());
                            }
                        }
                )
                .build();
    }


    public void saveStepsByDate( GoogleApiClient mGoogleApiClient, final Date date, final GoogleFitDataSource googleFitDataSource ) {

        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long startTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long endTime = cal.getTimeInMillis();

        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


        Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                DataSet stepData = dataReadResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                int totalSteps = 0;

                for (DataPoint dp : stepData.getDataPoints()) {
                    for (Field field : dp.getDataType().getFields()) {
                        int steps = dp.getValue(field).asInt();

                        totalSteps += steps;

                    }
                }

                GoogleFit googleFit = new GoogleFit();
                googleFit.setNumSteps(totalSteps);
                googleFit.setmDateMiliseconds(date.getTime());
                googleFit.setmSentToDatabase(0);

                googleFitDataSource.open();
                googleFitDataSource.insertGoogleFit(googleFit);
                googleFitDataSource.close();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}