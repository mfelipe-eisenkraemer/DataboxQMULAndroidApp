package app.databoxqmulandroidapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.databoxqmulandroidapp.database.DbLocation;

/**
 * Created by MateusFelipe on 12/05/2015.
 */
public class GpsLocationDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbLocation dbHelper;

    public GpsLocationDataSource(Context context) {
        dbHelper = new DbLocation(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public GpsLocation insertPlace(GpsLocation location) {
        ContentValues values = new ContentValues();
        values.put( dbHelper.COLUMN_LATITUDE, location.getmLatitude());
        values.put( dbHelper.COLUMN_LONGITUDE, location.getmLongitude());
        values.put(dbHelper.COLUMN_ACCURARY, location.getmAccuracy());

        long insertId = database.insert(dbHelper.TABLE_LOCATION, null, values);

        Cursor cursor = database.query(dbHelper.TABLE_LOCATION,
                dbHelper.columns, DbLocation.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        GpsLocation newLocation = null;
        if(cursor != null){
            cursor.moveToFirst();
            newLocation = cursorToPlace(cursor);
            cursor.close();
        }

        return newLocation;
    }

    public ArrayList<GpsLocation> getAllPlaces() {

        ArrayList<GpsLocation> placesList = new ArrayList<GpsLocation>();

        Cursor cursor = database.query(dbHelper.TABLE_LOCATION,
                dbHelper.columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GpsLocation location = cursorToPlace(cursor);
                placesList.add(location);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        }
        return placesList;
    }

    private GpsLocation cursorToPlace(Cursor cursor) {
        GpsLocation location = new GpsLocation();
        location.setId(cursor.getLong(0));
        location.setmLatitude(cursor.getFloat(1));
        location.setmLongitude(cursor.getFloat(2));
        location.setmAccuracy(cursor.getFloat(3));

        return location;
    }


    public void deletePlace(GpsLocation location) {}

}
