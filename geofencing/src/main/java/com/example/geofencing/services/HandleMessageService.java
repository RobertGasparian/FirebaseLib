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
import com.dreizak.miniball.highdim.Miniball;
import com.dreizak.miniball.model.ArrayPointSet;
import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.models.GeoQueue;
import com.example.geofencing.models.GeofenceInfo;
import com.example.geofencing.receivers.GeofenceEventReceiver;
import com.example.geofencing.util.HelpersPolyUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 8/14/2017.
 */

public class HandleMessageService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient googleApiClient;

    private final int ACTION_ADD = 1;
    private final int ACTION_DELETE = 2;
    private final int PENDING_INTENT_ID = 123;
    private final int LATITUDE_POSITION = 0;
    private final int LONGITUDE_POSITION = 1;
    private final float CONVERT_TO_METERS = 100000f;

    public static final String MESSAGE = "message";
    public static final String POLYGON = "polygon";
    private final String GEO_ID = "geoId";
    private final String ACTION = "action";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";

    private String polygonString;
    private Miniball miniball;
    private String geoId;
    private List<LatLng> polygon = new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            handleJson(intent.getStringExtra(MESSAGE));

        return START_NOT_STICKY;
    }

    public void handleJson(String coordinates) {


        int action;
        JSONObject jsonObject;
        if (polygon.size() != 0) {
            polygon.clear();
        }

        try {
            jsonObject = new JSONObject(coordinates);
            geoId = jsonObject.getString(GEO_ID);
            action = jsonObject.getInt(ACTION);
            if (action == ACTION_DELETE) {
                DBHelper dbHelper = new DBHelper(this);
                dbHelper.addQueue(new GeoQueue(geoId, action, null));
            } else {
                JSONArray polygonJson = jsonObject.getJSONArray(POLYGON);
                for (int i = 0; i < polygonJson.length(); i++) {
                    LatLng latLng = new LatLng(Double.valueOf(polygonJson.getJSONObject(i).getString(LATITUDE)), Double.valueOf(polygonJson.getJSONObject(i).getString(LONGITUDE)));
                    polygon.add(latLng);
                }
                polygonString = HelpersPolyUtil.encode(polygon);
                miniball = getSmallestEnclosingCircle(HelpersPolyUtil.decode(polygonString));
                DBHelper dbHelper = new DBHelper(HandleMessageService.this);
                dbHelper.addQueue(new GeoQueue(geoId, action, polygonString));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        createGoogleApi(this);
        googleApiClient.connect();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        DBHelper dbHelper = new DBHelper(this);
        List<GeoQueue> queues = dbHelper.getAllQueues();
        for (GeoQueue geoQueue :
                queues) {
            if (geoQueue.getAction() == ACTION_ADD) {

                double[] coordinates = miniball.center();


                GeofenceInfo geofenceInfo = new GeofenceInfo();
                geofenceInfo.setLatitude(String.valueOf(coordinates[LATITUDE_POSITION]));
                geofenceInfo.setLongitude(String.valueOf(coordinates[LONGITUDE_POSITION]));
                float miniballRadius = (float) miniball.radius() * CONVERT_TO_METERS;
                geofenceInfo.setRadius(String.valueOf(miniballRadius));
                geofenceInfo.setId(geoId);

                addGeofenceMonitor(geofenceInfo.getLatitudeDouble(), geofenceInfo.getLongitudeDouble(), geofenceInfo.getRadiusFloat(), geofenceInfo.getId(), geofenceInfo);


            } else if (geoQueue.getAction() == ACTION_DELETE) {

                String delete_id = geoQueue.getGeoId();
                deleteGeofence(delete_id);

            }


        }


    }


    public void addGeofenceMonitor(double latitude, double longitude, float radius, final String geoId, final GeofenceInfo geofenceInfo) {


        Geofence.Builder builder = new Geofence.Builder();

        builder.setRequestId(geoId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(getMillisTillMidnight())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest.Builder requestBuilder = new GeofencingRequest.Builder();

        requestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(builder.build());

        final Intent intent = new Intent(GeofenceEventReceiver.GEOFENCE_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        createGoogleApi(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            LocationServices.GeofencingApi.addGeofences(googleApiClient, requestBuilder.build(), pendingIntent).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {

                        DBHelper dbHelper = new DBHelper(HandleMessageService.this);
                        dbHelper.addGeofenceInfo(geofenceInfo);
                        for (int i = 0; i < polygon.size(); i++) {

                            dbHelper.addPolygonVertex(polygon.get(i), geoId, i);

                        }

                        dbHelper.deleteQueue(geofenceInfo.getId());
                        if (dbHelper.getAllQueues().size() == 0) {
                            googleApiClient.disconnect();
                        }

                    } else {

                        DBHelper dbHelper = new DBHelper(HandleMessageService.this);
                        dbHelper.deleteQueue(geofenceInfo.getId());
                        if (dbHelper.getAllQueues().size() == 0) {
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

                    DBHelper dbHelper = new DBHelper(HandleMessageService.this);
                    dbHelper.deleteGeofenceInfo(id);
                    dbHelper.deletePolygon(id);
                    Intent intent = new Intent(HandleMessageService.this, LocationPolygonMonitorService.class);
                    intent.setAction(GeofenceEventReceiver.DELETE_POLYGON);
                    intent.putExtra(GeofenceEventReceiver.GEO_ID, id);
                    startService(intent);
                    dbHelper.deleteQueue(id);
                    if (dbHelper.getAllQueues().size() == 0) {

                        googleApiClient.disconnect();
                    }

                } else {

                    DBHelper dbHelper = new DBHelper(HandleMessageService.this);
                    dbHelper.deleteQueue(id);

                    if (dbHelper.getAllQueues().size() == 0) {

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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Miniball getSmallestEnclosingCircle(List<LatLng> locations) {
        ArrayPointSet arrayPointSet = new ArrayPointSet(2, locations.size()); // 2 dimensions

        for (int i = 0; i < locations.size(); i++) {
            LatLng latLng = locations.get(i);
            arrayPointSet.set(i, 0, latLng.latitude);
            arrayPointSet.set(i, 1, latLng.longitude);
        }

        return new Miniball(arrayPointSet);
    }

}
