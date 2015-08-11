package app.databoxqmulandroidapp.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 12/05/2015.
 */
public class DbLocation extends SQLiteOpenHelper {

    public static final String TABLE_LOCATION = "location";

    public static final String  COLUMN_ID = "_id";
    public static final String  COLUMN_LATITUDE = "latitude";
    public static final String  COLUMN_LONGITUDE = "longitude";
    public static final String  COLUMN_ACCURARY = "accuracy";
    public static final String  COLUMN_DATE = "date";

    public static final String[] columns = { COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_ACCURARY, COLUMN_DATE };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_LOCATION + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_LATITUDE + " TEXT NOT NULL, "
                    + COLUMN_LONGITUDE + " TEXT NOT NULL, "
                    + COLUMN_ACCURARY + " TEXT NOT NULL, "
                    + COLUMN_DATE + " DATETIME DEFAULT (datetime('now','localtime')) NOT NULL "
            + ")";

    final private static String DB_NAME = "location_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbLocation(Context context) {
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
