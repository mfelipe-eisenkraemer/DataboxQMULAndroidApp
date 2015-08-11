package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import app.databoxqmulandroidapp.database.DbSentConfirmation;

/**
 * Created by MateusFelipe on 26/07/2015.
 */
public class SentConfirmationDataSource {
    // Database fields
    private SQLiteDatabase database;
    private DbSentConfirmation dbHelper;

    public SentConfirmationDataSource(Context context) {
        dbHelper = new DbSentConfirmation(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertConfirmation(SentConfirmation sentConfirmation) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_DATE, sentConfirmation.getmDateMiliseconds());

        long insertId = database.insert(dbHelper.TABLE_SENT_CONFIRMATION, null, values);
    }

    public SentConfirmation getConfirmationByDate( Long date) {


        String args[] = { String.valueOf(date) };
        Cursor cursor = database.query(dbHelper.TABLE_SENT_CONFIRMATION,
                dbHelper.columns, dbHelper.COLUMN_DATE + " = ?", args, null, null, null);

        SentConfirmation sentConfirmation = null;
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                sentConfirmation = cursorToConfirmation(cursor);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return sentConfirmation;
    }


    private SentConfirmation cursorToConfirmation(Cursor cursor) {
        SentConfirmation sentConfirmation = new SentConfirmation();
        sentConfirmation.setmDateMiliseconds(cursor.getLong(1));

        return sentConfirmation;
    }


}

