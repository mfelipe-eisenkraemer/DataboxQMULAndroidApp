package app.databoxqmulandroidapp.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MateusFelipe on 26/07/2015.
 */
public class SentConfirmation {

    private Long mDateMiliseconds;

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

}
