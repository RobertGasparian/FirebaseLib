package com.example.geofencing.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.geofencing.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeoFenceReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_REQ_CODE = 0;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("com.example.geofencing.receivers.ENTER_INTENT")) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int geofencingType = geofencingEvent.getGeofenceTransition();

            if (geofencingType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle("Enter")
                        .setContentText(getIdString(triggeringGeofences))
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_place_black_24dp);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_REQ_CODE, builder.build());
            }
        }

    }

    private String getIdString(List<Geofence> geofences){

        String ids="";
        for (Geofence geofence :
                geofences) {
            ids=ids+", " + geofence.getRequestId();
        }

        ids=ids+".";

        return ids;
    }


}

