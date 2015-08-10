package app.databoxqmulandroidapp.model;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MateusFelipe on 16/06/2015.
 */
public class TweetSentiment {

    private Long mId;
    private Long mIdTweet;
    private String mTextTweet;
    private Date mDate;
    private String mSentiment;
    private float mConfidence;
    private int isAvaiableForAnalysis;

    public TweetSentiment() {
        setIsAvaiableForAnalysis(1);
    }

    @Override
    public String toString(){
        return "Tweet id: " + getmIdTweet() + " Date: " + getmDate().toString() + " Emotion: " + getmSentiment();

    }
    public Long getmId() {
        return mId;
    }
    public void setmId(Long mId) {
        this.mId = mId;
    }
    public Date getmDate() {
        return mDate;
    }
    public void setmDate(String mDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        this.mDate = date;
    }
    public String getmSentiment() {
        return mSentiment;
    }
    public void setmSentiment(String mSentiment) {
        this.mSentiment = mSentiment;
    }
    public Long getmIdTweet() {
        return mIdTweet;
    }
    public void setmIdTweet(Long mIdTweet) {
        this.mIdTweet = mIdTweet;
    }
    public float getmConfidence() {
        return mConfidence;
    }
    public void setmConfidence(float mConfidence) {
        this.mConfidence = mConfidence;
    }
    public int getIsAvaiableForAnalysis() {
        return isAvaiableForAnalysis;
    }
    public void setIsAvaiableForAnalysis(int isAvaiableForAnalysis) {
        if(isAvaiableForAnalysis == 0 || isAvaiableForAnalysis == 1 ){
            this.isAvaiableForAnalysis = isAvaiableForAnalysis;
        }

    }
    public String getmTextTweet() {
        return mTextTweet;
    }
    public void setmTextTweet(String mTextTweet) {
        this.mTextTweet = mTextTweet;
    }
}
