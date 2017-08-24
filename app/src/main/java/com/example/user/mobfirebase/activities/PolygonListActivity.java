package com.example.user.mobfirebase.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.polygon_monitor.helpers.DBHelper;
import com.example.user.mobfirebase.R;
import com.example.user.mobfirebase.adapters.PolygonListAdapter;

public class PolygonListActivity extends AppCompatActivity {

    public static final String GEO_ID = "geoId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygon_list);
        TextView geoId = (TextView) findViewById(R.id.polygon_geo_id);
        RecyclerView polygonRv = (RecyclerView) findViewById(R.id.polygon_rv);
        Intent intent = getIntent();
        geoId.setText(intent.getStringExtra(GEO_ID));
        DBHelper dbHelper = new DBHelper(this);
        PolygonListAdapter adapter = new PolygonListAdapter(this, dbHelper.getPolygon(intent.getStringExtra(GEO_ID)));
        polygonRv.setLayoutManager(new LinearLayoutManager(this));
        polygonRv.setAdapter(adapter);

    }
}
