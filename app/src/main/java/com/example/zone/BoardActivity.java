package com.example.zone;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    ListViewAdapter adapter;
    int[] board_no; //게시판 번호
    String[] reg_date; //등록 날짜
    String[] reg_user; // 등록 유저
    String[] tag; // 게시판 태그 저장
    String[] title;
    String[] description;
    int[] icon;
    ArrayList<Model> arrayList = new ArrayList<Model>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_activity_main);

        //액션바 가져오기
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_bar);
        }

        //actionBar.setTitle("빠담 게시판");
        //액션바 타이틀 가운데 정렬



        //더미 데이터 입력
        reg_user = new String[]{"박지성", "손흥민", "황희찬", "이강인", "남태희"};
        reg_date = new String[]{"2019/09/30", "2019/00/00", "2019/00/00", "2019/00/00", "2019/00/00"};
        tag = new String[]{"공지", "담배", "건강", "Q&A", "오류", "기타"};
        title = new String[]{"Battery", "Cpu", "Display", "Memory", "Sensor"};
        description = new String[]{"Battery detail...", "Cpu detail...", "Display detail...", "Memory detail...", "Sensor detail..."};
        icon = new int[]{R.drawable.battery, R.drawable.cpu, R.drawable.display, R.drawable.memory, R.drawable.sensor};

        listView = findViewById(R.id.listView);

        //Model에 데이터를 넣어주고 arrayList<Model>에 넣어줌
        for (int i = 0; i < title.length; i++) {
            Model model = new Model(tag[i], reg_date[i], reg_user[i], title[i], description[i], icon[i]);
            //bind all strings in an array
            arrayList.add(model);
        }
        //listViewAdapter클래스에 결과를 넘겨줌
        adapter = new ListViewAdapter(this, arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);
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
        // Handle action bar item clicks here. The action bar willf
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if (id == R.id.action_settings) {
            System.out.println("눌름");
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
