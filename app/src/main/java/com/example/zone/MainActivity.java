package com.example.zone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.zone", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey("dccc7c0ddbd4beddfdaf5655ef4463ce");
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.556, 125.951949155);
        mapView.setMapCenterPoint(mapPoint, true);
        mapViewContainer.addView(mapView);
    }

}
