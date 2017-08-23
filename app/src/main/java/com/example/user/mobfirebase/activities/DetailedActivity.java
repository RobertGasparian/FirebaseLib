package com.example.user.mobfirebase.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.geofencing.helpers.DBHelper;
import com.example.user.mobfirebase.R;
import com.example.user.mobfirebase.adapters.PolygonListAdapter;

public class DetailedActivity extends AppCompatActivity {

    public static final String GEO_ID = "geoId";

    private RecyclerView polygonRv;
    private TextView geoId;

    private PolygonListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        geoId = (TextView) findViewById(R.id.polygon_geo_id);
        polygonRv = (RecyclerView) findViewById(R.id.polygon_rv);
        Intent intent = getIntent();
        geoId.setText(intent.getStringExtra(GEO_ID));
        DBHelper dbHelper = new DBHelper(this);
        adapter = new PolygonListAdapter(this, dbHelper.getPolygon(intent.getStringExtra(GEO_ID)));
        polygonRv.setLayoutManager(new LinearLayoutManager(this));
        polygonRv.setAdapter(adapter);

    }
}
