package app.databoxqmulandroidapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class DbTrend extends SQLiteOpenHelper {

    public static final String TABLE_TREND = "trend";

    public static final String  COLUMN_TERM = "term";
    public static final String  COLUMN_COUNT = "count";
    public static final String  COLUMN_DATE = "date";
    public static final String  COLUMN_ID_TWEET = "id_tweet";

    public static final String[] columns = { COLUMN_TERM, COLUMN_COUNT, COLUMN_DATE, COLUMN_ID_TWEET };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_TREND + " ("
                    + COLUMN_TERM + "  TEXT NOT NULL, "
                    + COLUMN_COUNT + " INTEGER, "
                    + COLUMN_DATE + " INTEGER,"
                    + COLUMN_ID_TWEET + " TEXT,"
                    + " UNIQUE (" + COLUMN_TERM + "," + COLUMN_DATE + ") ON CONFLICT REPLACE"
                    + ")";

    final private static String DB_NAME = "trend_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbTrend(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    void deleteDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}
