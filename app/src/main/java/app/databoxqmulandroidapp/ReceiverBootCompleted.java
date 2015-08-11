package app.databoxqmulandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MateusFelipe on 03/07/2015.
 */
public class ReceiverBootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Intent startServiceIntent = new Intent(context, ServTrendAnalysis.class);
            context.startService(startServiceIntent);

        }
    }
}