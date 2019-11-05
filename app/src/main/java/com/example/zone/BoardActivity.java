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
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {
    private JSONArray mArray;  //서버로부터 JSON Array를 받아 저장할 변수
    private ActionBar actionBar;
    ListView listView; //게시판 ListView 레이아웃 형성을 위한 객체 생성
    ListViewAdapter adapter; // 뷰에 넣을 데이터들을 어떠한 형식과 어떠한 값들로 구성할지 정하는 adapter 객체

    //각각의 데이터가 들어갈 ArrayList 생성
    ArrayList<String> arrayregDate = new ArrayList<String>();
    ArrayList<String> arrayregUser = new ArrayList<String>();
    ArrayList<String> arraytag = new ArrayList<String>();
    ArrayList<String> arraytitle = new ArrayList<String>();
    ArrayList<String> arrayctnt = new ArrayList<String>();
    ArrayList<Integer> arrayboardNo = new ArrayList<Integer>();
    ArrayList<Integer> arrayicon = new ArrayList<Integer>();


    ArrayList<BoardModel> arrayList = new ArrayList<BoardModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_activity_main);
        Toast.makeText(getApplicationContext(), "zzz", Toast.LENGTH_LONG).show();
        //액션바 가져오기
        actionBar = getSupportActionBar();
        //actionBar.setTitle("빠담 게시판");

        //액션바 타이틀 가운데 정렬
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_bar);

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // String url = "http://192.168.219.118:8080/SmokingArea/Board/boardList.jsp";
        NetworkTask networkTask = new NetworkTask(this, null);
        networkTask.execute();


        //더미 배열 생성
        String[] dummyuser = new String[]{"박지성", "손흥민", "황희찬", "이강인", "남태희"};
        String[] dummydate = new String[]{"2019/09/30", "2019/00/00", "2019/00/00", "2019/00/00", "2019/00/00"};
        String[] dummytag = new String[]{"공지", "담배", "건강", "Q&A", "오류", "기타"};
        String[] dummytitle = new String[]{"Battery", "Cpu", "Display", "Memory", "Sensor"};
        String[] dummyctnt = new String[]{"Battery detail...", "Cpu detail...", "Display detail...", "Memory detail...", "Sensor detail..."};
        int[] dummyicon = new int[]{R.drawable.battery, R.drawable.cpu, R.drawable.display, R.drawable.memory, R.drawable.sensor};

        //더미 데이터 입력
        for (int i = 0; i < 5; i++) {
            arrayregUser.add(dummyuser[i]);
            arrayregDate.add(dummydate[i]);
            arraytag.add(dummytag[i]);
            arraytitle.add(dummytitle[i]);
            arrayctnt.add(dummyctnt[i]);
            arrayicon.add(dummyicon[i]);
        }


        //더미 데이터 입력 (Json Parsing)
        String Test = "[{\"no\":\"1\",\"reg_date\":\"2019-10-14\",\"reg_user\":\"user\",\"ctnt\":\"ctntsadasdasdsada\",\"tag\":\"tag\",\"title\":\"타이룰\"}]";

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
            BoardModel boardModel = new BoardModel(arraytag.get(i), arrayregDate.get(i), arrayregUser.get(i), arraytitle.get(i), arrayctnt.get(i), arrayicon.get(i));
            //bind all strings in an array
            arrayList.add(boardModel);
        }

        //listViewAdapter클래스에 결과를 넘겨줌
        adapter = new ListViewAdapter(this, arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);

        //FAB 이벤트 처리 함수 생성 및 등록
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FABClickListener());
    }

    //FAB 리스너 함수
    class FABClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //FAb 클릭 이벤트 처리 구간

            Intent intent = new Intent(getApplicationContext(), BoardWriteActivity.class); //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
            startActivity(intent);
        }
    }

    //액티비티가 시작될 때 단 한번만 호출되는 함수로 이안에서 MenuItem생성과 초기화를 하면 됨.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu Inflacter를 통하여 XML Menu 리소스에 정의된 내용을 파싱하여 Menu 객체를 생성
        getMenuInflater().inflate(R.menu.menu, menu);

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
        });
        return true;
    }

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
                result = ServeBoardData();
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
            String Test = "[{\"no\":\"2\",\"reg_date\":\"2019-11-03\",\"reg_user\":\"익명\",\"ctnt\":\"ㅁㅇㄻㅇㄻ\",\"tag\":\"ㅇㄻㅇㄹ\",\"title\":\"타이룰\"}]";
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
                    BoardModel boardModel = new BoardModel(arraytag.get(i), arrayregDate.get(i), arrayregUser.get(i), arraytitle.get(i), arrayctnt.get(i), arrayicon.get(i));
                    //bind all strings in an array
                    arrayList.add(boardModel);
                }

                //listViewAdapter클래스에 결과를 넘겨줌
                adapter = new ListViewAdapter(mcontext, arrayList);

                //bind the adapter to the listview
                listView.setAdapter(adapter);
            }//result not null
            else {
                Log.d("data:", "게시글 없음!");
            }
        }
    }

    public String ServeBoardData() throws JSONException {

        String result = "";
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL("http://192.168.219.103:8080/SmokingArea/Board/boardList.jsp");
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
}


/*design row of listview*/
/*adding menu to add searchview in actionbar*/
/*add model class*/
/*add adapter class */
/*add some imgaes in drawable folder*/