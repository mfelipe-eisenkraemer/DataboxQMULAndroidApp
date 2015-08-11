package app.databoxqmulandroidapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 22/07/2015.
 */
public class DbGoogleFit extends SQLiteOpenHelper {

    public static final String TABLE_GOOGLE_FIT = "google_fit";

    public static final String  COLUMN_NUM_STEPS = "num_steps";
    public static final String  COLUMN_DATE = "date";
    public static final String  COLUMN_SENT_TO_DATABASE = "sent_to_database";

    public static final String[] columns = { COLUMN_NUM_STEPS, COLUMN_DATE, COLUMN_SENT_TO_DATABASE };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_GOOGLE_FIT + " ("
                    + COLUMN_NUM_STEPS + " INTEGER NOT NULL, "
                    + COLUMN_DATE + " INTEGER NOT NULL, "
                    + COLUMN_SENT_TO_DATABASE + " INTEGER NOT NULL "
                    + ")";

    final private static String DB_NAME = "google_fit_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbGoogleFit(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}
