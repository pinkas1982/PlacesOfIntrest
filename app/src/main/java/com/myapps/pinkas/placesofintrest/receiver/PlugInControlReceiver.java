package com.myapps.pinkas.placesofintrest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pinkas on 4/7/2016.
 */
public class PlugInControlReceiver extends BroadcastReceiver {
    public void onReceive(Context context , Intent intent) {
        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "POWER_CONNECTED",Toast.LENGTH_LONG).show();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, "POWER_DISCONNECTED",Toast.LENGTH_LONG).show();
        }
    }
}
