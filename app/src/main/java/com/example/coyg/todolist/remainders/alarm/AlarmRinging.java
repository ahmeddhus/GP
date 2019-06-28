package com.example.coyg.todolist.remainders.alarm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.coyg.todolist.MainActivity;
import com.example.coyg.todolist.R;

public class AlarmRinging extends AppCompatActivity
{
    Button close_alarm;
    MediaPlayer mp;
    Uri notification;
    TextView alarmtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_alarm_ringing);

        close_alarm = findViewById(R.id.close_alarm);
        alarmtxt = findViewById (R.id.alarmtxt);

        Intent intent = getIntent ();
        String theNote = intent.getExtras ().getString ("theNote");
        alarmtxt.setText (theNote);

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
                finish();

                Intent goToMain = new Intent (AlarmRinging.this, MainActivity.class);
                startActivity (goToMain);
            }
        });
    }
}
