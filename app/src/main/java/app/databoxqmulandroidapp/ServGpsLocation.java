package app.databoxqmulandroidapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Service;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import app.databoxqmulandroidapp.model.GpsLocationDataSource;
import app.databoxqmulandroidapp.model.GpsLocation;

//http://blog.teamtreehouse.com/beginners-guide-location-android
public class ServGpsLocation extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = "BackgroundLocation";

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private Location mLastLocationReading;

    // default minimum time between new readings ( 5 MINUTES )
    private long mMinTime = 5 * 60 * 1000;

    // default minimum distance between old and new readings.
    private float mMinDistance = 0;

    private static final long FIVE_MINS = 5 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("onStartCommand", "DONE");
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("STOP_SERVICE", "DONE");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        // not high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Update location every second
        mLocationRequest.setInterval(mMinTime);
        // Min distance for location to be accepted
        mLocationRequest.setSmallestDisplacement(mMinDistance);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {

        if(isBetterLocation(location, mLastLocationReading)) {
            mLastLocationReading = location;

            GpsLocation place = new GpsLocation( location );

            GpsLocationDataSource gpsLocationDataSource = new GpsLocationDataSource(getApplicationContext());
            gpsLocationDataSource.open();
            gpsLocationDataSource.insertPlace(place);
            gpsLocationDataSource.close();
        }

        //Log.i("Location received: ", location.toString());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > FIVE_MINS;
        boolean isSignificantlyOlder = timeDelta < -FIVE_MINS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
