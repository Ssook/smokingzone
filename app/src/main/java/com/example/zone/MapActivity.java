package com.example.zone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.helper.log.Logger;

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


import java.io.BufferedInputStream;
import java.net.URLConnection;
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
import java.util.Arrays;

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
    FloatingActionButton filter;
    ImageView profile;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ViewGroup mapViewContainer;
    ArrayList<MapPOIItem> cafe_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> food_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> school_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> company_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> street_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> other_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> banned_SmokeMarkerList = new ArrayList<MapPOIItem>();
    ArrayList<SmokeMarker> all_SmokeMarkerList = new ArrayList<SmokeMarker>();

    boolean[] isCheck;
    private String img_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sp = getSharedPreferences("profile", Activity.MODE_PRIVATE);

        center = mapPointWithGeoCoord(curlat, curlng);
        initLayoutMapActivity();


        GetSmokingAreaThread getSmokingAreaThread = new GetSmokingAreaThread();
        getSmokingAreaThread.start();
        try {
            getSmokingAreaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isCheck = new boolean[SmokeHereConstants.TYPECOUNT];
        Arrays.fill(isCheck, true);

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

    //----------------------------------
    /*마커의 말풍선을 클릭했을 때 이벤트 */
    //----------------------------------

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        //--------------------------------------
        /* 흡연구역 정보를 리뷰 액티비티에 넘겨줌 */
        //--------------------------------------
        Log.d("touch", mapView.toString());
        Log.d("touch", mapPOIItem.toString());
        Log.d("touch", calloutBalloonButtonType.toString());
        String[] arr = mapPOIItem.getItemName().split(",");
        Intent intent = new Intent(MapActivity.this, ReviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("arr", arr);
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
            img_url = arr[7];
            System.out.println(arr[0] + "??" + arr[1] + "??" + arr[2] + "??" + arr[3] + "??" + arr[4] + "??" + arr[5] + "??" + arr[6] + "??" + arr[7]);
//            final ImageView imgicon = (ImageView) calloutBalloon.findViewById(R.id.badge);
//            imgicon.setImageResource(R.drawable.defaultimg);
//            final String urlStr = "http://18.222.175.17:8080/SmokingArea/img/" + arr[7] + ".jpg"; // 웹서버에 프로필사진이 없을시 예외처리

//
//            if (img_url!=null||!img_url.equals(null)) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.with(MapActivity.this).load("http://18.222.175.17:8080/SmokingArea/img/"+img_url+".jpg").into(imgicon);
//                    }
//                });
//            }
//
////            if (arr[7]!=null) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        try{
////                        Glide.with(MapActivity.this).load(urlStr).into(imgicon);}
////                        catch (Exception e){
////                            imgicon.setImageResource(R.drawable.defaultimg);
////                        }
////                    }
////                });
////            }
//
////            if(arr[7]!=null) {
////                Drawable draw = loadDrawable(urlStr); // 웹서버에있는 사진을 안드로이드에 알맞게 가져온다.
////                if (draw != null) {
////                    System.out.println("dlrjwl");
////                    imgicon.setImageDrawable(draw);
////                    System.out.println("dlrjwl2");
////                }
////            }
//            if (arr[7]==null||arr[7].isEmpty()||arr[7].equals(null)){
//                System.out.println("sjfdlsep");
//                imgicon.setImageResource(R.drawable.defaultimg);
//            }
//


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

    public MapPOIItem setSmokeMarker(SmokingArea smokingarea){
        smokeMarker.setItemName(smokingarea.getSmokingAreaBench()
                + "," + smokingarea.getSmokingAreaRoof()
                + "," + smokingarea.getSmokingAreaAircondition()
                + "," + smokingarea.getSmokingAreaName()
                + "," + smokingarea.getSmokingAreaDesc()
                + "," + Math.round(smokingarea.getSmokingAreaPoint() * 100) / 100.0
                + "," + smokingarea.getSmokinAreaNo()
                + "," + smokingarea.getSmokingAreaImgUrl()
                + "," + smokingarea.getSmokingAreaType());
        System.out.println("장소" + smokingarea.getSmokingAreaRegUser());
        smokeMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        smokeMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        smokeMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(smokingarea.getSmokingAreaLat(), smokingarea.getSmokingAreaLng()));

        return smokeMarker;
    }

    //받아온 데이터를 통해 마커들을 생성하는 메소드
    private void createSmokeAreaMarker(MapView mapView) throws JSONException {
        JSONArray smokingAreaData = null;
        smokingAreaData = new JSONArray(smokeareainfo);

        for (int i = 0; i < smokingAreaData.length(); i++) {
            smokeMarker = new MapPOIItem();                 //이걸 반복문에서 빼니까 하나밖에 안나옴
            SmokingArea smokingarea = new SmokingArea((JSONObject) smokingAreaData.get(i));

            smokeMarker = setSmokeMarker(smokingarea);
            SmokeMarker data_SmokeMarker=new SmokeMarker(smokeMarker,smokingarea.getSmokingAreaType());

            //smokeMarker.setItemName((((JSONObject) (ja.get(i))).get("bench").toString()) + "," + (((JSONObject) (ja.get(i))).get("roof").toString()) + "," + (((JSONObject) (ja.get(i))).get("vtl").toString()) + "," + (((JSONObject) (ja.get(i))).get("name").toString()) + "," + (((JSONObject) (ja.get(i))).get("desc").toString()) + "," + (((JSONObject) (ja.get(i))).get("point").toString()) + "," + (((JSONObject) (ja.get(i))).get("no").toString()));
            all_SmokeMarkerList.add(data_SmokeMarker);
            switch (smokingarea.getSmokingAreaType()) {
                case SmokeHereConstants.CAFE:
                    smokeMarker.setCustomImageResourceId(R.drawable.cafe);
                    cafe_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.FOOD:
                    smokeMarker.setCustomImageResourceId(R.drawable.food);
                    food_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.SCHOOL:
                    smokeMarker.setCustomImageResourceId(R.drawable.school);
                    school_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.COMPANY:
                    smokeMarker.setCustomImageResourceId(R.drawable.company);
                    company_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.STREET:
                    smokeMarker.setCustomImageResourceId(R.drawable.street);
                    street_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.OTHER:
                    smokeMarker.setCustomImageResourceId(R.drawable.other);
                    other_SmokeMarkerList.add(smokeMarker);
                    break;
                case SmokeHereConstants.BANNED:
                    smokeMarker.setCustomImageResourceId(R.drawable.map_pin_black);
                    banned_SmokeMarkerList.add(smokeMarker);
                    break;
                default:
                    smokeMarker.setCustomImageResourceId(R.drawable.custom_poi_marker);
            }
            smokeMarker.setLeftSideButtonResourceIdOnCalloutBalloon(R.drawable.ic_menu_manage);
            smokeMarker.setLeftSideButtonResourceIdOnCalloutBalloon(3);
            smokeMarker.setCustomImageAutoscale(true);
            smokeMarkerlist.add(smokeMarker);
        }
    }

    public class GetNearSmokingAreaThread extends Thread {
        @Override
        public void run() {
            try {
                receiveMsg = getNearestSmokingArea();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetSmokingAreaThread extends Thread {
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
            System.out.println("durltjdpfj");
            return null;
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

        GetSmokingAreaThread getSmokingAreaThread = new GetSmokingAreaThread();
        getSmokingAreaThread.start();

        try {
            getSmokingAreaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            createSmokeAreaMarker(mapView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (smokeMarker == null) {
            System.out.println("wndyd");
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

    ///////////////////////
    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ArrayList<String> selectedItems = new ArrayList<String>();
        final String[] items = getResources().getStringArray(R.array.filter_list);

        builder.setTitle("흡연 장소 필터링");
        builder.setMultiChoiceItems(R.array.filter_list, isCheck, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos, boolean isChecked) {
                if (isChecked == false) // Checked 상태일 때 추가
                {
                        for (int i=0; i<all_SmokeMarkerList.size();i++){
                            if(all_SmokeMarkerList.get(i).getSmokingAreaType()==pos) {
                                all_SmokeMarkerList.get(i).getSmokeMarker().setAlpha(0.1f);
                            }

//                        case SmokeHereConstants.CAFE:
//                            for (int b = 0; b < cafe_SmokeMarkerList.size(); b++) {
//                                cafe_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[0] = false;
//                            break;
//                        case SmokeHereConstants.FOOD:
//                            for (int b = 0; b < food_SmokeMarkerList.size(); b++) {
//                                food_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//
//                            isCheck[1] = false;
//                            break;
//                        case SmokeHereConstants.SCHOOL:
//                            for (int b = 0; b < school_SmokeMarkerList.size(); b++) {
//                                school_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[2] = false;
//                            break;
//                        case SmokeHereConstants.COMPANY:
//                            for (int b = 0; b < company_SmokeMarkerList.size(); b++) {
//                                company_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[3] = false;
//                            break;
//                        case SmokeHereConstants.STREET:
//                            for (int b = 0; b < street_SmokeMarkerList.size(); b++) {
//                                street_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[4] = false;
//                            break;
//                        case SmokeHereConstants.OTHER:
//                            for (int b = 0; b < other_SmokeMarkerList.size(); b++) {
//                                other_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[5] = false;
//                            break;
//                        case SmokeHereConstants.BANNED:
//                            for (int b = 0; b < banned_SmokeMarkerList.size(); b++) {
//                                banned_SmokeMarkerList.get(b).setAlpha(0.1f);
//                            }
//                            isCheck[6] = false;
//                            break;
                    }
                } else                  // Check 해제 되었을 때 제거
                {
                    for (int i=0; i<all_SmokeMarkerList.size();i++){
                        if(all_SmokeMarkerList.get(i).getSmokingAreaType()==pos) {
                            all_SmokeMarkerList.get(i).getSmokeMarker().setAlpha(1.0f);
                        }

//                    switch (pos) {
//                        case SmokeHereConstants.CAFE:
//                            for (int b = 0; b < cafe_SmokeMarkerList.size(); b++) {
//                                cafe_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[SmokeHereConstants.CAFE] = true;
//                            break;
//                        case SmokeHereConstants.FOOD:
//                            for (int b = 0; b < food_SmokeMarkerList.size(); b++) {
//                                food_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[1] = true;
//                            break;
//                        case SmokeHereConstants.SCHOOL:
//                            for (int b = 0; b < school_SmokeMarkerList.size(); b++) {
//                                school_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[2] = true;
//                            break;
//                        case SmokeHereConstants.COMPANY:
//                            for (int b = 0; b < company_SmokeMarkerList.size(); b++) {
//                                company_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[3] = true;
//                            break;
//                        case SmokeHereConstants.STREET:
//                            for (int b = 0; b < street_SmokeMarkerList.size(); b++) {
//                                street_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[4] = true;
//                            break;
//                        case SmokeHereConstants.OTHER:
//                            for (int b = 0; b < other_SmokeMarkerList.size(); b++) {
//                                other_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[5] = true;
//                            break;
//                        case SmokeHereConstants.BANNED:
//                            for (int b = 0; b < banned_SmokeMarkerList.size(); b++) {
//                                banned_SmokeMarkerList.get(b).setAlpha(1.0f);
//                            }
//                            isCheck[6] = true;
//                            break;
                    }
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void filterMarker(int type, float transparent) {
        for (int i = 0; i < smokeMarkerlist.size(); i++) {
            smokeMarkerlist.get(i).getItemName().split(",")[7] ="0" ;
            smokeMarkerlist.get(i).setAlpha(transparent);
        }
    }

    public void initLayoutMapActivity() {           //레이아웃 정의
        setContentView(R.layout.activity_map);
        setView_MapView();
        setView_Toolbar();
        setView_BtnAddArea();
        setView_BtnRoadNavi();
        setView_NavHeader();
        setView_Drawer();
        setView_BtnTrack();
        setView_Profile();
    }

    private void setView_Drawer() {
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setView_NavHeader() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//View nav_header_view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        nav_header_view = navigationView.getHeaderView(0);
        nav_header_id_text = (TextView) nav_header_view.findViewById(R.id.user_name);
//        Intent intent = getIntent();
//        System.out.println(intent.getStringExtra("user_name") + "test");

        nav_header_id_text.setText(sp.getString("name", ""));

    }

    private void setView_Toolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("여기서펴");
        toolbar.setTitleMargin(5, 0, 5, 0);
    }

    private void setView_BtnAddArea() {
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
    }

    private void setView_BtnTrack() {
        track = findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                center = mapPointWithGeoCoord(curlat, curlng);
                mapView.setMapCenterPointAndZoomLevel(center, 0, true);
            }
        });
    }

    private void setView_BtnRoadNavi() {
        roadnavi = findViewById(R.id.roadnavi);
        roadnavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //길찾기 버튼 눌렀을 때
                String strlat = "";
                String strlng = "";

                GetNearSmokingAreaThread getNearSmokingAreaThread = new GetNearSmokingAreaThread();
                getNearSmokingAreaThread.start();

                try {
                    getNearSmokingAreaThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject nearSmokingAreaInfo = new JSONObject(receiveMsg);
                    strlat = nearSmokingAreaInfo.getString("smoking_area_lat");
                    strlng = nearSmokingAreaInfo.getString("smoking_area_lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String nearSmokingAreaInfoUrl = "daummaps://route?sp=" + curlat + "," + curlng + "&ep=" + strlat + "," + strlng + "&by=FOOT";//여기에 좌표값 넣어주면 됨
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(nearSmokingAreaInfoUrl));
                startActivity(intent);
            }
        });
    }

    private void setView_MapView() {
        mapView = new MapView(this);
        mapView.setDaumMapApiKey("dccc7c0ddbd4beddfdaf5655ef4463ce");
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapCenterPointAndZoomLevel(center, 0, true);
    }

    private void setView_Profile() {
        profile = nav_header_view.findViewById(R.id.profileimage);

        String urlStr;

        urlStr = sp.getString("image_url", "");
        System.out.println("dhkt" + urlStr);
        new Thread() {
            public void run() {
                try {
                    String urlStr = sp.getString("image_url", "");
                    URL url = new URL(urlStr);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    final Bitmap bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    if (bm == null) {
                        System.out.println("what");
                    }
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            if (bm != null) {
                                profile.setImageBitmap(bm);
                            } else return;

                        }
                    }, 0);
                } catch (IOException e) {
                    Logger.e("Androes", " " + e);
                }
            }
        }.start();
    }
}