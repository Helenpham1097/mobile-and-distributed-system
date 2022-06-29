package com.example.helloAndroid.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helloAndroid.R;
import com.example.helloAndroid.Result.Result;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Menu menu;

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceId = getIntent().getStringExtra("deviceId");

        startGps();

        Timer _Request_Trip_Timer = new Timer();
        _Request_Trip_Timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String url = "http://10.0.2.2:8080/app/location/fetch/"+deviceId;

                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<Result> locations = new ArrayList<>();
                        if(response.isSuccessful()){
                            try {
                                JSONArray array = new JSONArray(response.body().string());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    locations.add(new Result(object.getString("deviceId"),
                                            object.getDouble("lon"),
                                            object.getDouble("lat")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MainActivity.this.runOnUiThread(() -> {
                                recyclerView = findViewById(R.id.lv_location_view);
                                recyclerView.setHasFixedSize(true);
                                layoutManager = new LinearLayoutManager(MainActivity.this);
                                mAdapter = new RecycleViewAdapter(locations, MainActivity.this);
                                recyclerView.setAdapter(mAdapter);
                            });
                        }
                    }
                });
            }
        }, 5, 10000);
    }

    private void startGps() {
        setupPermissions();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                this);

    }

    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(MainActivity.class.getName(), "Permission to record denied");
            makeRequest();
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[] {ACCESS_FINE_LOCATION},
                1);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "http://10.0.2.2:8080/app/location/update/"+deviceId;

        OkHttpClient client = new OkHttpClient();
        JSONObject postData = new JSONObject();

        try {
            postData.put("lon", location.getLongitude());
            postData.put("lat", location.getLatitude());

        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postData.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Updated");
            }
        });
    }
}