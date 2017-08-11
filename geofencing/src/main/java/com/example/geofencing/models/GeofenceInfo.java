package com.example.geofencing.models;

/**
 * Created by User on 8/11/2017.
 */

public class GeofenceInfo {


    private String latitude;
    private String longitude;
    private String radius;
    private int id;

    public GeofenceInfo() {
    }

    public GeofenceInfo(int id, String latitude, String longitude, String radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.id = id;
    }

    public GeofenceInfo(String latitude, String longitude, String radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
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

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitudeDouble(){
        return Double.valueOf(latitude);
    }

    public double getLongitudeDouble(){
        return Double.valueOf(longitude);
    }

    public float getRadiusFloat(){
        return Float.valueOf(radius);
    }
}
