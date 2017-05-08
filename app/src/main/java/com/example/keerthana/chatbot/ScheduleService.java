package com.example.keerthana.chatbot;
import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by keerthana on 22-04-2017.
 */

public class ScheduleService extends Service {

    /**
     * Class for clients to access
     */
    private Calendar cal;
    private Calendar date;
    public class ServiceBinder extends Binder {
        ScheduleService getService() {
            return ScheduleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
        date = (Calendar)intent.getSerializableExtra("Date");
        this.setAlarm(date, intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients. See
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Show an alarm for a certain date when the alarm is called it will pop up a notification
     */
    public void setAlarm(Calendar c, Intent i) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        Log.e("Alarm", "Set Alarm is running");
            AlarmTask at = new AlarmTask(this, c);
            at.onReceive(this,i);
            Log.e("Alarm", "Reminding");
    }
}