package com.example.geofencing.controllers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.models.GeofenceInfo;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by User on 8/10/2017.
 */

public class GeoController {

    static GeoController instance = null;

    private final int ACTION_ADD=1;
    private final int ACTION_DELETE=2;

    private GeoController() {

    }

    public static GeoController getController() {

        if (instance == null) {
            instance = new GeoController();
        }

        return instance;
    }

    public void handleJson(String coordinates,Context context){


        int action = 0;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(coordinates);
            action=jsonObject.getJSONObject("action").getInt("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(action==ACTION_ADD) {
            try {
                GeofenceInfo geofenceInfo = new GeofenceInfo();
                geofenceInfo.setLatitude(jsonObject.getJSONObject("geofencing_info").getString("latitude"));
                geofenceInfo.setLongitude(jsonObject.getJSONObject("geofencing_info").getString("longitude"));
                geofenceInfo.setRadius(jsonObject.getJSONObject("geofencing_info").getString("radius"));
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.addGeofenceInfo(geofenceInfo);
                geofenceInfo.setId(dbHelper.getLastGeofenceInfoId());
                setCoordinates(geofenceInfo.getLatitudeDouble(), geofenceInfo.getLongitudeDouble(), geofenceInfo.getRadiusFloat(), context, geofenceInfo.getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }else if(action==ACTION_DELETE){
            try {
                int id = jsonObject.getJSONObject("geofence_id").getInt("id");
               deleteGeofence(id,context);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void setCoordinates(double latitude, double longitude, float radius, Context context, int id) {


        Geofence.Builder builder = new Geofence.Builder();
        builder.setRequestId(String.valueOf(id))
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(getMillisTillMidnight())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest.Builder requestBuilder = new GeofencingRequest.Builder();
        requestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(builder.build());
        Intent intent = new Intent("com.example.geofencing.receivers.ENTER_INTENT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(requestBuilder.build(), pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("rob", "Success");
                }
            }).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("rob", "failure");
                }
            });
            return;
        }



    }

    public void deleteGeofence(final int id, final Context context){


        List<String> idList = new ArrayList<>();
        idList.add(String.valueOf(id));
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        geofencingClient.removeGeofences(idList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("rob","Delete Success");
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.deleteGeofenceInfo(id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("rob", "Delete Failure");
            }
        });


    }

    public long getMillisTillMidnight() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public List<GeofenceInfo> getCurrentGeofences(Context context){

        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getAllGeofences();
    }

}
