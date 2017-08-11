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


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("com.example.geofencing.receivers.ENTER_INTENT")) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int geofencingType = geofencingEvent.getGeofenceTransition();

            if (geofencingType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle("Enter")
                        .setContentText(triggeringGeofences.get(triggeringGeofences.size()-1).getRequestId())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_place_black_24dp);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
        }

    }


}

