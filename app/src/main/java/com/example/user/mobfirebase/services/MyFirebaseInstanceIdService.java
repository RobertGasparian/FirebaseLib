package com.example.user.mobfirebase.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by User on 8/9/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private final String REG_TOKEN = "registration_token";

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);
    }
}
