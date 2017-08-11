package com.example.geofencing.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geofencing.models.GeofenceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofenceManager";
    private static final String TABLE_GEOFENCES = "geofences";

    private static final String _ID = "id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String RADIUS = "radius";

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
                "(" + _ID + PRIMARY_KEY_INT
                + LATITUDE + TEXT_TYPE + ","
                + LONGITUDE + TEXT_TYPE + ","
                + RADIUS + TEXT_TYPE  + ")";



        db.execSQL(CREATE_GEOFENCES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL(DROP_TABLE_EXISTS + TABLE_GEOFENCES);
        onCreate(db);
    }

    public void addGeofenceInfo(GeofenceInfo geofenceInfo){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LATITUDE,geofenceInfo.getLatitude());
        values.put(LONGITUDE,geofenceInfo.getLongitude());
        values.put(RADIUS,geofenceInfo.getRadius());
        db.insert(TABLE_GEOFENCES,null,values);
        db.close();

    }

    public int getLastGeofenceInfoId(){

        String selectQuery = SELECT_FROM_ALL + TABLE_GEOFENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        GeofenceInfo geofenceInfo = new GeofenceInfo(cursor.getInt(cursor.getColumnIndex(DBHelper._ID)),
                cursor.getString(cursor.getColumnIndex(DBHelper.LATITUDE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.RADIUS)));
        cursor.close();

        return geofenceInfo.getId();

    }

    public void deleteGeofenceInfo(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GEOFENCES, _ID + "=?", new String[]{String.valueOf(id)});
        db.close();

    }

    public List<GeofenceInfo> getAllGeofences() {

        List<GeofenceInfo> geofenceInfos = new ArrayList<>();
        String selectQuery = SELECT_FROM_ALL + TABLE_GEOFENCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            GeofenceInfo geofenceInfo = new GeofenceInfo(cursor.getInt(cursor.getColumnIndex(DBHelper._ID)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.RADIUS)));
            geofenceInfos.add(geofenceInfo);

        }
        cursor.close();

        return geofenceInfos;

    }
}
