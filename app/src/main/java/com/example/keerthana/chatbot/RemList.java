package com.example.keerthana.chatbot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RemList extends AppCompatActivity {

    String query = "CREATE TABLE IF NOT EXISTS REMINDER(NAME VARCHAR PRIMARY KEY, DATE VARCHAR, TIME VARCHAR);";
    DbHelper dh;
    SQLiteDatabase write, read;
    Data_Provider dp;
    EditText nameIn, dateIn, timeIn, dname;
    FragmentManager fm;
    FragmentTransaction ft;
    Cursor c;
    FrameLayout fl;
    View da;
    LayoutInflater inf;
    Switch chB;
    ScheduleService scheduleClient;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Date dte;
    TextView t1;
    Switch alDay, rec;
    TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rem_list);
        getWindow().setEnterTransition(new Explode());
        Intent in = getIntent();
        dh = new DbHelper(this, query);
        write = dh.getWritableDatabase();
        fm = getSupportFragmentManager();
        fl = (FrameLayout) findViewById(R.id.frameLayout);
        alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        inf = getLayoutInflater();
        ft = fm.beginTransaction();
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.UK);
            }
        });
        ft.add(R.id.frameLayout, new Display_data());
        ft.commit();
        getWindow().setExitTransition(new Explode());
    }

    public void insert(final View view) {
        final View enData = inf.inflate(R.layout.fragment_enter_data, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(RemList.this).create();
        final LayoutInflater inflater = (RemList.this).getLayoutInflater();
        alertDialog.setTitle("Insert Reminder:");
        alertDialog.setView(enData);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Insert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name, date, time, bd;
                Calendar cal = Calendar.getInstance();

                nameIn = (EditText) enData.findViewById(R.id.editText);
                dateIn = (EditText) enData.findViewById(R.id.editText2);
                timeIn = (EditText) enData.findViewById(R.id.editText3);
                alDay = (Switch) enData.findViewById(R.id.switch1);
                rec = (Switch) enData.findViewById(R.id.switch2);
                boolean allDay = alDay.isChecked();
                boolean recc = rec.isChecked();
                name = nameIn.getText().toString();
                date = dateIn.getText().toString();
                time = timeIn.getText().toString();
                String[] d = date.split("/");
                String[] t = time.split(":");
                Calendar c = new GregorianCalendar();
                //if(name.isEmpty() || date.isEmpty() || (time.isEmpty() && allDay == false)){
                //    ttobj.speak("How can I remind you about nothing?", TextToSpeech.QUEUE_FLUSH, null);
                //    insert(view);
                //}
                //if(time.isEmpty()){
                //    c.set(Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0]));
                //}else {
                c.set(Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0]), Integer.parseInt(t[0]), Integer.parseInt(t[1]), 0);
                //}
                if(c.getTimeInMillis() < cal.getTimeInMillis())
                {
                    Toast.makeText(getApplicationContext(), "How can I remind you for something that's already done?", Toast.LENGTH_LONG).show();
                    ttobj.speak("How can I remind you for something that's already done?", TextToSpeech.QUEUE_FLUSH, null);
                    dialog.dismiss();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
                intent.putExtra(CalendarContract.Events.TITLE, "Reminder");
                intent.putExtra(CalendarContract.Events.DESCRIPTION, name);
                if(recc){
                    intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");}
                startActivity(intent);
                ttobj.speak("I set a reminder on " + d[0] + "/" + d[1] + "/" + d[2] + " for " + name + " at " + time, TextToSpeech.QUEUE_FLUSH, null);
                //AlarmManager alarmManager = (AlarmManager) getSystemService(RemList.this.ALARM_SERVICE);
                //Intent intent = new Intent(RemList.this, ScheduleService.class);
                //intent.putExtra("Title", name);
                //intent.putExtra("Date", c);
                //PendingIntent alarmIntent = PendingIntent.getBroadcast(RemList.this, 0, intent, 0);
                //alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);
                NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(RemList.this)
                        .setSmallIcon(R.mipmap.chatbot)
                        .setContentTitle("Hey")
                        .setContentText(String.valueOf(c.getTimeInMillis()))
                        .setContentText( "I set a reminder set for: " + d[0] + "/" + d[1] + "/" + d[2])
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                android.app.NotificationManager notificationManager =
                        (android.app.NotificationManager) RemList.this.getSystemService(RemList.this.NOTIFICATION_SERVICE);
                notificationManager.notify(0 , notificationBuilder.build());
                dh.add(name, date, time, write);
                dialog.dismiss();
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.frameLayout, new Display_data());
                ft.commit();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "Okay Chill", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

;
    public void delRem(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(RemList.this).create();
        final LayoutInflater inflater = (RemList.this).getLayoutInflater();
        alertDialog.setTitle("Delete");
        alertDialog.setView(inflater.inflate(R.layout.del_data, null));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dname = (EditText) alertDialog.findViewById(R.id.editText4);
                dh.delete(write, dname.getText().toString());
                dialog.dismiss();
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.frameLayout, new Display_data());
                ft.commit();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "Okay Chill", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}