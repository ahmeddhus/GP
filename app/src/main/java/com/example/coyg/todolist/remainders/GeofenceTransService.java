package com.example.coyg.todolist.remainders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
//import com.example.coyg.todolist.R;

import com.example.coyg.todolist.MainActivity;


public class GeofenceTransService extends Service
{
//    private static final String CHANNEL_ID = "channel_01";
//    private final static int NOTIFICATION_ID = 95;
//    private NotificationManager notificationManager;

    public GeofenceTransService()
    {
    }

    @Override
    public void onCreate()
    {
        Intent intent = new Intent(getApplicationContext(), Ringing.class);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }
}
