package com.example.user.mobfirebase.services;

import com.example.geofencing.controllers.LibController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by User on 8/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getNotification().getBody();
        LibController libController = LibController.getController();
        libController.setMessage(MyFirebaseMessagingService.this,message);
    }
}
