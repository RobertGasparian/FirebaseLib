package com.example.user.mobfirebase.services;

import com.example.polygon_monitor.controllers.PolygonMonitorController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by User on 8/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getNotification().getBody();
        PolygonMonitorController polygonMonitorController = PolygonMonitorController.getInstance();
        polygonMonitorController.handleFirebaseMessage(MyFirebaseMessagingService.this,message);
    }
}
