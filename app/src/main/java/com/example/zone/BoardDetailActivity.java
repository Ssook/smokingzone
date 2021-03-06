package com.example.zone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoardDetailActivity extends AppCompatActivity implements KeyboardHeightProvider.KeyboardHeightObserver {

    private KeyboardHeightProvider keyboardHeightProvider;

    private ViewGroup editTextLinearLayoutView;
    private float initialY;

    // 게시글 화면 구성 변수들
    private TextView title_TV;
    private TextView desc_TV;
    private EditText review_comment_ET;
    private Button reg_comment_Btn;
    private TextView reg_user_TV;
    private TextView reg_date_TV;
    private String mTitle;
    private String mDesc;
    private String mUser;
    private String mDate;

    // 게시글 번호 저장 변수
    private int board_no;

    //서버로부터 JSON Array를 받아 저장할 변수
    private JSONArray jsonArray;
    //게시글의 댓글 ListView 레이아웃 형성을 위한 객체 생성
    ListView listView;
    // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체
    BoardCommentListViewAdapter adapter;

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayBoardNo = new ArrayList<String>();
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();

    //BoardCommentModel 클래스 타입 객체들을 담을 arrayList 생성
    ArrayList<BoardCommentModel> arrayList = new ArrayList<BoardCommentModel>();

    // 사용자 닉네임을 받아오는 변수들
    SharedPreferences sp;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //게시글 화면의 layout 설정
        initLayoutBoardDetailActivity();
        //사용자 닉네임 받아오기
        setUserNickName();
        //-------------------------------
        /* 해당 게시글의 모든 댓글을 받아옴*/
        //-------------------------------
        BoardDetailActivity.NetworkTask networkTask = new BoardDetailActivity.NetworkTask(this, Integer.toString(board_no));
        networkTask.execute();

    }//onCreate func()

    private void setUserNickName() {
        sp = getSharedPreferences("profile", MODE_PRIVATE);
        name = sp.getString("name", "");
    }

    //해당하는 layout 컴포넌트를 변수에 설정
    public void initLayoutBoardDetailActivity() {
        setContentView(R.layout.activity_board_detail);
        //editText 키보드 팝업 설정
        setView_editText_KeyBoardView();
        //actionbar 설정
        setView_actionbarView();
        //layout view 설정
        //intent 객체로 부터 해당 값들을 받아옴.

        //intent 정보 저장
        Intent intent = getIntent();

        mTitle = intent.getStringExtra("mTitle");
        mDesc = intent.getStringExtra("mDesc");
        mUser = intent.getStringExtra("mUser");
        mDate = intent.getStringExtra("mDate");
        board_no = intent.getIntExtra("board_no", 0);

        //게시글 내용 등록
        setView_titleTVView();
        setView_descTVView();
        setView_regDateTVView();
        setView_regUserTVView();
        setView_regCommentBtn();
        setView_reviewCommentET();
    }

    private void setView_actionbarView() {
        //----------------------------
        /*        액션바 설정 부분    */
        //----------------------------
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.activity_board_detail);
        actionBar.setTitle("빠담 게시글");
    }

    private void setView_titleTVView() {
        title_TV = findViewById(R.id.title);
        title_TV.setText(mTitle);
    }

    private void setView_descTVView() {
        desc_TV = findViewById(R.id.textView);
        desc_TV.setText(mDesc);
    }

    private void setView_regDateTVView() {
        reg_date_TV = findViewById(R.id.reg_date);
        reg_date_TV.setText(mDate);
    }

    private void setView_regUserTVView() {
        reg_user_TV = findViewById(R.id.reg_user);
        reg_user_TV.setText(mUser);
    }

    private void setView_regCommentBtn() {
        reg_comment_Btn = findViewById((R.id.comment_reg_button));

    }

    private void setView_reviewCommentET() {
        review_comment_ET = findViewById((R.id.edit_review_comment));
        //----------------------------
        /* 댓글 등록 버튼 눌렀을 때 작업*/
        //----------------------------
        reg_comment_Btn.setOnClickListener(new regCommentBtnListener());
    }

    private void setView_editText_KeyBoardView() {
        keyboardHeightProvider = new KeyboardHeightProvider(this);

        editTextLinearLayoutView = findViewById(R.id.linearLayout);
        editTextLinearLayoutView.post(() -> initialY = editTextLinearLayoutView.getY());

        View view = findViewById(R.id.board_detail_layout);
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

    class regCommentBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //----------------------------
            /* 서버에 댓글 정보를 보냄 Part*/
            //----------------------------
            JSONObject sbParam = new JSONObject();
            try {
                sbParam.put("board_area_no", board_no);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                sbParam.put("board_review_reg_user", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                sbParam.put("board_review_ctnt", review_comment_ET.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //-------------------------------
            /*JSON형식 String 값을 서버에 보냄*/
            //-------------------------------
            NetworkTaskWrite networkTaskWrite = new NetworkTaskWrite(sbParam.toString());
            networkTaskWrite.execute();

        }
    }//댓글 등록버튼 regCommentBtnListener func()

    //---------------------------------------
    /* 해당 게시글의 모든 댓글을 받아오는 클래스*/
    //---------------------------------------
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String values;
        Context mcontext;

        NetworkTask(Context mcontext, String values) {
            this.mcontext = mcontext;
            this.values = values;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }//실행 이전에 작업되는 것들을 정의하는 함수

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                //서버로 게시글 번호를 주고 게시글 댓글 데이타를 받아옴.
                result = ServerBoardCommentData(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }// 백그라운드 작업 함수

        //---------------------------------------------
        /* 서버로 부터 받아온 게시글 댓글로 댓글 UI 작업  */
        //---------------------------------------------
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
                        // array에 해당 값들을 넣어줌.
                        //Time Setting
                        String time = jsonObject.getString("reg_date");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(time);
                        } catch (ParseException e) {
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                            try {
                                date = simpleDateFormat1.parse(time);
                            } catch (ParseException ex) {
                                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    date = simpleDateFormat2.parse(time);
                                } catch (ParseException exc) {
                                    exc.printStackTrace();
                                }
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                        Long longDate = date.getTime();
                        arrayregDate.add(TimeString.formatTimeString(longDate));
                        arrayregUser.add(jsonObject.getString("reg_user"));
                        arrayctnt.add(jsonObject.getString("ctnt"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //listView에 xml의 listView를 가져와서 넣어줌
                listView = findViewById(R.id.listView);

                //Model에 데이터를 넣어주고 arrayList<BoardCommentModel>에 넣어줌
                for (int i = 0; i < arrayregUser.size(); i++) {
                    BoardCommentModel boardCommentModel = new BoardCommentModel(arrayregDate.get(i), arrayregUser.get(i), arrayctnt.get(i));
                    //bind all strings in an array
                    arrayList.add(boardCommentModel);
                }

                //listViewAdapter클래스에 결과를 넘겨줌
                adapter = new BoardCommentListViewAdapter(mcontext, arrayList);

                //adapter 설정
                listView.setAdapter(adapter);
            }//result not null
            else {
                Toast.makeText(BoardDetailActivity.this, "댓글 없음.", Toast.LENGTH_SHORT).show();
            }
        }//onPostExecute func()
    }//NetWorkTask Class

    //--------------------------------------------------------
    /* 서버로 게시글 번호를 주고 게시글 댓글 정보를 받아오는 함수  */
    //--------------------------------------------------------
    public String ServerBoardCommentData(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://18.222.175.17:8080/SmokingArea/Board/boardReview.jsp");
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
            String currentlocationsend = "boardReviewValue=" + values;

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
            result = builder.toString();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        System.out.println(result);
        return result;
    } // HttpPostDat

    //------------------------------------
    /* 해당 게시글에 댓글을 입력하는  클래스*/
    //------------------------------------
    public class NetworkTaskWrite extends AsyncTask<Void, Void, String> {

        String values;

        NetworkTaskWrite(String values) {
            this.values = values;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }//실행 이전 작업에 대한 함수

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                //댓글 값들을 넘겨주는 함수를 호출
                result = sendCommentWrite(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }// 백그라운드 작업 함수

        //-------------------------------------------
        /* 댓글을 입력하고 게시글 화면 최신화 UI 작업  */
        //-------------------------------------------
        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
            //--------------------------------------
            /* 게시글 화면 intent 설정 및 시작 작업  */
            //-------------------------------------
            Intent intent = new Intent(BoardDetailActivity.this, BoardDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            intent.putExtra("mTitle", mTitle);
            intent.putExtra("mDesc", mDesc);
            intent.putExtra("board_no", board_no);
            intent.putExtra("mUser", mUser);
            intent.putExtra("mDate", mDate);

            startActivity(intent);

        }
    }

    //-----------------------------------
    /* 서버로 게시글 댓글을 넘겨주는 함수  */
    //-----------------------------------
    public String sendCommentWrite(String values) throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------

            URL url = new URL("http://18.222.175.17:8080/SmokingArea/Board/insertBoardReview.jsp");
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
            String regdata = "json_boardReviewValue=" + values;
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
        return result;
    } // HttpPostDat
} //BoardDetailActivity Class


