package com.example.zone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.zone.runtimePermissions.AppPermissionHelper.REQUEST_CODE;

public class AddSmokingAreaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView imageView;
    double curlat, curlng;
    EditText areaDesc;
    EditText areaName;
    Toolbar toolbar;
    CheckBox check_inside;
    CheckBox check_aircondition;
    CheckBox check_loop;
    CheckBox check_bench;
    RadioGroup area_type;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    Button btnadd;

    SharedPreferences sp;
    RadioButton rb_cafe, rb_food, rb_school, rb_company, rb_street, rb_other;
    RadioGroup rg_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        curlat = getIntent().getDoubleExtra("curlat", 0.0);
        curlng = getIntent().getDoubleExtra("curlng", 0.0);
        sp = getSharedPreferences("profile", MODE_PRIVATE);
    }

    public class networkThread extends Thread {
        @Override
        public void run() {
            String response = "";
            try {
                //--------------------------
                //   URL 설정하고 접속하기
                //--------------------------
                URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/insertSmokingArea.jsp");
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
                String json_smokingAreaValue = "json_smokingAreaValue=";
                makeJsonObject();

                buffer.append(json_smokingAreaValue);

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
                response = builder.toString();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
            System.out.println(response + "data");
        }
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
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notice) {

        } else if (id == R.id.nav_community) {
            Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void addImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    imageView.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkNull(String smokingareaInfomation) {
        if (smokingareaInfomation.equals("") || smokingareaInfomation == null) {
            return false;
        } else return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    public String getCurrentTime() {
        // 현재 시스템 시간 구하기
        long systemTime = System.currentTimeMillis();

// 출력 형태를 위한 formmater
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);

// format에 맞게 출력하기 위한 문자열 변환
        String currentTime = formatter.format(systemTime);
        return currentTime;
    }

    public JSONObject makeJsonObject() {
        boolean format_check = check_Format();
        JSONObject smokingareainfo=new JSONObject();
        if (format_check == false) {
            return null;
        } else if (format_check == true) {
            try {
                smokingareainfo.put("smoking_area_name", areaName.getText().toString());
                smokingareainfo.put("smoking_area_lat", "" + curlat + "");
                smokingareainfo.put("smoking_area_lng", "" + curlng + "");
                smokingareainfo.put("smoking_area_reg_date", "" + getCurrentTime() + "");
                smokingareainfo.put("smoking_area_reg_user", sp.getString("name", ""));
                smokingareainfo.put("smoking_area_point", "0");
                smokingareainfo.put("smoking_area_report", "0");
                smokingareainfo.put("smoking_area_roof", "" + checkboxresult(check_loop) + "");
                smokingareainfo.put("smoking_area_vtl", "" + checkboxresult(check_aircondition) + "");
                smokingareainfo.put("smoking_area_bench", "" + checkboxresult(check_bench) + "");
                smokingareainfo.put("smoking_area_desc", areaDesc.getText().toString());
                smokingareainfo.put("smoking_area_type", getRadioGroup());
                System.out.println(smokingareainfo + "eldyd");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return smokingareainfo;

    }

    public int checkboxresult(CheckBox chk) {
        if (chk.isChecked()) {
            return 1;
        } else return 0;
    }

    public void show(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(message);
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    public void initLayout() {
        setContentView(R.layout.activity_add_smoking_area);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        areaDesc = findViewById(R.id.smokingareadesc);
        areaName = findViewById(R.id.smokingareaname);
        check_inside = findViewById(R.id.check_inside);
        check_aircondition = findViewById(R.id.check_aircondition);
        check_loop = findViewById(R.id.check_loop);
        check_bench = findViewById(R.id.check_bench);
        area_type = findViewById(R.id.radioGroup);

        btnadd = findViewById(R.id.btnadd);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            //이거 누를 때 서버로 데이터를 전송
//                sendDataToServer();
                if (!checkNull(areaName.getText().toString())) {
                    Snackbar.make(view, "흡연 장소의 이름을 입력해주세요.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;

                } else {
                    networkThread t1 = new networkThread();
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //이미지
        imageView = (ImageView) findViewById(R.id.areaimage);

        //radio
        rb_cafe = (RadioButton) findViewById(R.id.rb_cafe);
        rb_food = (RadioButton) findViewById(R.id.rb_food);
        rb_school = (RadioButton) findViewById(R.id.rb_school);
        rb_company = (RadioButton) findViewById(R.id.rb_company);
        rb_street = (RadioButton) findViewById(R.id.rb_street);
        rb_other = (RadioButton) findViewById(R.id.rb_other);

        rb_cafe.setOnClickListener(radioButtonClickListener);
        rb_food.setOnClickListener(radioButtonClickListener);
        rb_school.setOnClickListener(radioButtonClickListener);
        rb_company.setOnClickListener(radioButtonClickListener);
        rb_street.setOnClickListener(radioButtonClickListener);
        rb_other.setOnClickListener(radioButtonClickListener);


        rg_type = findViewById(R.id.radioGroup);
        rg_type.setOnCheckedChangeListener(radioGroupButtonChangeListener);


    }

    //라디오 버튼 클릭 리스너
    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.rb_cafe) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼1 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.rb_food) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.rb_school) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.rb_company) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.rb_street) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.rb_other) {
                Toast.makeText(AddSmokingAreaActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public int getRadioGroup() {
        int check_type;
        check_type = ((RadioGroup) rg_type.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (check_type) {
            case R.id.rb_cafe:
                return 1;
            case R.id.rb_food:
                return 2;
            case R.id.rb_school:
                return 3;
            case R.id.rb_company:
                return 4;
            case R.id.rb_street:
                return 5;
            case R.id.rb_other:
                return 6;
            default:
                return 0;
        }
    }

    public boolean check_Format() {
        if (areaName.getText().toString().equals("")) {
            System.out.println("흡연장소명을 입력하여주세요");
            return false;
        } else if (getRadioGroup() == 0) {
            System.out.println("흡연장소의 유형을 입력하여주세요");
            return false;
        }
        return true;
    }

}
