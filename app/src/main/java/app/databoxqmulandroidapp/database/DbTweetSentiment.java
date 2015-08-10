package app.databoxqmulandroidapp.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MateusFelipe on 16/06/2015.
 */
public class DbTweetSentiment extends SQLiteOpenHelper {

    public static final String TABLE_TWEET_SENTIMENT = "tweet_sentiment";

    public static final String  COLUMN_ID = "_id";
    public static final String  COLUMN_ID_TWEET = "id_tweet";
    public static final String  COLUMN_TEXT_TWEET = "text_tweet";
    public static final String  COLUMN_DATE = "date";
    public static final String COLUMN_SENTIMENT = "sentiment";
    public static final String COLUMN_CONFIDENCE = "confidence";
    public static final String COLUMN_IS_AVAIABLE_FOR_ANALYSIS = "is_avaiable_for_analysis";

    public static final String[] columns = { COLUMN_ID, COLUMN_ID_TWEET, COLUMN_TEXT_TWEET, COLUMN_DATE, COLUMN_SENTIMENT, COLUMN_CONFIDENCE, COLUMN_IS_AVAIABLE_FOR_ANALYSIS };

    final private static String CREATE_CMD =

            "CREATE TABLE " + TABLE_TWEET_SENTIMENT + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ID_TWEET + " TEXT NOT NULL, "
                    + COLUMN_TEXT_TWEET + " TEXT NOT NULL, "
                    + COLUMN_DATE + " DATETIME DEFAULT (datetime('now','localtime')) NOT NULL, "
                    + COLUMN_SENTIMENT + " TEXT NOT NULL, "
                    + COLUMN_CONFIDENCE + " REAL NOT NULL, "
                    + COLUMN_IS_AVAIABLE_FOR_ANALYSIS + " INTEGER NOT NULL "
                    + ")";

    final private static String DB_NAME = "tweet_sentiment_db";
    final private static Integer VERSION = 2;
    final private Context mContext;

    public DbTweetSentiment(Context context) {
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
