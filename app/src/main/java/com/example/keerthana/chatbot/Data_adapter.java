package com.example.keerthana.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Data_adapter extends ArrayAdapter {
    List list = new ArrayList();
    LayoutInflater li;
    public Data_adapter(Context context, int resource) {
        super(context, resource);
    }
    static class ViewHolder{
        TextView name;
        TextView date;
        TextView time;
    }

    @Override
    public void add(Object object) {
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if(row == null){
            li = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = li.inflate(R.layout.row,parent,false);
            holder.name = (TextView)row.findViewById(R.id.textView3);
            holder.date = (TextView)row.findViewById(R.id.textView4);
            holder.time = (TextView)row.findViewById(R.id.textView5);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }
        Data_Provider dp = (Data_Provider) this.getItem(position);
        holder.name.setText(dp.getName());
        holder.date.setText(dp.getDate());
        holder.time.setText(dp.getTime());
        return row;
    }
}