package com.example.keerthana.chatbot;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Display_data extends Fragment {
    ListView lv;
    Cursor c;
    DbHelper dh;
    Data_Provider dp;
    SQLiteDatabase read;
    String name, date, time;
    Data_adapter da;
    String query = "CREATE TABLE IF NOT EXISTS REMINDER(NAME VARCHAR PRIMARY KEY, DATE VARCHAR, TIME VARCHAR);";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.fragment_display_data, container, false);
        dh = new DbHelper(r.getContext(),query);
        da = new Data_adapter(r.getContext(),R.layout.row);
        read = dh.getReadableDatabase();
        lv = (ListView)r.findViewById(R.id.listView);
        c = dh.get(read);
        while (c.moveToNext()){
            name = c.getString(0);
            date = c.getString(1);
            time = c.getString(2);
            dp = new Data_Provider(name, date, time);
            da.add(dp);
            Log.e("Adapter","Inserted in Adapter " + da.getCount());
        }
        lv.setAdapter(da);
        return r;
    }
}