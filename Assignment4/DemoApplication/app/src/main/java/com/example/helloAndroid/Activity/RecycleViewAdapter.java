package com.example.helloAndroid.Activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helloAndroid.R;
import com.example.helloAndroid.Result.Result;

import java.util.List;
import java.util.Random;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private List<Result> locations;
    private Context context;

    public RecycleViewAdapter(List<Result> locations, Context context) {
        this.locations = locations;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_line, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.itemView.setBackgroundColor(currentColor);
        holder.application.setText(locations.get(position).getApplication());
        holder.longitude.setText(String.valueOf(locations.get(position).getLongitude()));
        holder.latitude.setText(String.valueOf(locations.get(position).getLatitude()));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView application;
        private TextView longitude;
        private TextView latitude;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            application = itemView.findViewById(R.id.tv_app_name);
            longitude = itemView.findViewById(R.id.tv_longitude);
            latitude = itemView.findViewById(R.id.tv_latitude);
        }
    }
}
