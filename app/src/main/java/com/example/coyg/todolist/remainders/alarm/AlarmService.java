package com.example.coyg.todolist.remainders.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.coyg.todolist.MainActivity;
import com.example.coyg.todolist.R;
import com.example.coyg.todolist.notes.AddNoteActivity;


public class AlarmService extends IntentService
{
    private NotificationManager alarmNotificationManager;

    public AlarmService()
    {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent)
    {
        sendNotification("Wake Up! Wake Up!");
    }

    private void sendNotification(String msg)
    {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AddNoteActivity.class), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID");

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_arrow_back_black_24dp)
                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle("Default notification")
                .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .setContentInfo("Info");

        notificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, notificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

}
