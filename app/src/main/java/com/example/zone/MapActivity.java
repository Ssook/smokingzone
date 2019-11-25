package com.example.zone;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;

import static net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord;

public class MapActivity extends AppCompatActivity
        implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, NavigationView.OnNavigationItemSelectedListener {
    MapPoint center;
    double curlat;
    double curlng;
    MapView mapView;
    String receiveMsg;
    private MapPOIItem smokeMarker;
    ArrayList<MapPOIItem> smokeMarkerlist = new ArrayList<MapPOIItem>();
    String smokeareainfo = "";
    SharedPreferences sp;
    Toolbar toolbar;
    NavigationView navigationView;
    View nav_header_view;
    TextView nav_header_id_text;
    FloatingActionButton fab;
    FloatingActionButton roadnavi;
    FloatingActionButton addarea;
    FloatingActionButton track;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ViewGroup mapViewContainer;
    ArrayList<MapPOIItem> cafe_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> food_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> school_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> banned_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> street_SmokeMarkerList = new ArrayList<MapPOIItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        toolbar = findViewById(R.id.toolbar);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.example.zone", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        toolbar.setTitle("                       여기서펴");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//View nav_header_view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        nav_header_view = navigationView.getHeaderView(0);

        nav_header_id_text = (TextView) nav_header_view.findViewById(R.id.user_name);
        Intent intent = getIntent();
        System.out.println(intent.getStringExtra("user_name")+"test");
        nav_header_id_text.setText(intent.getStringExtra("user_name"));


        toolbar.setTitle("                       여기서펴");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        roadnavi = findViewById(R.id.roadnavi);
        roadnavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //길찾기 버튼 눌렀을 때
                String strlat = "";
                String strlng = "";

                minDistanceThread t2 = new minDistanceThread();
                t2.start();

                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jo1 = new JSONObject(receiveMsg);

                    strlat = jo1.getString("smoking_area_lat");
                    strlng = jo1.getString("smoking_area_lng");

                    System.out.println(strlat + strlng + "tlqk");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                String url = "daummaps://route?sp=" + "37.537229,127.005515&ep=37.4979502,127.0276368&by=FOOT";//여기에 좌표값 넣어주면 됨

                String url = "daummaps://route?sp=" + curlat + "," + curlng + "&ep=" + strlat + "," + strlng + "&by=FOOT";//여기에 좌표값 넣어주면 됨


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        addarea = findViewById(R.id.ad);
        addarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //길찾기 버튼 눌렀을 때
                Intent intent = new Intent(MapActivity.this, AddSmokingAreaActivity.class);
                intent.putExtra("curlat", curlat);
                intent.putExtra("curlng", curlng);
                startActivity(intent);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mapView = new MapView(this);

        mapView.setDaumMapApiKey("dccc7c0ddbd4beddfdaf5655ef4463ce");
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        center = mapPointWithGeoCoord(curlat, curlng);
        mapView.setMapCenterPointAndZoomLevel(center, 0, true);

         track = findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                center = mapPointWithGeoCoord(curlat, curlng);
                mapView.setMapCenterPointAndZoomLevel(center, 0, true);
            }
        });
        getThread t1 = new getThread();
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            createSmokeAreaMarker(mapView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapView.addPOIItems(smokeMarkerlist.toArray(new MapPOIItem[smokeMarkerlist.size()]));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_notice) {

        } else if (id == R.id.nav_community) {
            Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
            //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MapActivity.this, ReviewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            onClickLogout();
        }

        drawer = findViewById(R.id.drawer_layout);//??
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);

    }

    private void onFinishReverseGeoCoding(String result) {
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float v) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        curlat = mapPointGeo.latitude;
        curlng = mapPointGeo.longitude;
        System.out.println(curlat + "ddd" + curlng);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        String[] arr = mapPOIItem.getItemName().split(",");
        Intent intent = new Intent(MapActivity.this, ReviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("arr",arr);
        startActivity(intent);
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View calloutBalloon;

        public CustomCalloutBalloonAdapter() {
            calloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);

        }


        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {

            String[] arr = poiItem.getItemName().split(",");
            System.out.println(arr[0] + "??" + arr[1] + "??" + arr[2] + "??" + arr[3] + "??" + arr[4] + "??" + arr[5] + "??" + arr[6]);
            ImageView imgicon = (ImageView) calloutBalloon.findViewById(R.id.badge);
            String urlStr = "http://18.222.175.17:8080/SmokingArea/img/" + arr[6]; // 웹서버에 프로필사진이 없을시 예외처리

            //   Drawable draw = loadDrawable(urlStr); // 웹서버에있는 사진을 안드로이드에 알맞게 가져온다.
            //  imgicon.setImageDrawable(draw);


            ((TextView) calloutBalloon.findViewById(R.id.title)).setText(arr[3]);
            ((TextView) calloutBalloon.findViewById(R.id.desc)).setText(arr[4]);
            ((TextView) calloutBalloon.findViewById(R.id.star)).setText(arr[5]);


            return calloutBalloon;
        }


        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

    }


    private void createSmokeAreaMarker(MapView mapView) throws JSONException {
        JSONArray ja = null;

        ja = new JSONArray(smokeareainfo);
        for (int i = 0; i < ja.length(); i++) {
            //
            SmokingArea smokingarea = new SmokingArea((JSONObject) ja.get(i));
            //

            smokeMarker = new MapPOIItem();

            //smokeMarker.setItemName((((JSONObject) (ja.get(i))).get("bench").toString()) + "," + (((JSONObject) (ja.get(i))).get("roof").toString()) + "," + (((JSONObject) (ja.get(i))).get("vtl").toString()) + "," + (((JSONObject) (ja.get(i))).get("name").toString()) + "," + (((JSONObject) (ja.get(i))).get("desc").toString()) + "," + (((JSONObject) (ja.get(i))).get("point").toString()) + "," + (((JSONObject) (ja.get(i))).get("no").toString()));
            smokeMarker.setItemName((((JSONObject) (ja.get(i))).get("bench").toString()) + "," + (((JSONObject) (ja.get(i))).get("roof").toString()) + "," + (((JSONObject) (ja.get(i))).get("vtl").toString()) + "," + (((JSONObject) (ja.get(i))).get("name").toString()) + "," + (((JSONObject) (ja.get(i))).get("desc").toString()) + "," + (((JSONObject) (ja.get(i))).get("point").toString()) + "," + (((JSONObject) (ja.get(i))).get("no").toString()));
            System.out.println("장소" + (((JSONObject) (ja.get(i))).get("reg_user").toString()));
            smokeMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            smokeMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            smokeMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(smokingarea.getSmokingAreaLat(), smokingarea.getSmokingAreaLng()));
            switch (smokingarea.getSmokinAreaType()) {
                case 1:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_brown);
                    break;
                case 2:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_red);
                    break;
                case 3:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_yellow);
                    break;
                case 4:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_blue);
                    break;
                case 5:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_green);
                    break;
                case 6:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_black);
                    break;

                default:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_white);
            }
            smokeMarker.setLeftSideButtonResourceIdOnCalloutBalloon(R.drawable.ic_menu_manage);
            smokeMarker.setLeftSideButtonResourceIdOnCalloutBalloon(3);
            smokeMarker.setCustomImageAutoscale(true);
            smokeMarkerlist.add(smokeMarker);
        }
    }

    public class minDistanceThread extends Thread {
        @Override
        public void run() {
            try {
                receiveMsg = getNearestSmokingArea();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class getThread extends Thread {
        @Override
        public void run() {
            try {
                String str;
                URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/smokingAreaList.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "Application/json");
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoInput(true);

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    smokeareainfo = buffer.toString();
                    System.out.println(smokeareainfo + "결과");
                } else {
                    System.out.println("에러 발생");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println("에러 발생");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("에러 발생");

            }
        }
    }

    public Drawable loadDrawable(String urlStr) { // 웹서버의 사진을 가져와서 Drawable로 만들어준다.
        Drawable drawable = null;

        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            drawable = Drawable.createFromStream(is, "none");
        } catch (Exception e) {
            // Log.e("LOG_TAG", "error, in loadDrawable \n" + e.toString());
        }

        return drawable;
    }

    public String getNearestSmokingArea() throws JSONException {
        String nearSmokingArea = "";
        System.out.println();
        JSONObject currentlocation = new JSONObject();
        currentlocation.put("lat", "" + curlat + "");
        currentlocation.put("lng", "" + curlng + "");
        System.out.println("test" + currentlocation + "dd");
        //현재 위치 데이터를 서버에 보내서 가까운 값 갖고오는거
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/minDistance.jsp");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");            //--------------------------
            //   서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            String currentlocationsend = "currentlocation=" + currentlocation.toString();

            buffer.append(currentlocationsend);                 // php 변수에 값 대입

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            //--------------------------
            //   서버에서 전송받기
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            nearSmokingArea = builder.toString();

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        System.out.println(nearSmokingArea + "data");
        return nearSmokingArea;
    } // HttpPostDat

    @Override
    protected void onResume() {
        super.onResume();
        mapView.removeAllPOIItems();
        getThread t1 = new getThread();
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            createSmokeAreaMarker(mapView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapView.addPOIItems(smokeMarkerlist.toArray(new MapPOIItem[smokeMarkerlist.size()]));

    }

    private void onClickLogout() {//로그아웃인데..왜
        sp = getSharedPreferences("profile", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
        sp.edit().clear().apply();
        Toast.makeText(MapActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
        finish();
    }

}