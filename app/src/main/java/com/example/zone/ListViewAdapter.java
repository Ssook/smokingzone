package com.example.zone;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
public class ListViewAdapter extends BaseAdapter {

    //toast출력 test변수
    static int callcnt = 0;

    //variables
    Context mContext; //어플리케이션에 관하여 시스템이 관리하고 있는 정보에 접근 , 안드로이드 시스템에서 제공하는 API 를 호출할 수 있는 기능


    LayoutInflater inflater; //layout xml을 layoutInflater를 통해 view를 객체화
    List<BoardModel> modellist; // main으로부터 modellist들을 전달 받을 객체
    ArrayList<BoardModel> arrayList; // modellist로부터 받은 모델을 array형으로 받을 객체

    // ListViewAdapter 생성자
    public ListViewAdapter(Context context, List<BoardModel> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<BoardModel>();
        this.arrayList.addAll(modellist);
    }

    //ListView의 퍼포먼스를 향상시키기위한 Holder Class 정의
    public class ViewHolder {
        TextView mTitleTv, mDescTv, mReg_DateTv, mReg_UserTv, mtagTv; //제목,내용,등록날자,등록유저,태그
        ImageView mIconIv; //이미지
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
            view = inflater.inflate(R.layout.row, null); //row xml을 view로 형성

            //row.xml에서 각각의 값들을 홀더에 입력
            holder.mTitleTv = view.findViewById(R.id.mainTitle);
            holder.mReg_DateTv = view.findViewById(R.id.reg_date);
            holder.mReg_UserTv = view.findViewById(R.id.reg_user);
            holder.mtagTv = view.findViewById(R.id.tag);
            holder.mDescTv = view.findViewById(R.id.mainDesc);
            holder.mIconIv = view.findViewById(R.id.mainIcon);
            //태그 설정
            view.setTag(holder);
            //뷰가 NULL이 아니면 이미 Holder 객체가 생성 되어 있으므로 view의 태그값을 Holder에 입력
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //textView에 값을 넣어주는 작업
        holder.mTitleTv.setText(modellist.get(position).getTitle());
        holder.mDescTv.setText(modellist.get(position).getDesc());
        holder.mReg_DateTv.setText(modellist.get(position).getReg_date());
        holder.mReg_UserTv.setText(modellist.get(position).getReg_user());
        holder.mtagTv.setText(modellist.get(position).getTag());
        // holder.mBoard_NoTv.setText(modellist.get(position).getBoard_no());
        //imageView에 값을 넣어주는 작업
        holder.mIconIv.setImageResource(modellist.get(position).getIcon());

        //listview의 item을 Click했을 때
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // listview에 있는 item을 클릭했을 때의 이벤트를 Listen, 그 때의 작업
                // 제목은 액션바에, 내용은 TextView에 출력
                Intent intent = new Intent(mContext, BoardDetailActivity.class); //인탠트 객체는 액티비티 이동,데이터 입출력에 사용
             //   Log.d("data", modellist.get(position).getTitle());
                intent.putExtra("actionBarTitle", modellist.get(position).getTitle());
                intent.putExtra("contentTv", modellist.get(position).getDesc());
                intent.putExtra("board_no",modellist.get(position).getBoard_no());
                mContext.startActivity(intent);

            }
        });

        return view;
    }//getView func

    //filter
    public void filter(String charText) {
        BoardModel tempBoardModel =  new BoardModel();
        charText = charText.toLowerCase(Locale.getDefault()); //모두 소문자로 변환
        modellist.clear();
        if (charText.length() == 0) { //없을 경우
            modellist.addAll(arrayList); //모두 보여줌
        } else {
            for (BoardModel boardModel : arrayList) {
                //모델의 제목과 일치하는 것이 있을 경우
                if (boardModel.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    modellist.add(boardModel);
                    //동일내용 중복 표시 방지
                    tempBoardModel = boardModel;
                }
                //모델의 내용과 일치하는 것이 있을 경우
                if (boardModel.getDesc().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    //중복검사
                    if(tempBoardModel != boardModel)
                    {
                        modellist.add(boardModel);
                    }
                }

            }
        }
        //listView 갱신
        notifyDataSetChanged();
    }//filter func
}
