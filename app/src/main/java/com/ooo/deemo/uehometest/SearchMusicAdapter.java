package com.ooo.deemo.uehometest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dfzt.olinemusic.entity.Music;


import java.util.List;

public class SearchMusicAdapter extends BaseAdapter {

    private Context context;
    private List<Music> list ;

    public SearchMusicAdapter(Context context){
        this.context = context;
    }

    public void setList(List<Music> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_music_item,parent,false);
            holder.name = convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(list.get(position).getTitle() + " - " +list.get(position).getArtist());

//        holder.name.setText("11111");
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
