package com.example.keerthana.chatbot;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Enter_data extends Fragment {
    String query = "CREATE TABLE IF NOT EXISTS REMINDERS(NAME VARCHAR PRIMARY KEY, DATE VARCHAR, TIME VARCHAR);";
    DbHelper dh;
    SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View r= inflater.inflate(R.layout.fragment_enter_data, container, false);
        return r;
    }


}