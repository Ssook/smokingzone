package com.example.zone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zone.R;

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
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {
    private RatingBar ratingbar;
    private TextView ratingValue;
    private EditText ed_review_comment;
    private Button bt_reg_rating;
    private Button bt_reg_comment;
    private ActionBar actionBar;

    private JSONArray mArray;  //서버로부터 JSON Array를 받아 저장할 변수
    ListView listView; //게시판 ListView 레이아웃 형성을 위한 객체 생성
    ReviewListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    //흡연구역데이터 배열 선언
    String smoking_area_data[];

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();

    ArrayList<ReviewModel> arrayList = new ArrayList<ReviewModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        //액션바 가져오기
        actionBar = getSupportActionBar();
        actionBar.setTitle("리뷰 화면");

        //액션바 타이틀 가운데 정렬
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_bar);

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        //흡연구역 정보를 intent를 통해 받음
        Intent intent = getIntent();
        smoking_area_data =intent.getExtras().getStringArray("arr");


        ratingbar = findViewById(R.id.ratingbar);
        ratingValue = findViewById(R.id.ratingvalue);
        ed_review_comment = findViewById((R.id.edit_review_comment));
        bt_reg_rating = findViewById((R.id.ratingregbutton));
        bt_reg_comment = findViewById((R.id.comment_reg_button));


        bt_reg_comment.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // URL 설정.
                String url = "http://172.20.10.8:8080/SmokingArea/Board/insertBoard.jsp";

                //서버로 보낼 데이터를 ContentValues에 담아줌
                ContentValues values = new ContentValues();

                values.put("smoking_area_no", "1");
                values.put("smoking_review_reg_user", "주용이");
                values.put("smoking_review_ctnt", ed_review_comment.getText().toString());
                values.put("smoking_review_point", ratingValue.getText().toString());

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
                        key = "\"" + parameter.getKey() + "\"";
                        value = "\"" + parameter.getValue().toString() + "\"";

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


                NetworkTaskWrite networkTask = new NetworkTaskWrite(sbParams.toString());
                networkTask.execute();


            }
        });

        // String url = "http://18.222.175.17:8080/SmokingArea/Board/boardList.jsp";
        ReviewActivity.NetworkTask networkTask = new ReviewActivity.NetworkTask(this, null);
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
        String Test = "[{\"smoking_area_no\":\"1\",\"smoking_review_reg_user\":\"user\",\"smoking_review_ctnt\":\"ctntsadasdasdsada\",\"smoking_review_point\":\"15.5\"}]";

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
            ReviewModel reviewModel = new ReviewModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
            //bind all strings in an array
            arrayList.add(reviewModel);
        }

        //listViewAdapter클래스에 결과를 넘겨줌
        adapter = new ReviewListViewAdapter(this, arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);


        ratingbar.setOnRatingBarChangeListener(new RatingbarListener());
    }

    class RatingbarListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            ratingbar.setRating(rating);
            ratingValue.setText(Float.toString(ratingbar.getRating()));
            ratingValue.setTypeface(null, Typeface.BOLD);


        }
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
            String Test = "[{\"smoking_area_no\":\"1\",\"smoking_review_reg_user\":\"user\",\"smoking_review_ctnt\":\"ctntsadasdasdsada\",\"smoking_review_point\":\"15.5\"}]";
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
                    ReviewModel reviewModel = new ReviewModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
                    //bind all strings in an array
                    arrayList.add(reviewModel);
                }

                //listViewAdapter클래스에 결과를 넘겨줌
                adapter = new ReviewListViewAdapter(mcontext, arrayList);

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
                result = sendBoardWrite(values);
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

    public String sendBoardWrite(String values) throws JSONException {

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
            String regdata = "json_smokingReviewValue=" + values;
            Log.d("data", values);
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

