package com.example.zone;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    private TextView tv_outPut;
    private EditText et_title;
    private EditText et_content;
    private CheckBox cb_anony;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        //액션바 가져오기
        ActionBar actionBar = getSupportActionBar();

        //액션바 타이틀 가운데 정렬
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_bar_write);

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 컴포넌트 값 받아오기
        tv_outPut = (TextView) findViewById(R.id.tv_outPut);
        et_title = (EditText) findViewById(R.id.titletext);
        et_content = (EditText) findViewById(R.id.contenttext);
        cb_anony = (CheckBox) findViewById(R.id.anonycheck);

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_board_write, menu);

        return true;

    }
    //메뉴 아이템을 눌렀을 때 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //새 글 등록 메뉴를 클릭했을 때 이벤트
        if (id == R.id.newPost) {
            // URL 설정.
            String url = "http://172.20.10.8:8080/SmokingArea/Board/insertBoard.jsp";

            //서버로 보낼 데이터를 ContentValues에 담아줌
            ContentValues values = new ContentValues();

            // 현재시간을 msec 으로 구한다.
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);

            values.put("reg_date",formatDate);
            if(cb_anony.isChecked())
            {
                values.put("reg_user","익명");
            }
            values.put("ctnt",et_content.getText().toString());
            values.put("tag","전체");
            values.put("title",et_title.getText().toString());

            // URL 뒤에 붙여서 보낼 파라미터.
            StringBuffer sbParams = new StringBuffer();

            /**
             * 1. StringBuffer에 파라미터 연결
             * */
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (values == null)
                sbParams.append("");
                // 보낼 데이터가 있으면 파라미터를 채운다.
            else {
                // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
                boolean isAnd = false;
                // 파라미터 키와 값.
                String key;
                String value;
                sbParams.append("{");
                for (Map.Entry<String, Object> parameter : values.valueSet()) {
                    key = "\""+parameter.getKey()+"\"";
                    value = "\""+parameter.getValue().toString()+"\"";

                    // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                    if (isAnd)
                        sbParams.append(",");

                    sbParams.append(key).append(":").append(value);

                    // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                    if (!isAnd)
                        if (values.size() >= 2)
                            isAnd = true;
                }
                sbParams.append("}");
            }


            NetworkTask networkTask = new NetworkTask(sbParams.toString());
            networkTask.execute();



            //액티비티 간 전환
            //  Intent intent = new Intent(getApplicationContext(),MainActivity.class); //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
            // startActivity(intent);
            //Toast.makeText(BoardWriteActivity.this, "게시글 등록 성공", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {


        String values;

        NetworkTask( String values){

            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result="";

            try {
                result =  sendBoardWrite(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            tv_outPut.setText(result);
        }
    }
    public String sendBoardWrite(String values) throws JSONException {

        String result="";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------

            URL url = new URL("http://172.16.25.91:8080/SmokingArea/Board/insertBoard.jsp");
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
            String regdata="board_param="+values;
            Log.d("data",values);
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
        System.out.println( result);
        return  result;
    } // HttpPostDat
}


