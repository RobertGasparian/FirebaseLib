package com.example.user.mobfirebase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.mobfirebase.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/21/2017.
 */

public class PolygonListAdapter extends RecyclerView.Adapter<PolygonListAdapter.PolygonHolder> {

    private Context context;
    private List<LatLng> polygonList = new ArrayList<>();


    public PolygonListAdapter(Context context, List<LatLng> polygonList) {
        this.context = context;
        this.polygonList = polygonList;
    }

    @Override
    public PolygonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.polygon_item, parent, false);
        return new PolygonHolder(view);
    }

    @Override
    public void onBindViewHolder(PolygonHolder holder, int position) {
        holder.order.setText(String.valueOf(position));
        holder.latitude.setText(String.valueOf(polygonList.get(position).latitude));
        holder.longitude.setText(String.valueOf(polygonList.get(position).longitude));
    }

    @Override
    public int getItemCount() {
        return polygonList.size();
    }

    class PolygonHolder extends RecyclerView.ViewHolder {

        private TextView order, latitude, longitude;

        public PolygonHolder(View itemView) {
            super(itemView);
            order = (TextView) itemView.findViewById(R.id.polygon_order_text);
            latitude = (TextView) itemView.findViewById(R.id.polygon_lat_text);
            longitude = (TextView) itemView.findViewById(R.id.polygon_long_text);
        }
    }


}
