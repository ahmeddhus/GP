package com.example.coyg.todolist.remainders;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.coyg.todolist.R;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.widget.Button;
import android.widget.Toast;

public class Ringing extends AppCompatActivity
{
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    Button close_alarm;
    MediaPlayer mp;
    Uri notification;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        close_alarm = findViewById(R.id.close_alarm);
        geofencingClient = LocationServices.getGeofencingClient (this);

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();

        close_alarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mp.stop();
                mp.release();
                stopGeofence();
                finish();
                System.exit(0);
            }
        });
    }

    private PendingIntent getGeofencePendingIntent()
    {
        if (geofencePendingIntent != null)
            return geofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransService.class);

        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }

    private void stopGeofence()
    {
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText (Ringing.this, "REMOVED", Toast.LENGTH_LONG).show ();

                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText (Ringing.this, "onFailure", Toast.LENGTH_LONG).show ();
                    }
                });
    }
}
