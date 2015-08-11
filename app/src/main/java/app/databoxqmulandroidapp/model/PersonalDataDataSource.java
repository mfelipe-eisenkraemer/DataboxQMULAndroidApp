package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.databoxqmulandroidapp.database.DbPersonalData;

/**
 * Created by MateusFelipe on 19/06/2015.
 */
public class PersonalDataDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbPersonalData dbHelper;

    public PersonalDataDataSource(Context context) {
        dbHelper = new DbPersonalData(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public PersonalData insertPersonalData(PersonalData personalData) {
        ContentValues values = new ContentValues();
        values.put( dbHelper.COLUMN_NAME, personalData.getmName());
        values.put( dbHelper.COLUMN_GENDER, personalData.getmGender());
        values.put( dbHelper.COLUMN_LOCALE, personalData.getmLocale());
        values.put( dbHelper.COLUMN_LINK, personalData.getmLink());
        values.put( dbHelper.COLUMN_AGE_MIN, personalData.getmAgeMin());
        values.put( dbHelper.COLUMN_AGE_MAX, personalData.getmAgeMax());

        long insertId = database.insert(dbHelper.TABLE_PERSONAL_DATA, null, values);

        Cursor cursor = database.query(dbHelper.TABLE_PERSONAL_DATA,
                dbHelper.columns, DbPersonalData.COLUMN_ID + " = " + insertId, null,
                null, null, null);


        PersonalData newData = null;
        if( cursor != null ){
            cursor.moveToFirst();
            newData = cursorToPersonalData(cursor);
            cursor.close();
        }
        return newData;
    }

    public ArrayList<PersonalData> getAllPersonalData() {

        ArrayList<PersonalData> dataList = new ArrayList<PersonalData>();

        Cursor cursor = database.query(dbHelper.TABLE_PERSONAL_DATA,
                dbHelper.columns, null, null, null, null, null);

        if( cursor != null ){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PersonalData data = cursorToPersonalData(cursor);
                dataList.add(data);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }

        return dataList;
    }

    public PersonalData getFirstPersonalData() {

        Cursor cursor = database.query(dbHelper.TABLE_PERSONAL_DATA,
                dbHelper.columns, null, null, null, null, null, "1");

        PersonalData data = null;
        if( cursor != null ){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data = cursorToPersonalData(cursor);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }

        return data;
    }

    private PersonalData cursorToPersonalData(Cursor cursor) {
        PersonalData data = new PersonalData();
        data.setmId(cursor.getLong(0));
        data.setmName(cursor.getString(1));
        data.setmGender(cursor.getString(2));
        data.setmLocale(cursor.getString(3));
        data.setmLink(cursor.getString(4));
        data.setmAgeMin(cursor.getInt(5));
        data.setmAgeMax(cursor.getInt(6));

        return data;
    }


    public void deletePlace(GpsLocation location) {}
}
