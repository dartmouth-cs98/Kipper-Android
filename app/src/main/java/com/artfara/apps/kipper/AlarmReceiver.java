package com.artfara.apps.kipper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Joe Connolly on 11/5/16.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver
{
    private static final String TAG = " AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Utils.startAlarmTrackingService(context);
        }

        Intent myService = new Intent(context, TrackingIntentService.class);
        startWakefulService(context, myService);
//        Log.d(TAG, "called " + intent.getAction());

    }
}