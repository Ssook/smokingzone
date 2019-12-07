package com.example.zone;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BoardWriteActivity extends AppCompatActivity {

    //빠담 글쓰기 화면 view 변수들
    private TextView tv_outPut;
    private EditText et_title;
    private EditText et_content;
    private CheckBox cb_anony;

    //현재 유저 닉네임 받아오는 변수들
    SharedPreferences sp;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        //----------------------------
        /*        액션바 설정 부분    */
        //----------------------------

        //액션바 가져오기
        ActionBar actionBar = getSupportActionBar();

        //액션바 커스텀 뷰 설정
        actionBar.setCustomView(R.layout.custom_bar_write);
        actionBar.setTitle("빠담 글쓰기");

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 컴포넌트 값 받아오기
       // tv_outPut = (TextView) findViewById(R.id.tv_outPut);
        et_title = (EditText) findViewById(R.id.titletext);
        et_content = (EditText) findViewById(R.id.contenttext);
        cb_anony = (CheckBox) findViewById(R.id.anonycheck);

        //사용자 닉네임 받아오기
        sp = getSharedPreferences("profile", MODE_PRIVATE);
        name = sp.getString("name", "");

    }//onCreate func()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //빠담 글쓰기 화면 메뉴 설정
        getMenuInflater().inflate(R.menu.menu_board_write, menu);
        return true;
    }//onCreateOptionsMenu func

    //메뉴 아이템을 눌렀을 때 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //---------------------------------
        /*  새글 등록 버튼 눌렀을 때 작업   */
        //---------------------------------
        if (id == R.id.newPost) {
            // URL 설정.
            String url = "http://18.222.175.17:8080/SmokingArea/Board/insertBoard.jsp";

            //JSONObject에 서버로 보낼 게시글 정보를 담음
            JSONObject board_data = new JSONObject();

            //-----------------------------
            /* 서버에 게시글 정보를 보냄 Part*/
            //-----------------------------

            if (cb_anony.isChecked()) {
                try {
                    board_data.put("reg_user", "익명");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//익명 처리
            else if (!cb_anony.isChecked()) {
                try {
                    board_data.put("reg_user", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//익명 아닐 시
            try {
                board_data.put("ctnt", et_content.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                board_data.put("tag", "전체");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                board_data.put("title", et_title.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //--------------------------------
            /* 게시글 정보를 서버에 보냄   Part*/
            //--------------------------------
            NetworkTask networkTask = new NetworkTask(board_data.toString());
            networkTask.execute();

            //--------------------------------
            /* 액티비티 전환 게시글 최신화 Part*/
            //--------------------------------

            //액티비티 간 전환
            Intent intent = new Intent(getApplicationContext(), BoardActivity.class); //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
            //게시판 액티비티 활성화
            startActivity(intent);
            Toast.makeText(BoardWriteActivity.this, "게시글 등록 성공", Toast.LENGTH_SHORT).show();

            return true;

        } // 게시글 등록 버튼(new Post)눌렀을시 if문
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected func()

    //--------------------------------
    /* 게시글 정보를 서버에 보내는 Class*/
    //--------------------------------
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String values;

        NetworkTask(String values) {
            this.values = values;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }//실행 이전 작업 정의 함수

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                //서버에 게시글 정보를 입력하는 함수 호출
                result = sendBoardWrite(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        } // 백그라운드 작업 함수

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
        }
    }

    //----------------------------------
    /* 서버에 게시글 정보를 입력하는 함수 */
    //----------------------------------
    public String sendBoardWrite(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------

            URL url = new URL("http://18.222.175.17:8080/SmokingArea/Board/insertBoard.jsp");
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
            String regdata = "board_param=" + values;
            Log.d("board_data", values);
            buffer.append(regdata);                 // php 변수에 값 대입

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
            result = builder.toString();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        System.out.println(result);
        return result;
    } // HttpPostDat
}


