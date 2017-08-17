package com.example.geofencing.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.geofencing.models.GeoQueue;
import com.example.geofencing.models.GeofenceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = "GGG";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofenceManager";
    private static final String TABLE_GEOFENCES = "geofences";
    private static final String TABLE_QUEUE = "queue";

    private static final String _ID = "id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String RADIUS = "radius";
    private static final String ACTION = "action";
    private static final String GEOFENCE_ID = "geofence_id";
    private static final String JSON_STRING = "JSON";

    private static final String TEXT_TYPE = " TEXT";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY_INT = " INTEGER PRIMARY KEY AUTOINCREMENT,";
    private static final String DROP_TABLE_EXISTS = "DROP TABLE IF EXISTS ";
    private static final String SELECT_FROM_ALL = "SELECT  * FROM ";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GEOFENCES_TABLE = CREATE_TABLE + TABLE_GEOFENCES +
                "(" + _ID + TEXT_TYPE + ","
                + LATITUDE + TEXT_TYPE + ","
                + LONGITUDE + TEXT_TYPE + ","
                + RADIUS + TEXT_TYPE + ")";
        String CREATE_QUEUE_TABLE = CREATE_TABLE + TABLE_QUEUE +
                "(" + _ID + PRIMARY_KEY_INT
                + ACTION + TEXT_TYPE + ","
                + GEOFENCE_ID + TEXT_TYPE + ","
                + JSON_STRING + TEXT_TYPE + ")";


        db.execSQL(CREATE_GEOFENCES_TABLE);
        db.execSQL(CREATE_QUEUE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL(DROP_TABLE_EXISTS + TABLE_GEOFENCES);
        db.execSQL(DROP_TABLE_EXISTS + TABLE_QUEUE);
        onCreate(db);
    }

    public void addGeofenceInfo(GeofenceInfo geofenceInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        if(!checkIdExistence(geofenceInfo.getId())) {
            ContentValues values = new ContentValues();

            values.put(_ID, geofenceInfo.getId());
            values.put(LATITUDE, geofenceInfo.getLatitude());
            values.put(LONGITUDE, geofenceInfo.getLongitude());
            values.put(RADIUS, geofenceInfo.getRadius());
            long inserted = db.insert(TABLE_GEOFENCES, null, values);
            Log.d(TAG, String.valueOf(inserted));
            db.close();
        }else {
            updateGeofence(geofenceInfo);
        }
    }

    public void updateGeofence(GeofenceInfo geofenceInfo) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(_ID, geofenceInfo.getId());
        values.put(LATITUDE, geofenceInfo.getLatitude());
        values.put(LONGITUDE, geofenceInfo.getLongitude());
        values.put(RADIUS, geofenceInfo.getRadius());

        db.update(TABLE_GEOFENCES, values, _ID + "=?", new String[]{String.valueOf(geofenceInfo.getId())});

    }

    private boolean checkIdExistence(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_QUEUE, new String[]{_ID}, _ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();

            return true;
        } else {
            cursor.close();

            return false;
        }


    }

    public void deleteGeofenceInfo(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GEOFENCES, _ID + "=?", new String[]{id});
        db.close();

    }

    public List<GeofenceInfo> getAllGeofences() {

        List<GeofenceInfo> geofenceInfos = new ArrayList<>();
        String selectQuery = SELECT_FROM_ALL + TABLE_GEOFENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            GeofenceInfo geofenceInfo = new GeofenceInfo(cursor.getString(cursor.getColumnIndex(DBHelper._ID)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.RADIUS)));
            geofenceInfos.add(geofenceInfo);

        }
        cursor.close();

        return geofenceInfos;

    }

    public void addQueue(GeoQueue geoQueue) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GEOFENCE_ID, geoQueue.getGeoId());
        values.put(ACTION, geoQueue.getAction());
        values.put(JSON_STRING, geoQueue.getJSON());
        db.insert(TABLE_QUEUE, null, values);
        db.close();

    }

    public List<GeoQueue> getAllQueues() {

        List<GeoQueue> geoQueues = new ArrayList<>();
        String selectQuery = SELECT_FROM_ALL + TABLE_QUEUE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            GeoQueue geoQueue = new GeoQueue(cursor.getInt(cursor.getColumnIndex(DBHelper._ID)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.GEOFENCE_ID)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.ACTION)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.JSON_STRING)));
            geoQueues.add(geoQueue);

        }
        cursor.close();

        return geoQueues;

    }

    public void deleteQueue(String id) {


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_QUEUE, new String[]{_ID, GEOFENCE_ID, ACTION, JSON_STRING}, GEOFENCE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        int deleting_id = cursor.getInt(cursor.getColumnIndex(DBHelper._ID));
        cursor.close();
        db.delete(TABLE_QUEUE, _ID + "=?", new String[]{String.valueOf(deleting_id)});
        db.close();
    }

}
