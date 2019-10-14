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

public class ListViewAdapter extends BaseAdapter {


    //variables
    Context mContext;
    LayoutInflater inflater;
    List<Model> modellist;
    ArrayList<Model> arrayList;

    //constructor


    public ListViewAdapter(Context context, List<Model> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<Model>();
        this.arrayList.addAll(modellist);
    }

    public class ViewHolder {
        TextView mTitleTv, mDescTv,mReg_DateTv,mReg_UserTv,mtagTv;
        ImageView mIconIv;
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

    @Override
    public View getView(final int postition, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row, null);

            //locate the views in row.xml

            holder.mTitleTv = view.findViewById(R.id.mainTitle);
            holder.mReg_DateTv = view.findViewById(R.id.reg_date);
            holder.mReg_UserTv = view.findViewById(R.id.reg_user);
            holder.mtagTv = view.findViewById(R.id.tag);
            holder.mDescTv = view.findViewById(R.id.mainDesc);
            holder.mIconIv = view.findViewById(R.id.mainIcon);

            view.setTag(holder);

        }
        else {
            holder = (ViewHolder)view.getTag();
        }
        //set the results into textviews
        holder.mTitleTv.setText(modellist.get(postition).getTitle());
        holder.mDescTv.setText(modellist.get(postition).getDesc());
        holder.mReg_DateTv.setText(modellist.get(postition).getReg_date());
        holder.mReg_UserTv.setText(modellist.get(postition).getReg_user());
        holder.mtagTv.setText(modellist.get(postition).getTag());
//        holder.mBoard_NoTv.setText(modellist.get(postition).getBoard_no());
        //set the result in imageview
        holder.mIconIv.setImageResource(modellist.get(postition).getIcon());

        //listview item clicks
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code later
                if (modellist.get(postition).getTitle().equals("Battery")){
                    //start NewActivity with title for actionbar and text for textview
                    Intent intent = new Intent(mContext, NewActivity.class);
                    intent.putExtra("actionBarTitle", "Battery");
                    intent.putExtra("contentTv", "This is Battery detail...");
                    mContext.startActivity(intent);
                }
                if (modellist.get(postition).getTitle().equals("Cpu")){
                    //start NewActivity with title for actionbar and text for textview
                    Intent intent = new Intent(mContext, NewActivity.class);
                    intent.putExtra("actionBarTitle", "Cpu");
                    intent.putExtra("contentTv", "This is Cpu detail...");
                    mContext.startActivity(intent);
                }
                if (modellist.get(postition).getTitle().equals("Display")){
                    //start NewActivity with title for actionbar and text for textview
                    Intent intent = new Intent(mContext, NewActivity.class);
                    intent.putExtra("actionBarTitle", "Display");
                    intent.putExtra("contentTv", "This is Display detail...");
                    mContext.startActivity(intent);
                }
                if (modellist.get(postition).getTitle().equals("Memory")){
                    //start NewActivity with title for actionbar and text for textview
                    Intent intent = new Intent(mContext, NewActivity.class);
                    intent.putExtra("actionBarTitle", "Memory");
                    intent.putExtra("contentTv", "This is Memory detail...");
                    mContext.startActivity(intent);
                }
                if (modellist.get(postition).getTitle().equals("Sensor")){
                    //start NewActivity with title for actionbar and text for textview
                    Intent intent = new Intent(mContext, NewActivity.class);
                    intent.putExtra("actionBarTitle", "Sensor");
                    intent.putExtra("contentTv", "This is Sensor detail...");
                    mContext.startActivity(intent);
                }
            }
        });


        return view;
    }

    //filter
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modellist.clear();
        if (charText.length()==0){
            modellist.addAll(arrayList);
        }
        else {
            for (Model model : arrayList){
                if (model.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)){
                    modellist.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
