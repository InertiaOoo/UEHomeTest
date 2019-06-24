package com.ooo.deemo.uehometest;

import android.content.Context;
import android.support.annotation.NonNull;
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
public class ShowListAdapter extends RecyclerView.Adapter<ShowListAdapter.ViewHolder>  {
private List<ShowContent> contentList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_show_layout,viewGroup,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ShowContent showContent = contentList.get(i);
        viewHolder.tv_show_onlist1.setText(showContent.getID());
        viewHolder.tv_show_onlist2.setText(showContent.getContent());

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position, @NonNull List<Object> payloads) {

        if(payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else {
            String payload = (String) payloads.get(0);
            ShowContent scontent = contentList.get(position);
            ViewHolder viewHolder = (ShowListAdapter.ViewHolder) holder;
            viewHolder.tv_show_onlist1.setText(scontent.getID());
            viewHolder.tv_show_onlist2.setText(scontent.getContent());
        }
    }

    @Override
    public int getItemCount() {


        return contentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_show_onlist1;
        TextView tv_show_onlist2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_show_onlist1 = itemView.findViewById(R.id.tv_show_onlist1);

            tv_show_onlist2 = itemView.findViewById(R.id.tv_show_onlist2);
        }
    }


    public ShowListAdapter( List<ShowContent> contentList){
        this.contentList = contentList;
    }
}
