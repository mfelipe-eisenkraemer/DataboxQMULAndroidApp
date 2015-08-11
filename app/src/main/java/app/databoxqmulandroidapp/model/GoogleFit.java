package app.databoxqmulandroidapp.model;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by MateusFelipe on 22/07/2015.
 */
public class GoogleFit {

    private static final String TAG = "GoogleFit Model";
    private int numSteps;
    private Long mDateMiliseconds;
    private int mSentToDatabase;

    public int getNumSteps() {
        return numSteps;
    }
    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }
    public Date getDate() {
        Date date = new Date(mDateMiliseconds);
        return date;
    }
    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDateMiliseconds);

        Date date = calendar.getTime();

        return dateFormat.format(date);
    }


    public Long getmDateMiliseconds() {
        return mDateMiliseconds;
    }
    public void setmDateMiliseconds(Long mDateMiliseconds) {
        this.mDateMiliseconds = mDateMiliseconds;
    }

    public int getmSentToDatabase() {
        return mSentToDatabase;
    }
    public void setmSentToDatabase(int mSentToDatabase) {
        this.mSentToDatabase = mSentToDatabase;
    }
}
