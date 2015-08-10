package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import app.databoxqmulandroidapp.database.DbTrend;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class TrendDataSource {// Database fields
    private SQLiteDatabase database;
    private DbTrend dbHelper;
    private static final String TWITTER_SINCE_ID = "twitter_since_id";

    public TrendDataSource(Context context) {
        dbHelper = new DbTrend(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Trend getTwitterSinceId(){

        String[] args = { TWITTER_SINCE_ID };
        Cursor cursor = database.query(dbHelper.TABLE_TREND,
                dbHelper.columns, DbTrend.COLUMN_TERM + " = ?", args, null, null, null);

        Trend trend = null;
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trend = cursorToTrend(cursor);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }

        return trend;
    }

    public int getTrendCount(){

        String countQuery = "SELECT  1 FROM " + dbHelper.TABLE_TREND;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void insertTrend(Trend trend) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_TERM, trend.getmTerm());

        String countQuery = "" +
                "INSERT OR REPLACE INTO " + dbHelper.TABLE_TREND + "\n" +
                "VALUES (?, \n" +
                "  COALESCE(\n" +
                "    (SELECT count FROM " + dbHelper.TABLE_TREND + "\n" +
                "       WHERE term=? and date = ? ),\n" +
                "    0) + 1), ?;";

        String[] args = new String [] {trend.getmTerm(),trend.getmTerm(), String.valueOf(trend.getmDateMiliseconds()), String.valueOf(trend.getmDateMiliseconds())};
        database.execSQL(countQuery, args );
    }

    public void insertMultipleTrends(List<Trend> trendList){

        String multiInsertQuery = "" +
                "INSERT OR REPLACE INTO " + dbHelper.TABLE_TREND + "\n" +
                "SELECT ? as " + dbHelper.COLUMN_TERM + ", \n" +
                "  COALESCE(\n" +
                "    (SELECT count FROM " + dbHelper.TABLE_TREND + " WHERE term = ? and date = ? ), " +
                "    0" +
                "  ) + 1 as " + dbHelper.COLUMN_COUNT + "," +
                "  ? as " + dbHelper.COLUMN_DATE + "," +
                "  null as " + dbHelper.COLUMN_ID_TWEET
              ;

        Trend firstElement = trendList.get(0);
        List<String> argsList = new ArrayList<String>();
        argsList.add( firstElement.getmTerm() );
        argsList.add( firstElement.getmTerm() );
        argsList.add( String.valueOf(firstElement.getmDateMiliseconds()) );
        argsList.add( String.valueOf(firstElement.getmDateMiliseconds()) );

        if( trendList.size() > 1 ){

            for( Trend trend : trendList.subList(1,trendList.size()) ){

                multiInsertQuery = multiInsertQuery + " UNION SELECT ?, " +
                        "  COALESCE(\n" +
                        "    (SELECT count FROM " + dbHelper.TABLE_TREND + " WHERE term = ? and date = ? ), " +
                        "    0" +
                        "  ) + 1, ?, null";
                argsList.add( trend.getmTerm() );
                argsList.add( trend.getmTerm() );
                argsList.add( String.valueOf(trend.getmDateMiliseconds()) );
                argsList.add( String.valueOf(trend.getmDateMiliseconds()) );

            }
        }
        multiInsertQuery = multiInsertQuery + " ;";

        String[] args = new String [argsList.size()];
        args = argsList.toArray(args);
        database.execSQL(multiInsertQuery, args);
    }

    public void insertTwitterSinceId(Trend trend) {

        String countQuery = "" +
                "INSERT OR REPLACE INTO " + dbHelper.TABLE_TREND + "\n" +
                "VALUES (?, ?, ?, ? );";

        database.execSQL(countQuery, new String [] {TWITTER_SINCE_ID, null, "123", String.valueOf(trend.getmIdTweet())} );

    }


    public ArrayList<Trend> getTrendsByDate( String limit, String order, Long date) {

        ArrayList<Trend> trendList = new ArrayList<Trend>();

        String[] args = { TWITTER_SINCE_ID, String.valueOf(date) };
        Cursor cursor = database.query(dbHelper.TABLE_TREND,
                dbHelper.columns, DbTrend.COLUMN_TERM + " != ? and " + DbTrend.COLUMN_DATE + " = ?", args, null, null, order, limit);

        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Trend trend = cursorToTrend(cursor);
                trendList.add(trend);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return trendList;
    }

    private Trend cursorToTrend(Cursor cursor) {

        Trend trend = new Trend();
        trend.setmTerm(cursor.getString(0));
        trend.setmCount(cursor.getInt(1));
        trend.setmDateMiliseconds(cursor.getLong(2));
        trend.setmIdTweet(cursor.getLong(3));

        return trend;
    }

    public void deleteInPopularTrends() {

        String countQuery = "" +
                "DELETE \n" +
                "FROM \n" +
                dbHelper.TABLE_TREND + "\n" +
                "WHERE term NOT IN (\n" +
                    "    SELECT TERM \n" +
                    "    FROM \n" +
                    dbHelper.TABLE_TREND + "\n" +
                    "    ORDER BY count DESC \n" +
                    "    LIMIT 50 \n" +
                ")" +
                "AND term != ?;";

        database.execSQL(countQuery, new String [] {TWITTER_SINCE_ID} );
    }
}
