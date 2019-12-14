package com.example.zone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Administrator on 2019-01-24.
 */

public class SplashActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)){
            try{
                Thread.sleep(1500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            //권한 설정 안내 문구 필요
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode==200 && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
            }
        }
    }
}
