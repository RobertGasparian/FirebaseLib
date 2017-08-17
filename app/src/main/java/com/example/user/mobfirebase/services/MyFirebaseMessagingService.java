package com.example.user.mobfirebase.services;
import android.content.Intent;

import com.example.geofencing.services.HandleJsonService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by User on 8/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String coordinates = remoteMessage.getNotification().getBody();
        Intent intent = new Intent(MyFirebaseMessagingService.this, HandleJsonService.class);
        intent.putExtra(HandleJsonService.MESSAGE,coordinates);
        startService(intent);

    }
}
