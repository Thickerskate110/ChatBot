package com.example.keerthana.chatbot;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ADARSH on 02-04-2017.
 */
public class Data_Provider {
    private String name;
    private String date;
    private String time;

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime(){
        return time;
    }


    public Data_Provider(String name,String date,String time){
        this.name = name;
        this.date = date;
        this.time = time;
    }
}