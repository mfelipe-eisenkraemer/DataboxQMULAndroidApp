package app.databoxqmulandroidapp.model;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class PersonalData {

    private long mId;
    private String mName;
    private String mGender;
    private String mLocale;
    private String mLink;
    private String mEmail;
    private int mAgeMin;
    private int mAgeMax;

    public PersonalData(){}

    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public String getmGender() {
        return mGender;
    }
    public void setmGender(String mGender) {
        this.mGender = mGender;
    }
    public String getmLocale() {
        return mLocale;
    }
    public void setmLocale(String mLocale) {
        this.mLocale = mLocale;
    }
    public String getmLink() {
        return mLink;
    }
    public void setmLink(String mLink) {
        this.mLink = mLink;
    }
    public String getmEmail() {
        return mEmail;
    }
    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public int getmAgeMin() {
        return mAgeMin;
    }
    public void setmAgeMin(int mAgeMin) {
        this.mAgeMin = mAgeMin;
    }
    public int getmAgeMax() {
        return mAgeMax;
    }
    public void setmAgeMax(int mAgeMax) {
        this.mAgeMax = mAgeMax;
    }
    public long getmId() {
        return mId;
    }
    public void setmId(long mId) {
        this.mId = mId;
    }
}
