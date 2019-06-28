package com.example.coyg.todolist.remainders.alarm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String theNote = intent.getExtras ().getString ("note");

        Intent goToAlarm = new Intent (context, AlarmRinging.class);
        goToAlarm.putExtra ("theNote", theNote);
        context.startActivity (goToAlarm);

        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
