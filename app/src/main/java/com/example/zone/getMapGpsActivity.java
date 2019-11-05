package com.example.zone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapView;

public class getMapGpsActivity extends AppCompatActivity {
    MapView mapView;
    ViewGroup mapViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_map_gps);
        mapView = (MapView) findViewById(R.id.map_view);

        mapView.setDaumMapApiKey("dccc7c0ddbd4beddfdaf5655ef4463ce");
        mapViewContainer = (ViewGroup) findViewById(R.id.getgps_map);

    }
}
