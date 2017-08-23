package com.example.geofencing.controllers;

import android.content.Context;
import android.content.Intent;

import com.example.geofencing.services.HandleMessageService;

/**
 * Created by User on 8/23/2017.
 */

public class LibController {

    public static final String LIB_TAG = "rob";

    private static LibController instance = null;

    private LibController() {

    }

    public static LibController getController() {

        if (instance == null) {
            instance = new LibController();
        }

        return instance;
    }

    public void setMessage(Context context, String message) {
        Intent intent = new Intent(context, HandleMessageService.class);
        intent.putExtra(HandleMessageService.MESSAGE, message);
        context.startService(intent);
    }
}
