package com.example.user.mobfirebase.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.geofencing.helpers.DBHelper;
import com.example.geofencing.models.GeofenceInfo;
import com.example.user.mobfirebase.R;
import com.example.user.mobfirebase.adapters.GeoAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<GeofenceInfo> geoList;
    private GeoAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private DBHelper dbHelper;


    private final int LOCATION_ACCESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS);
                return;
            } else {
                init();
            }

        } else {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.refreshData(dbHelper.getAllGeofences());
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.geo_rv);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_main);
        dbHelper = new DBHelper(this);
        geoList = dbHelper.getAllGeofences();
        adapter = new GeoAdapter(geoList, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case LOCATION_ACCESS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    init();

                } else {
                    Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2500);
                }
        }
    }


}
