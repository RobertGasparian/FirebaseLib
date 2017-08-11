package com.example.user.mobfirebase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geofencing.models.GeofenceInfo;
import com.example.user.mobfirebase.R;

import java.util.List;

/**
 * Created by User on 8/11/2017.
 */

public class GeoAdapter extends RecyclerView.Adapter<GeoAdapter.GeoHolder>{

    private List<GeofenceInfo> itemList;
    private Context context;


    public GeoAdapter(List<GeofenceInfo> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public GeoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.geo_item,parent,false);
        return new GeoHolder(view);
    }

    @Override
    public void onBindViewHolder(GeoHolder holder, int position) {

        GeofenceInfo item = itemList.get(position);
        holder.latitude.setText(item.getLatitude());
        holder.longitude.setText(item.getLongitude());
        holder.radius.setText(item.getRadius());
        holder.id.setText(String.valueOf(item.getId()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public void refreshData(List<GeofenceInfo> list) {
        itemList=list;
        notifyDataSetChanged();
    }

    class GeoHolder extends RecyclerView.ViewHolder{

        TextView latitude, longitude, radius, id;

        public GeoHolder(View itemView) {
            super(itemView);
            latitude = (TextView)itemView.findViewById(R.id.latitude);
            longitude = (TextView)itemView.findViewById(R.id.longitude);
            radius = (TextView)itemView.findViewById(R.id.radius);
            id = (TextView)itemView.findViewById(R.id.geo_id);

        }
    }
}
