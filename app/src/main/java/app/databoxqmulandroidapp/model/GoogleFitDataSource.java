package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.databoxqmulandroidapp.database.DbGoogleFit;

/**
 * Created by MateusFelipe on 22/07/2015.
 */
public class GoogleFitDataSource {
    // Database fields
    private SQLiteDatabase database;
    private DbGoogleFit dbHelper;

    public GoogleFitDataSource(Context context) {
        dbHelper = new DbGoogleFit(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertGoogleFit(GoogleFit googleFit) {
        ContentValues values = new ContentValues();
        values.put( dbHelper.COLUMN_NUM_STEPS, googleFit.getNumSteps());
        values.put(dbHelper.COLUMN_DATE, googleFit.getmDateMiliseconds());
        values.put(dbHelper.COLUMN_SENT_TO_DATABASE, googleFit.getmSentToDatabase());

        long insertId = database.insert(dbHelper.TABLE_GOOGLE_FIT, null, values);
    }

    public ArrayList<GoogleFit> getAllGoogleFit( String order) {

        ArrayList<GoogleFit> googleFitArrayList = new ArrayList<GoogleFit>();

        Cursor cursor = database.query(dbHelper.TABLE_GOOGLE_FIT,
                dbHelper.columns, null, null, null, null, dbHelper.COLUMN_DATE + order);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GoogleFit googleFit = cursorToGoogleFit(cursor);
                googleFitArrayList.add(googleFit);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return googleFitArrayList;
    }

    public ArrayList<GoogleFit> getAllGoogleFitNotSent( String order) {

        ArrayList<GoogleFit> googleFitArrayList = new ArrayList<GoogleFit>();

        int notSent = 0;
        String[] args = { String.valueOf(notSent) };
        Cursor cursor = database.query(dbHelper.TABLE_GOOGLE_FIT,
                dbHelper.columns, dbHelper.COLUMN_SENT_TO_DATABASE + " =  ?", args, null, null, dbHelper.COLUMN_DATE + order);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GoogleFit googleFit = cursorToGoogleFit(cursor);
                googleFitArrayList.add(googleFit);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return googleFitArrayList;
    }

    public void updateSentToDatabase(GoogleFit googleFit){



        ContentValues newValues = new ContentValues();
        newValues.put(dbHelper.COLUMN_SENT_TO_DATABASE, 1);

        String strFilter = dbHelper.COLUMN_NUM_STEPS + " = ? AND " + dbHelper.COLUMN_DATE + " = ? ";
        String[] args = new String[]{ String.valueOf(googleFit.getNumSteps()), String.valueOf(googleFit.getmDateMiliseconds())};

        database.update(dbHelper.TABLE_GOOGLE_FIT, newValues, strFilter, args);

    }

    public int getGoogleFitCount(){

        String countQuery = "SELECT  1 FROM " + dbHelper.TABLE_GOOGLE_FIT;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    private GoogleFit cursorToGoogleFit(Cursor cursor) {
        GoogleFit googleFit = new GoogleFit();
        googleFit.setNumSteps(cursor.getInt(0));
        googleFit.setmDateMiliseconds(cursor.getLong(1));

        return googleFit;
    }


}
