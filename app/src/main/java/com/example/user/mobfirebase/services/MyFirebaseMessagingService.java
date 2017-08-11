package com.example.user.mobfirebase.services;
import android.util.Log;
import android.widget.Toast;

import com.example.geofencing.controllers.GeoController;
import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.models.GeofenceInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.radius;

/**
 * Created by User on 8/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("rob","mtav");
        String coordinates = remoteMessage.getNotification().getBody();

        GeoController geoController = GeoController.getController();
        geoController.handleJson(coordinates,getApplicationContext());

    }
}
