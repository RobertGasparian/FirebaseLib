package com.example.geofencing.models;

/**
 * Created by User on 8/17/2017.
 */

public class PolygonWithOrder {

    private String latitude;
    private String longitude;
    private int order;

    public PolygonWithOrder(String latitude, String longitude, int order) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
