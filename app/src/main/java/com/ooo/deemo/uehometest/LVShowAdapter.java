package com.ooo.deemo.uehometest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Author by Deemo, Date on 2019/5/6.
 * Have a good day
 */
public class LVShowAdapter extends ArrayAdapter<ShowContent> {

    private int resourceId;

    public LVShowAdapter( Context context,  int textViewResourceId,  // 子项布局的id
                          List<ShowContent> objects) {



        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,  @NonNull ViewGroup parent) {
ShowContent showContent = getItem(position);

        Log.e("LVShowAdapter","getView1");

View view ;

ViewHolder viewHolder;
if(convertView==null){
    Log.e("LVShowAdapter","getView2");
    view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
    viewHolder = new ViewHolder();
    viewHolder.tv_title = view.findViewById(R.id.tv_show_onlv1);
    viewHolder.tv_content = view.findViewById(R.id.tv_show_onlv2);

}else {
    Log.e("LVShowAdapter","getView3");
    view = convertView;
    viewHolder = (ViewHolder) view.getTag();
}


        viewHolder.tv_title.setText(showContent.getID());
        viewHolder.tv_content.setText(showContent.getContent());


        return view;
    }


    class ViewHolder{
        TextView tv_title;

        TextView tv_content;
    }
}
