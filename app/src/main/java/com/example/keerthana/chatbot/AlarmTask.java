package com.example.keerthana.chatbot;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by keerthana on 22-04-2017.
 */
public class AlarmTask extends BroadcastReceiver {
    private final Calendar date;
    Calendar cal;
    private final AlarmManager am;
    private final Context context;

    public AlarmTask(){
        this.context = null;
        this.am = null;
        this.date = null;
    }

    public AlarmTask(Context context, Calendar date) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        cal = Calendar.getInstance();
        //if(intent.getAction().equals("com.example.keerthana.chatbot.NOTIFICATION_ALARM")) {
            Log.d("Alarm Receiver", "onReceive called");
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.chatbot).setContentTitle("Reminder")
                    .setContentText(intent.getCharSequenceExtra("Title"));
            Intent resultIntent = new Intent(context, RemList.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(RemList.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notificationBuilder.build());
        //}
    }
}