package com.example.zone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BoardDetailActivity extends AppCompatActivity {

    private TextView mDetailTv;
    private EditText ed_review_comment;
    private Button bt_reg_comment;
    private String board_no;

    private JSONArray mArray;  //서버로부터 JSON Array를 받아 저장할 변수
    ListView listView; //게시판 ListView 레이아웃 형성을 위한 객체 생성
    BoardCommentListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayBoardNo = new ArrayList<String>();
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();

    ArrayList<BoardCommentModel> arrayList = new ArrayList<BoardCommentModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_bar_detail);
        mDetailTv = findViewById(R.id.textView);
        bt_reg_comment = findViewById((R.id.comment_reg_button));
        ed_review_comment = findViewById((R.id.edit_review_comment));


        //get data from previous activity when item of listview is clicked using intent
        Intent intent = getIntent();
        String mActionBarTitle = intent.getStringExtra("actionBarTitle");
        String mContent = intent.getStringExtra("contentTv");
        board_no = intent.getStringExtra("board_no");

        //set actionbar title
        actionBar.setTitle(mActionBarTitle);
        //set text in textview
        mDetailTv.setText(mContent);

        bt_reg_comment.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                //JSONObject에 서버에 보내줄 댓글 데이터 담아줌
                JSONObject sbParam = new JSONObject();
                try {
                    sbParam.put("board_no", "" + board_no + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sbParam.put("board_comment_reg_user", "주용이승연이은석이!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sbParam.put("board_comment_ctnt", ed_review_comment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("data",sbParam.toString());
                NetworkTaskWrite networkTask = new NetworkTaskWrite(sbParam.toString());
                networkTask.execute();


            }
        });

        // String url = "http://18.222.175.17:8080/SmokingArea/Board/boardList.jsp";
        BoardDetailActivity.NetworkTask networkTask = new BoardDetailActivity.NetworkTask(this, null);
        networkTask.execute();

        //더미 배열 생성
        String[] dummyuser = new String[]{"박지성", "손흥민", "황희찬", "이강인", "남태희"};
        String[] dummydate = new String[]{"2019/09/30", "2019/00/00", "2019/00/00", "2019/00/00", "2019/00/00"};
        String[] dummyctnt = new String[]{"Battery detail...", "Cpu detail...", "Display detail...", "Memory detail...", "Sensor detail..."};

        //더미 데이터 입력
        for (int i = 0; i < 5; i++) {
            arrayregUser.add(dummyuser[i]);
            arrayregDate.add(dummydate[i]);
            arrayctnt.add(dummyctnt[i]);
        }


        //더미 데이터 입력 (Json Parsing)
        String Test = "[{\"smoking_area_no\":\"1\",\"smoking_review_reg_user\":\"user\",\"smoking_review_ctnt\":\"ctntsadasdasdsada\"}]";

        try {
            mArray = new JSONArray(Test);
        } catch (JSONException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < mArray.length(); i++) {
            try {
                JSONObject jsonObject = mArray.getJSONObject(i);
                // Pulling items from the array
                arrayregDate.add(jsonObject.getString("reg_date"));
                arrayregUser.add(jsonObject.getString("reg_user"));
                arrayctnt.add(jsonObject.getString("ctnt"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //listView에 xml의 listView를 가져와서 넣어줌
        listView = findViewById(R.id.listView);

        //Model에 데이터를 넣어주고 arrayList<BoardModel>에 넣어줌
        for (int i = 0; i < arrayregUser.size(); i++) {
            BoardCommentModel boardcommentModel = new BoardCommentModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
            //bind all strings in an array
            arrayList.add(boardcommentModel);
        }

        //listViewAdapter클래스에 결과를 넘겨줌
        adapter = new BoardCommentListViewAdapter(this, arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);


    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {


        ContentValues values;
        Context mcontext;

        NetworkTask(Context mcontext, ContentValues values) {
            this.mcontext = mcontext;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                result = ServerReviewData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
            //더미 데이터 입력 (Json Parsing)
            String Test = "[{\"smoking_area_no\":\"1\",\"smoking_review_reg_user\":\"user\",\"smoking_review_ctnt\":\"ctntsadasdasdsada\"}]";
            if (result != "") {
                try {
                    mArray = new JSONArray(result);
                } catch (JSONException e) {
                    //TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for (int i = 0; i < mArray.length(); i++) {
                    try {
                        JSONObject jsonObject = mArray.getJSONObject(i);
                        // Pulling items from the array
                        arrayregDate.add(jsonObject.getString("reg_date"));
                        arrayregUser.add(jsonObject.getString("reg_user"));
                        arrayctnt.add(jsonObject.getString("ctnt"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //listView에 xml의 listView를 가져와서 넣어줌
                listView = findViewById(R.id.listView);

                //Model에 데이터를 넣어주고 arrayList<BoardModel>에 넣어줌
                for (int i = 0; i < arrayregUser.size(); i++) {
                    BoardCommentModel boardCommentModel = new BoardCommentModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
                    //bind all strings in an array
                    arrayList.add(boardCommentModel);
                }

                //listViewAdapter클래스에 결과를 넘겨줌
                adapter = new BoardCommentListViewAdapter(mcontext, arrayList);

                //bind the adapter to the listview
                listView.setAdapter(adapter);
            }//result not null
            else {
                Log.d("data:", "게시글 없음!");
            }
        }
    }

    public String ServerReviewData() throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/Board/boardList.jsp");
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
            //StringBuffer buffer = new StringBuffer();
            //String currentlocationsend="board_param="+values;

            // buffer.append(currentlocationsend);                 // php 변수에 값 대입

            //OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            //PrintWriter writer = new PrintWriter(outStream);
            //writer.write(buffer.toString());
            //writer.flush();

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

    public class NetworkTaskWrite extends AsyncTask<Void, Void, String> {


        String values;

        NetworkTaskWrite(String values) {

            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                result = sendReviewWrite(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.

        }
    }

    public String sendReviewWrite(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------

            URL url = new URL("http://172.16.25.91:8080/SmokingArea/SmokingArea/insertSmokingReview.jsp");
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
            String regdata = "json_BoardCommentValue=" + values;

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


