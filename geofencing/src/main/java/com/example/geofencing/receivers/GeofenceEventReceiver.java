package com.example.geofencing.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.geofencing.R;
import com.example.geofencing.controllers.LibController;
import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.services.LocationPolygonMonitorService;
import com.example.geofencing.util.HelpersPolyUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class GeofenceEventReceiver extends BroadcastReceiver {

    public static final String GEOFENCE_ACTION = "com.example.geofencing.receivers.ENTER_INTENT";
    public static final String ENTER_POLYGON = "com.example.geofencing.receivers.ENTER_POLYGON_INTENT";
    public static final String ADD_POLYGON = "add polygon";
    public static final String DELETE_POLYGON = "delete polygon";
    public static final String POLYGON_STRING = "polygonString";
    public static final String GEO_ID = "geoId";


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(GEOFENCE_ACTION)) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int geofencingType = geofencingEvent.getGeofenceTransition();

            if (geofencingType == Geofence.GEOFENCE_TRANSITION_ENTER) {

                DBHelper dbHelper = new DBHelper(context);
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Geofence triggeredGeofence :
                        triggeringGeofences) {
                    List<LatLng> polygon = dbHelper.getPolygon(triggeredGeofence.getRequestId());
                    String polygonString = HelpersPolyUtil.encode(polygon);
                    Intent serviceIntent = new Intent(context, LocationPolygonMonitorService.class);
                    serviceIntent.setAction(ADD_POLYGON);
                    Log.d(LibController.LIB_TAG, "geo enter - " + triggeredGeofence.getRequestId());
                    serviceIntent.putExtra(POLYGON_STRING, polygonString);
                    serviceIntent.putExtra(GEO_ID, triggeredGeofence.getRequestId());
                    context.startService(serviceIntent);
                }


            } else if (geofencingType == Geofence.GEOFENCE_TRANSITION_EXIT) {

                Log.d(LibController.LIB_TAG, "geo exit");

                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Geofence triggeredGeofence :
                        triggeringGeofences) {

                    Intent serviceIntent = new Intent(context, LocationPolygonMonitorService.class);
                    serviceIntent.setAction(DELETE_POLYGON);
                    Log.d(LibController.LIB_TAG, "deleting key is " + triggeredGeofence.getRequestId());
                    serviceIntent.putExtra(GEO_ID, triggeredGeofence.getRequestId());
                    context.startService(serviceIntent);
                }
            }
        } else if (intent.getAction().equals(ENTER_POLYGON)) {

            int not_id = (int) System.currentTimeMillis();
            Log.d(LibController.LIB_TAG, "polygon enter " + intent.getStringExtra(GEO_ID));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(context.getString(R.string.enter))
                    .setContentText(intent.getStringExtra(GEO_ID))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_place_black_24dp);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(not_id, builder.build());
            Log.d(LibController.LIB_TAG, "notification - " + intent.getStringExtra(GEO_ID));

        }

    }

}
