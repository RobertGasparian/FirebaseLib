package com.example.geofencing.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.models.GeoQueue;
import com.example.geofencing.models.GeofenceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 8/14/2017.
 */

public class HandleJsonService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient googleApiClient;

    private final int ACTION_ADD = 1;
    private final int ACTION_DELETE = 2;
    public static final String MESSAGE = "message";
    private final String ENTER_ACTION= "com.example.geofencing.receivers.ENTER_INTENT";

    private final String GEO_ID= "geoId";
    private final String ACTION= "action";
    private final String GEOFENCING_INFO= "geofencing_info";
    private final String LATITUDE= "latitude";
    private final String LONGITUDE= "longitude";
    private final String RADIUS= "radius";




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handleJson(intent.getStringExtra(MESSAGE));

        return START_STICKY;
    }

    public void handleJson(String coordinates) {

        String geoId = null;
        String JSON = null;
        int action = 0;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(coordinates);
            geoId = jsonObject.getJSONObject(GEO_ID).getString(GEO_ID);
            action = jsonObject.getJSONObject(ACTION).getInt(ACTION);
            if(jsonObject.opt(GEOFENCING_INFO)==null){
                DBHelper dbHelper = new DBHelper(this);
                dbHelper.addQueue(new GeoQueue(geoId, action, null));
            }else{
                JSON = jsonObject.opt(GEOFENCING_INFO).toString();

                DBHelper dbHelper = new DBHelper(this);
                dbHelper.addQueue(new GeoQueue(geoId, action, JSON));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        createGoogleApi(this);
        googleApiClient.connect();


    }

    public void addGeofenceMonitor(double latitude, double longitude, float radius, String geoId, final GeofenceInfo geofenceInfo) {



        Geofence.Builder builder = new Geofence.Builder();

        builder.setRequestId(geoId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(getMillisTillMidnight())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest.Builder requestBuilder = new GeofencingRequest.Builder();

        requestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(builder.build());

        Intent intent = new Intent(ENTER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        createGoogleApi(this);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



            LocationServices.GeofencingApi.addGeofences(googleApiClient, requestBuilder.build(), pendingIntent).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {

                        DBHelper dbHelper = new DBHelper(HandleJsonService.this);
                        dbHelper.addGeofenceInfo(geofenceInfo);
                        dbHelper.deleteQueue(geofenceInfo.getId());
                        if(dbHelper.getAllQueues().size()==0){
                            googleApiClient.disconnect();
                        }

                    } else {

                        DBHelper dbHelper = new DBHelper(HandleJsonService.this);
                        dbHelper.deleteQueue(geofenceInfo.getId());
                        if(dbHelper.getAllQueues().size()==0){
                            googleApiClient.disconnect();
                        }


                    }
                }
            });


        }


    }

    public void deleteGeofence(final String id) {


        List<String> idList = new ArrayList<>();
        idList.add(id);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, idList).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {

                    DBHelper dbHelper = new DBHelper(HandleJsonService.this);
                    dbHelper.deleteGeofenceInfo(id);
                    dbHelper.deleteQueue(id);
                    if(dbHelper.getAllQueues().size()==0){

                        googleApiClient.disconnect();
                    }

                } else {

                    DBHelper dbHelper = new DBHelper(HandleJsonService.this);
                    dbHelper.deleteQueue(id);

                    if(dbHelper.getAllQueues().size()==0){

                        googleApiClient.disconnect();
                    }

                }
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


    private void createGoogleApi(Context context) {

        if (googleApiClient == null) {



            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }




    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        DBHelper dbHelper = new DBHelper(this);
        List<GeoQueue> queues = dbHelper.getAllQueues();
        for (GeoQueue geoQueue :
                queues) {
            if (geoQueue.getAction() == ACTION_ADD) {


                try {
                    JSONObject jsonObject = new JSONObject(geoQueue.getJSON());
                    GeofenceInfo geofenceInfo = new GeofenceInfo();
                    geofenceInfo.setLatitude(jsonObject.getString(LATITUDE));
                    geofenceInfo.setLongitude(jsonObject.getString(LONGITUDE));
                    geofenceInfo.setRadius(jsonObject.getString(RADIUS));
                    geofenceInfo.setId(jsonObject.getString(GEO_ID));

                    addGeofenceMonitor(geofenceInfo.getLatitudeDouble(), geofenceInfo.getLongitudeDouble(), geofenceInfo.getRadiusFloat(), geofenceInfo.getId(), geofenceInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (geoQueue.getAction() == ACTION_DELETE) {

                String delete_id = geoQueue.getGeoId();
                deleteGeofence(delete_id);

            }


        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
