package app.databoxqmulandroidapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class MyApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "PUT YOUR TWITTER KEY HERE";
    public static final String TWITTER_SECRET = "PUT YOUR TWITTER SECRET HERE";
    private TwitterAuthConfig authConfig;
    private Context mContext;

    public static final String INSTAGRAM_CLIENT_ID = "PUT YOUT INSTAGRAM CLIENT ID HERE";
    public static final String INSTAGRAM_CLIENT_SECRET = "PUT YOUR INSTAGRAM CLIENT SECRET HERE";
    public static final String INSTAGRAM_CALLBACK_URL = "PUT YOUR INSTAGRAM CALLBACK URL HERE";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(mContext);

        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Date getTodayDate(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date date = c.getTime(); //the midnight, that's the first second of the day.
        return date;
    }

    public static Date getYesterdayDate(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, -1);
        Date date = c.getTime(); //the midnight, that's the first second of the day.
        return date;
    }

    public static List<Long> getDaysBetweenDates(Date startdate, Date enddate) {

        List<Long> dates = new ArrayList<Long>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate))
        {
            Date result = calendar.getTime();
            dates.add( result.getTime() );
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

}
