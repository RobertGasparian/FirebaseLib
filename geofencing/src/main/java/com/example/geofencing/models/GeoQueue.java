package com.example.geofencing.models;

/**
 * Created by User on 8/15/2017.
 */

public class GeoQueue {

    private String geoId;
    private int id;
    private int action;
    private String JSON = null;


    public GeoQueue(int id, String geoId, int action, String JSON) {
        this.geoId = geoId;
        this.id = id;
        this.action = action;
        this.JSON = JSON;
    }


    public GeoQueue(String geoId, int action, String JSON) {
        this.geoId = geoId;
        this.action = action;
        this.JSON = JSON;
    }

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
