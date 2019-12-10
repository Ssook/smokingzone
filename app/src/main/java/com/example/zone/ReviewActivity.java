package com.example.zone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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

public class ReviewActivity extends AppCompatActivity  implements KeyboardHeightProvider.KeyboardHeightObserver{

    private KeyboardHeightProvider keyboardHeightProvider;


    private ViewGroup editTextLinearLayoutView;
    private float initialY;

    private TextView smokingarea_name_TV;
    private TextView smokingarea_avg_star_point_TV;
    private CheckBox bench_CB;
    private CheckBox roof_CB;
    private CheckBox vtl_CB;
    private RatingBar star_point_RB;
    private TextView star_point_TV;
    private EditText review_comment_ET;
    private Button reg_comment_Btn;
    private ImageView smokingarea_image_IV;
    private String img_url;
    private JSONArray jsonArray;  //서버로부터 JSON Array를 받아 저장할 변수
    private ActionBar actionBar;
    private ImageButton report_IBtn;
    private TextView report_ctn_TV;
    private String report_result;
    ListView listView; //리뷰화면 댓글  ListView 레이아웃 형성을 위한 객체 생성
    ReviewListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    //흡연구역데이터 배열 선언
    String smoking_area_data[];

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();
    ArrayList<ReviewModel> arrayList = new ArrayList<ReviewModel>();

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("profile", Activity.MODE_PRIVATE);
        setContentView(R.layout.activity_review);
        //editText 키보드 팝업 설정
        setView_editText_KeyBoardView();
        //----------------------------
        /*        액션바 설정 부분    */
        //----------------------------
        //액션바 가져오기

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_bar_review);
        actionBar.setTitle("리뷰 화면");

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //각 해당하는 변수의 xml 컴포넌트를 등록
        smokingarea_name_TV = findViewById(R.id.review_smokingarea);
        smokingarea_avg_star_point_TV = findViewById(R.id.avg_point);
        bench_CB = findViewById(R.id.bench);
        roof_CB = findViewById(R.id.roof);
        vtl_CB = findViewById(R.id.vtl);
        star_point_RB = findViewById(R.id.ratingbar);
        star_point_TV = findViewById(R.id.ratingvalue);
        review_comment_ET = findViewById((R.id.edit_review_comment));
        reg_comment_Btn = findViewById((R.id.comment_reg_button));
        smokingarea_image_IV =findViewById(R.id.areaimage);
        report_ctn_TV= findViewById(R.id.report_cnt);
        //----------------------------
        /*   뷰에 해당하는 값 설정    */
        //----------------------------

        //흡연구역 정보를 intent를 통해 받음
        Intent intent = getIntent();
        smoking_area_data =intent.getExtras().getStringArray("arr");
        img_url=smoking_area_data[7];
        smokingarea_image_IV.setImageResource(R.drawable.defaultimg);
        //받아온 정보를 각 항목에 설정
        if (!img_url.equals("null")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(ReviewActivity.this).load("http://18.222.175.17:8080/SmokingArea/img/"+img_url+".jpg").into(smokingarea_image_IV);
                }
            });
        }

        smokingarea_name_TV.setText(smoking_area_data[3]);

        if(smoking_area_data[0].charAt(0)=='1')
        {
            bench_CB.setChecked(true);
        }
        else{
            bench_CB.setChecked(false);
        }
        if(smoking_area_data[1].charAt(0)=='1')
        {
            roof_CB.setChecked(true);
        }
        else{
            roof_CB.setChecked(false);
        }
        if(smoking_area_data[2].charAt(0)=='1')
        {
            vtl_CB.setChecked(true);
        }
        else{
            vtl_CB.setChecked(false);
        }
        //3가지 항목 체크박스 선택할 수 없도록 하기
        bench_CB.setClickable(false);
        roof_CB.setClickable(false);
        vtl_CB.setClickable(false);

        //-------------------------------------
        /* 댓글 등록버튼을 눌렀을 때 리스너 등록 */
        //-------------------------------------
        reg_comment_Btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                //JSONObject에 서버에 보내줄 댓글 데이터 담아줌
                JSONObject sbParam = new JSONObject();
                try {
                    sbParam.put("smoking_area_no",Integer.parseInt(smoking_area_data[6]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sbParam.put("smoking_review_reg_user", sp.getString("token",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sbParam.put("smoking_review_ctnt", review_comment_ET.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sbParam.put("smoking_review_point", star_point_TV.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //-------------------------------------
                /* 서버로 댓글 입력 클래스 생성 및 실행  */
                //-------------------------------------
                NetworkTaskWrite networkTaskWrite = new NetworkTaskWrite(sbParam.toString());
                Log.d("reviewComment",sbParam.toString());
                networkTaskWrite.execute();


            }
        });//setONClickListener func()f


        //------------------------
        /*흡연구역 댓글정보 갖고오기*/
        //------------------------
        //리뷰 액티비티 networkTask 클래스 생성 및 실행
        ReviewActivity.NetworkTask networkTask = new ReviewActivity.NetworkTask(this,smoking_area_data[6]);
        networkTask.execute();



        //-------------------------
        /*  별점 star_point_RB 리스너 */
        //-------------------------
        star_point_RB.setOnRatingBarChangeListener(new RatingbarListener());
    }

    private void setView_editText_KeyBoardView() {
        keyboardHeightProvider = new KeyboardHeightProvider(this);

        editTextLinearLayoutView = findViewById(R.id.linearLayout4);
        editTextLinearLayoutView.post(() -> initialY = editTextLinearLayoutView.getY());

        View view = findViewById(R.id.review_layout);
        view.post(() -> keyboardHeightProvider.start());
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height == 0) {
            editTextLinearLayoutView.setY(initialY);
            editTextLinearLayoutView.requestLayout();
        } else {

            float newPosition = initialY - height;
            editTextLinearLayoutView.setY(newPosition);
            editTextLinearLayoutView.requestLayout();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }
    class RatingbarListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            star_point_RB.setRating(rating);
            star_point_TV.setText(Float.toString(star_point_RB.getRating()));
            star_point_TV.setTypeface(null, Typeface.BOLD);
        }
    }//Ratingbarlistenr class


    //--------------------------------------------------
    /* 해당 흡연구역의 댓글을 뿌려주는 NetworkTask 클래스  */
    //--------------------------------------------------
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        String values;
        Context mcontext;

        NetworkTask(Context mcontext, String values) {
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
                result = ServerReviewData(values);
                Log.d("result",result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
            if (result != "") {
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    //TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //신고수,평균별점 넣기
                        if(i==jsonArray.length()-1)
                        {
                            smokingarea_avg_star_point_TV.setText(jsonObject.getString("point"));
                            report_ctn_TV.setText(jsonObject.getString("report"));
                        }
                        else{
                            // Pulling items from the array
                            arrayregDate.add(jsonObject.getString("reg_date"));
                            arrayregUser.add(jsonObject.getString("reg_user"));
                            arrayctnt.add(jsonObject.getString("ctnt"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //listView에 xml의 listView를 가져와서 넣어줌
                listView = findViewById(R.id.listView);

                //reviewModel에 데이터를 넣어주고 arrayList<ReviewModel>에 넣어줌
                for (int i = 0; i < arrayregUser.size(); i++) {
                    ReviewModel reviewModel = new ReviewModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
                    //bind all strings in an array
                    arrayList.add(reviewModel);
                }

                //ReviewlistViewAdapter클래스에 결과를 넘겨줌
                adapter = new ReviewListViewAdapter(mcontext, arrayList);

                //bind the adapter to the listview
                listView.setAdapter(adapter);
            }//result not null
            else {
                Log.d("오류:", "정보 안넘어옴!");
            }
        }
    }

    //------------------------------------------------------
    /*서버로 해당 흡연구역 정보를 넘겨주고 댓글 정보를 받는 함수  */
    //------------------------------------------------------
    public String ServerReviewData(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/smokingAreaReview.jsp");
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
            String smokingAreaReviewValue="smokingAreaReviewValue="+values;

            buffer.append(smokingAreaReviewValue);                 // php 변수에 값 대입

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

       // System.out.println(result);
        return result;
    } // HttpPostDat

    //----------------------------------------------------------------
    /*  리뷰화면 댓글 입력 클래스 (클라이언트 -> 서버 , 서버 -> 클라이언트) */
    //----------------------------------------------------------------
    public class NetworkTaskWrite extends AsyncTask<Void, Void, String> {

        String values;

        NetworkTaskWrite(String values) {
            this.values = values;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                //클라이언트로 받은 값들을 result에 넣어줌
                result = sendReviewWrite(values);
                if(result.equals("overlap")){
                    showDialogOverlap("이미 댓글과 별점을 등록하셨습니다.");
                }
                Log.d("reviewcommentIn",result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }
    //////////////////////////이쪽 봐바 승연아!!
        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.

            if(result.contains("overlap")){
                Toast.makeText(ReviewActivity.this, "이미 리뷰를 등록하셨습니다.", Toast.LENGTH_SHORT).show();
                return ;
            }
            else{
                Intent intent = new Intent(ReviewActivity.this, ReviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("arr",smoking_area_data);
                startActivity(intent);
            }

        }
    }
    //서버로 댓글을 JSONObject 형태의 String 값으로 댓글보내고 리턴값을 받는 함수.
    public String sendReviewWrite(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/insertSmokingReview.jsp");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            //--------------------------
            //   서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            String regdata = "json_smokingReviewValue=" + values;
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
        if (result.equals("overlap")) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 사용하고자 하는 코드
                    showDialog("이미 별점과 댓글을 입력하셨습니다.");
                    return ;
                }
            }, 0);

        }

        System.out.println(result);
        return result;
    } // HttpPostDat

    public void report(View view) {
        showDialog("신고하시겠습니까?");
    }

    //----------------------------------------------------------------
    /*  신고 */
    //----------------------------------------------------------------
    public class NetworkTaskReport extends AsyncTask<Void, Void, String> {

        String values;

        NetworkTaskReport(String values) {
            this.values = values;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                //클라이언트로 받은 값들을 result에 넣어줌
                result = sendReviewReport(values);
                Log.d("sendReviewReportIN",result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
            Log.d("data",result);

            if(result.contains("success"))
            {
                Log.d("data","tjdrhd");
                //
                Toast.makeText(ReviewActivity.this, "신고 등록 성공", Toast.LENGTH_SHORT).show();
            }
            else if (result.contains("fail")){
                Log.d("data","tlfvo");
                Toast.makeText(ReviewActivity.this, "신고 등록에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
            else if (result.contains("overlap")){
                Log.d("data","wndqhrtlfvo");
                Toast.makeText(ReviewActivity.this, "이미 신고한 흡연 장소입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //서버로 댓글을 JSONObject 형태의 String 값으로 댓글보내고 리턴값을 받는 함수.
    public String sendReviewReport(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/SmokingArea/insertSmokingReport.jsp");
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
            String regdata = "json_smokingReportValue=" + values;
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

    public void showDialogOverlap(String message) {                                  //장소 등록이 완료되면 다이얼로그 팝업을 띄워주는 메소드
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public void showDialog(String message) {                                  //장소 등록이 완료되면 다이얼로그 팝업을 띄워주는 메소드
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(message);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //JSONObject에 서버에 보내줄 댓글 데이터 담아줌
                        JSONObject sbParam = new JSONObject();
                        try {
                            sbParam.put("report_title", "title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            sbParam.put("report_user", sp.getString("token",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            sbParam.put("report_ctnt","report_IBtn CONTENT");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            sbParam.put("report_smoking_area_no",Integer.parseInt(smoking_area_data[6]));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NetworkTaskReport networkTaskReport = new NetworkTaskReport(sbParam.toString());
                        Log.d("reviewComment", sbParam.toString());
                        networkTaskReport.execute();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("ddddd");
                    }
                });
        builder.show();
    }

}

