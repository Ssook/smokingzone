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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends AppCompatActivity {
    private JSONArray mArray;  //서버로부터 JSON Array를 받아 저장할 변수
    private ActionBar actionBar;    //게시판화면에 쓰일 actionBar
    ListView listView; //게시판 ListView 레이아웃 형성을 위한 객체 생성
    ListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    FloatingActionButton tagFab;
    FloatingActionButton fab1,fab2,fab3;
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
        setContentView(R.layout.board_activity_main);

        //----------------------------
        /*        액션바 설정 부분    */
        //----------------------------
        //액션바 가져오기
        actionBar = getSupportActionBar();

        //액션바 타이틀 가운데 정렬
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_bar);

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //----------------------------
        /*      게시글을 전부 가져옴  */
        //----------------------------
        NetworkTask networkTask = new NetworkTask(this, null);
        networkTask.execute();

        //----------------------------
        /*      게시판 글쓰기 버튼   */
        //----------------------------
        //FAB 이벤트 처리 함수 생성 및 등록
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FABClickListener());
        tagFab = (FloatingActionButton) findViewById(R.id.tagFab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        tagFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }

    //FAB 리스너 함수
    class FABClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //----------------------------
            /* FAb 클릭 이벤트 처리 구간  */
            //----------------------------
            //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
            Intent intent = new Intent(getApplicationContext(), BoardWriteActivity.class);
            //글쓰기 완료 후 전환 시 액티비티가 남지 않게 함
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
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


    //옵션 메뉴들을 눌렀을 때 기능 명시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings1) {
            //do your functionality here
            return true;
        }
        if (id == R.id.action_settings2) {
            //do your functionality here
            return true;
        }
        if (id == R.id.action_settings3) {
            //do your functionality here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------
    /*  게시글 뿌려주는 클래스     */
    //----------------------------
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        ContentValues values;
        Context mcontext;

        NetworkTask(Context mcontext, ContentValues values) {
            this.mcontext = mcontext;
            this.values = values;
        } // 생성자

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
                result = ServeBoardData();
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
                    mArray = new JSONArray(result);
                } catch (JSONException e) {
                    //TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //제일 최근에 담긴 게시글이 맨 위에 뜨도록 함
                for (int i =  mArray.length()-1; i >=0 ; i--) {
                    try {
                        JSONObject jsonObject = mArray.getJSONObject(i);
                        // Pulling items from the array
                        //Time Setting
                        Log.d("data",jsonObject.getString("reg_date"));
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
                        //Log.d("data",Long.toString(date.getTime()));
                        Long longDate = date.getTime();
                        arrayregDate.add( TimeString.formatTimeString(longDate));
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
                    BoardModel boardModel = new BoardModel(arrayboardNo.get(i),arraytag.get(i), arrayregDate.get(i), arrayregUser.get(i), arraytitle.get(i), arrayctnt.get(i), arrayicon.get(i));
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
                Log.d("data:", "게시글 없음!");
            }
        }
    } //NetWorkTask Class

    //------------------------------------
    /* 서버로 부터 게시판 DB 정보를 받아옴  */
    //------------------------------------
    public String ServeBoardData() throws JSONException {

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
        return result; //onPostExcute()로 전달
    } // HttpPostDat
}//Board Activity Class

