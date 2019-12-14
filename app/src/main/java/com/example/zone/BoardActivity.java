package com.example.zone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class BoardActivity extends AppCompatActivity {
    private JSONArray boardJSONArray;  //서버로부터 JSON Array를 받아 저장할 변수
    private ActionBar actionBar;    //게시판화면에 쓰일 actionBar
    ListView listView; //게시판 ListView 레이아웃 형성을 위한 객체 생성
    ListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    private String tag;
    //FloatingActionButton 선언 
    FloatingActionButton write_fab;
    FloatingActionButton tag_fab;
    FloatingActionButton tag_all_fab, tag_health_fab, tag_ciga_fab;
    private Boolean isFABOpen = false;

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arraytag = new ArrayList<String>();
    ArrayList<String> arraytitle = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();
    ArrayList<Integer> arrayboardNo = new ArrayList<Integer>();
    ArrayList<Integer> arrayicon = new ArrayList<Integer>();

    //BoardModel 클래스 타입의 ArrayList
    ArrayList<BoardModel> arrayList = new ArrayList<BoardModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //이 액티비티의 view에 해당하는 layout을 등록한다.
        initLayoutBoardActivity();
        //----------------------------
        /*      게시글을 전부 가져옴  */
        //----------------------------
        Intent intent = getIntent();
        try {
            if(!(intent.getExtras().isEmpty()))
            {
                tag=intent.getExtras().getString("태그");
            }
        } catch (NullPointerException e) {
            tag="전체";
            e.printStackTrace();
        }


        NetworkTask networkTask = new NetworkTask( this,tag);
        networkTask.execute();
    }

    public void initLayoutBoardActivity() {
        setContentView(R.layout.board_activity_main);
        //actionbar 등록
        setView_actionbarView();
        //FAB 이벤트 처리 함수 생성 및 등록
        setView_tagFabView();
        setView_tagAllFabView();
        setView_tagHealthFabView();
        setView_tagCigaFabView();
        setView_writeFabView();
    }

    private void setView_writeFabView() {
        write_fab = (FloatingActionButton) findViewById(R.id.write_fab);
        write_fab.setOnClickListener(new FABClickListener());

    }

    private void setView_tagCigaFabView() {
        tag_ciga_fab = (FloatingActionButton) findViewById(R.id.all_tag_fab);
        tag_ciga_fab.setOnClickListener(new FABClickListener());
    }

    private void setView_tagHealthFabView() {
        tag_health_fab = (FloatingActionButton) findViewById(R.id.health_tag_fab);
        tag_health_fab.setOnClickListener(new FABClickListener());
    }

    private void setView_tagAllFabView() {
        tag_all_fab = (FloatingActionButton) findViewById(R.id.ciga_tag_fab);
        tag_all_fab.setOnClickListener(new FABClickListener());
    }

    private void setView_tagFabView() {
        tag_fab = (FloatingActionButton) findViewById(R.id.tagFab);
        tag_fab.setOnClickListener(new FABClickListener());
    }

    private void setView_actionbarView() {
        //액션바 가져오기
        actionBar = getSupportActionBar();

        //액션바 타이틀 가운데 정렬
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_bar);

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    //FAB 리스너 함수
    class FABClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //----------------------------
            /* FAb 클릭 이벤트 처리 구간  */
            //----------------------------

            if (v.getId() == R.id.tagFab) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }

            }
            else if (v.getId() == R.id.ciga_tag_fab) {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("태그","담배");
                startActivity(intent);
            }
            else if (v.getId() == R.id.health_tag_fab) {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("태그","건강");
                startActivity(intent);
            }
            else if (v.getId() == R.id.all_tag_fab) {
                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);
                //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("태그","전체");
                startActivity(intent);
            }
            else if(v.getId()== R.id.write_fab) {
                //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
                Intent intent = new Intent(getApplicationContext(), BoardWriteActivity.class);
                //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }

        }
    }
    private void showFABMenu() {
        isFABOpen = true;
        tag_all_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        tag_health_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        tag_ciga_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        tag_all_fab.animate().translationY(0);
        tag_health_fab.animate().translationY(0);
        tag_ciga_fab.animate().translationY(0);
    }

    //액티비티가 시작될 때 단 한번만 호출되는 함수로 이안에서 MenuItem생성과 초기화를 하면 됨.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Menu Inflacter를 통하여 XML Menu 리소스에 정의된 내용을 파싱하여 Menu 객체를 생성
        getMenuInflater().inflate(R.menu.menu, menu);

        //----------------------------
        /* 메뉴의 SearchView 설정  */
        //----------------------------
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search_hint_query));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //검색어 완료시
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //검색어 입력시(실질적인 검색 기능 구현 listview의 filter(s)함수를 통해서!)
            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(s);
                }
                return true;
            }
        });//onQueryTextListener func()
        return true;
    }//onCreateOptionMenu func()



    //----------------------------
    /*  게시글 뿌려주는 클래스     */
    //----------------------------
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String value;
        ContentValues values;
        Context mcontext;

        NetworkTask(Context mcontext, ContentValues values) {
            this.mcontext = mcontext;
            this.values = values;
        } // 생성자

        NetworkTask(Context mcontext,String value) {
            this.mcontext = mcontext;
            this.value = value;
        }//생성자

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }  //실행 이전에 작업되는 것들을 정의하는 함수

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                //서버의 게시판 정보를 받아오는 함수를 호출함.
                result = ServeBoardData(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        } //백그라운드 작업 함수

        //------------------------------------
        /* 서버로 부터 받아온 게시글로 UI 작업  */
        //------------------------------------
        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.

            if (result != "") {
                try {
                    boardJSONArray = new JSONArray(result);
                } catch (JSONException e) {
                    //TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //제일 최근에 담긴 게시글이 맨 위에 뜨도록 함

                for (int i = boardJSONArray.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject jsonObject = boardJSONArray.getJSONObject(i);
                        // Pulling items from the array
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
                        arraytag.add(jsonObject.getString("tag"));
                        arraytitle.add(jsonObject.getString("title"));
                        arrayboardNo.add(jsonObject.getInt("no"));
                        arrayicon.add(0);
                        //arrayicon.add(jsonObject.getInt("icon"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //listView에 xml의 listView를 가져와서 넣어줌
                listView = findViewById(R.id.listView);

                //Model에 데이터를 넣어주고 arrayList<BoardModel>에 넣어줌
                for (int i = 0; i < arraytitle.size(); i++) {
                    BoardModel boardModel = new BoardModel(arrayboardNo.get(i), arraytag.get(i), arrayregDate.get(i), arrayregUser.get(i), arraytitle.get(i), arrayctnt.get(i), arrayicon.get(i));
                    //bind all strings in an array
                    arrayList.add(boardModel);
                }

                //listViewAdapter클래스에 결과를 넘겨줌
                adapter = new ListViewAdapter(mcontext, arrayList);

                //bind the adapter to the listview
                listView.setAdapter(adapter);
            }//result not null
            //result is null
            else {
                Toast.makeText(BoardActivity.this, "게시글 없음.", Toast.LENGTH_SHORT).show();
            }
        }
    } //NetWorkTask Class

    //------------------------------------
    /* 서버로 부터 게시판 DB 정보를 받아옴  */
    //------------------------------------
    public String ServeBoardData(String values) throws JSONException {

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
            StringBuffer buffer = new StringBuffer();
            String regdata = "boardTag=" + values;
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
        return result; //onPostExcute()로 전달
    } // HttpPostDat
}//Board Activity Class

