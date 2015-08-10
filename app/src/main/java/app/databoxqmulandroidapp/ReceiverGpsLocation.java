package app.databoxqmulandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Created by MateusFelipe on 13/05/2015.
 */
public class ReceiverGpsLocation extends BroadcastReceiver {

    public static final String TAG = "GPS Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Make sure we are getting the right intent
        if( "android.location.PROVIDERS_CHANGED".equals(intent.getAction())) {

            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Dont create service if gps is off
            if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                Intent serviceIntent = new Intent(context, ServGpsLocation.class);
                context.startService(serviceIntent);
            }

            manager = null;

        }

    }


}
