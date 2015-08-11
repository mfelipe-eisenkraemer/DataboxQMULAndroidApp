package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import app.databoxqmulandroidapp.database.DbTweetSentiment;

/**
 * Created by MateusFelipe on 16/06/2015.
 */
public class TweetSentimentDataSource {
    // Database fields
    private SQLiteDatabase database;
    private DbTweetSentiment dbHelper;

    public TweetSentimentDataSource(Context context) {
        dbHelper = new DbTweetSentiment(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public TweetSentiment getLastTweetSentiment(){

        Cursor cursor = database.query(dbHelper.TABLE_TWEET_SENTIMENT,
                dbHelper.columns, null, null,
                null, null, DbTweetSentiment.COLUMN_ID + " ASC ", "1");

        TweetSentiment tweetSentiment = null;
        if(cursor != null) {
            cursor.moveToFirst();
            tweetSentiment = cursorToTweetSentiment(cursor);
            cursor.close();
        }

        return tweetSentiment;
    }

    public int getSentimentCount(){

        String countQuery = "SELECT  1 FROM " + dbHelper.TABLE_TWEET_SENTIMENT;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public TweetSentiment insertTweetSentiment(TweetSentiment tweetSentiment) {
        ContentValues values = new ContentValues();
        values.put( dbHelper.COLUMN_ID_TWEET, tweetSentiment.getmIdTweet());
        values.put( dbHelper.COLUMN_TEXT_TWEET, tweetSentiment.getmTextTweet());
        values.put( dbHelper.COLUMN_SENTIMENT, tweetSentiment.getmSentiment());
        values.put( dbHelper.COLUMN_CONFIDENCE, tweetSentiment.getmConfidence());
        values.put( dbHelper.COLUMN_IS_AVAIABLE_FOR_ANALYSIS, tweetSentiment.getIsAvaiableForAnalysis());

        long insertId = database.insert(dbHelper.TABLE_TWEET_SENTIMENT, null, values);

        String[] args = { String.valueOf(insertId) };

        Cursor cursor = database.query(dbHelper.TABLE_TWEET_SENTIMENT,
                dbHelper.columns, DbTweetSentiment.COLUMN_ID + " = ?", args,
                null, null, null);

        TweetSentiment newTweetSentiment = null;
        if(cursor != null){
            cursor.moveToFirst();
            newTweetSentiment = cursorToTweetSentiment(cursor);
            cursor.close();
        }



        return newTweetSentiment;
    }

    public ArrayList<TweetSentiment> getAllAvaiableForAnalysisTweetSentiment() {

        ArrayList<TweetSentiment> tweetSentimentList = new ArrayList<TweetSentiment>();

        String[] args = { String.valueOf(1) };
        Cursor cursor = database.query(dbHelper.TABLE_TWEET_SENTIMENT,
                dbHelper.columns, DbTweetSentiment.COLUMN_IS_AVAIABLE_FOR_ANALYSIS + " = ?", args, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TweetSentiment tweetSentiment = cursorToTweetSentiment(cursor);
                tweetSentimentList.add(tweetSentiment);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return tweetSentimentList;
    }

    private TweetSentiment cursorToTweetSentiment(Cursor cursor) {

        TweetSentiment tweetSentiment = new TweetSentiment();
        tweetSentiment.setmId(cursor.getLong(0));
        tweetSentiment.setmIdTweet(cursor.getLong(1));
        tweetSentiment.setmTextTweet(cursor.getString(2));
        tweetSentiment.setmDate(cursor.getString(3));
        tweetSentiment.setmSentiment(cursor.getString(4));
        tweetSentiment.setmConfidence(cursor.getFloat(5));
        tweetSentiment.setIsAvaiableForAnalysis(cursor.getInt(6));

        return tweetSentiment;
    }

    public void deletePlace(TweetSentiment tweetSentiment) {}

}
