package app.databoxqmulandroidapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 26/07/2015.
 */
public class DbSentConfirmation extends SQLiteOpenHelper {

    public static final String TABLE_SENT_CONFIRMATION = "sent_confirmation";

    public static final String  COLUMN_DATE = "date";

    public static final String[] columns = { COLUMN_DATE };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_SENT_CONFIRMATION + " ("
                    + COLUMN_DATE + " INTEGER"
                    + ")";

    final private static String DB_NAME = "sent_confirmation_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbSentConfirmation(Context context) {
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
