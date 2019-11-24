package com.example.zone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//listview에 적용될 데이터들을 view에 언제 어떻게 넣을지 알려주는 Class
public class BoardCommentListViewAdapter extends BaseAdapter {

    //toast출력 test변수
    static int callcnt = 0;

    //variables
    Context mContext; //어플리케이션에 관하여 시스템이 관리하고 있는 정보에 접근 , 안드로이드 시스템에서 제공하는 API 를 호출할 수 있는 기능


    LayoutInflater inflater; //layout xml을 layoutInflater를 통해 view를 객체화
    List<BoardCommentModel> modellist; // main으로부터 modellist들을 전달 받을 객체
    ArrayList<BoardCommentModel> arrayList; // modellist로부터 받은 모델을 array형으로 받을 객체

    // ReviewListViewAdapter 생성자
    public BoardCommentListViewAdapter(Context context, List<BoardCommentModel> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<BoardCommentModel>();
        this.arrayList.addAll(modellist);
    }

    //ListView의 퍼포먼스를 향상시키기위한 Holder Class 정의
    public class ViewHolder {
        TextView mDescTv, mReg_DateTv, mReg_UserTv; // 내용,날짜,유저
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int i) {
        return modellist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override // 한화면 단위로 호출되는 getView func
    public View getView(final int position, View view, ViewGroup parent) {

        //callcnt++;
        //Toast myToast = Toast.makeText(mContext, "호출됨: "+callcnt, Toast.LENGTH_SHORT);
        //myToast.show();
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_review_content, null); //row xml을 view로 형성

            //row.xml에서 각각의 값들을 홀더에 입력
            holder.mReg_DateTv = view.findViewById(R.id.reg_date);
            holder.mReg_UserTv = view.findViewById(R.id.reg_user);
            holder.mDescTv = view.findViewById(R.id.mainDesc);

            //태그 설정
            view.setTag(holder);
            //뷰가 NULL이 아니면 이미 Holder 객체가 생성 되어 있으므로 view의 태그값을 Holder에 입력
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //textView에 값을 넣어주는 작업
        holder.mDescTv.setText(modellist.get(position).getReg_ctnt());
        holder.mReg_DateTv.setText(modellist.get(position).getReg_date());
        holder.mReg_UserTv.setText(modellist.get(position).getReg_user());


        //listview의 item을 Click했을 때
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }//getView func


}
