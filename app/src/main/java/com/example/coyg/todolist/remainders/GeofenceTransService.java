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
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//
//        // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            CharSequence name = "TODOLIST";
//            // Create the channel for the notification
//            NotificationChannel mChannel =
//                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Set the Notification Channel for the Notification Manager.
//            notificationManager.createNotificationChannel(mChannel);
//        }
//
//        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//
//
//        // Build your notification here
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.mipmap.sym_def_app_icon));
//        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
//        builder.setContentTitle("REACH!");
//        builder.setContentText("YOU HAVE REACHED IT!");
//        builder.setContentIntent(notificationPendingIntent);
//        builder.setColor(Color.RED);
//
//        // Set the Channel ID for Android O.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setChannelId(CHANNEL_ID); // Channel ID
//        }
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Launch notification
//        startForeground(NOTIFICATION_ID, builder.build());
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

//        notificationManager.cancel(NOTIFICATION_ID);
    }
}
