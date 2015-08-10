package app.databoxqmulandroidapp.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class Trend {

    private String mTerm;
    private int mCount;
    private Long mIdTweet;
    private Long mDateMiliseconds;

    public String getmTerm() {
        return mTerm;
    }
    public void setmTerm(String mTerm) {
        this.mTerm = mTerm;
    }
    public int getmCount() {
        return mCount;
    }
    public void setmCount(int mCount) {
        this.mCount = mCount;
    }
    public Long getmIdTweet() {
        return mIdTweet;
    }
    public void setmIdTweet(Long mIdTweet) {
        this.mIdTweet = mIdTweet;
    }
    public Date getDate() {
        Date date = new Date(mDateMiliseconds);
        return date;
    }
    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());

        Date date = new Date(mDateMiliseconds);

        return dateFormat.format(date);
    }


    public Long getmDateMiliseconds() {
        return mDateMiliseconds;
    }
    public void setmDateMiliseconds(Long mDateMiliseconds) {
        this.mDateMiliseconds = mDateMiliseconds;
    }
}
