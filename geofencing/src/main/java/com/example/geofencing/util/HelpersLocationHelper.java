package com.example.geofencing.util;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.example.geofencing.services.LocationPolygonMonitorService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

/**
 * Created by polbins on 14/05/2016.
 */
public class HelpersLocationHelper {



    static LatLng convertLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static boolean isPointInPolygon(Location location, List<LatLng> locations) {
        return HelpersPolyUtil.containsLocation(
                HelpersLocationHelper.convertLocationToLatLng(location), locations, true);
    }


}
