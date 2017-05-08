package com.example.keerthana.chatbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    Context context;
    String query;

    public DbHelper(Context context,String query) {
        super(context,"periodical2", null, 1);
        this.context = context;
        this.query = query;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
        Log.e("DataBase", "Table Created...");
    }
    public void add(String name, String date, String time, SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("Date", date);
        cv.put("Time", time);
        db.insert("Reminder",null,cv);
        Log.e("Database","1 row inserted");
    }
    public Cursor get(SQLiteDatabase db){
        Cursor c;
        String[] reqCol = {"Name", "Date", "Time"};
        c = db.query("Reminder",reqCol,null,null,null,null,null);
        return c;
    }
    public void delete(SQLiteDatabase db, String name){
        db.delete("Reminder","Name = '" + name + "'",null);
        Log.e("Database","1 row Deleted");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}