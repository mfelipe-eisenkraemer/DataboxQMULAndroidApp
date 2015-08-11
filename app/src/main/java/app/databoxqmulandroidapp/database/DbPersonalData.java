package app.databoxqmulandroidapp.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class DbPersonalData extends SQLiteOpenHelper {

    public static final String TABLE_PERSONAL_DATA = "personal_data";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_LOCALE = "locale";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_AGE_MIN = "age_min";
    public static final String COLUMN_AGE_MAX = "age_max";

    public static final String[] columns = { COLUMN_ID, COLUMN_NAME, COLUMN_GENDER, COLUMN_LOCALE, COLUMN_LINK, COLUMN_AGE_MIN, COLUMN_AGE_MAX };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_PERSONAL_DATA + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_GENDER + " TEXT NOT NULL, "
                    + COLUMN_LOCALE + " TEXT NOT NULL, "
                    + COLUMN_LINK + " TEXT NOT NULL, "
                    + COLUMN_AGE_MIN + " INTEGER NOT NULL, "
                    + COLUMN_AGE_MAX + " INTEGER NOT NULL "
                    + ")";

    final private static String DB_NAME = "personal_data_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbPersonalData(Context context) {
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
