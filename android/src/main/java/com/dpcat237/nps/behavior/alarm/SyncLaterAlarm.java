package com.dpcat237.nps.behavior.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.dpcat237.nps.behavior.receiver.BootReceiver;
import com.dpcat237.nps.behavior.service.SyncLaterService;
import com.dpcat237.nps.behavior.service.SyncNewsService;
import com.dpcat237.nps.helper.LoginHelper;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class SyncLaterAlarm extends WakefulBroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private static final String TAG = "NPS:SyncLaterAlarm";
  
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!LoginHelper.checkLogged(context)) {
            cancelAlarm(context);
        }

        Intent syncLaterService = new Intent(context, SyncLaterService.class);
        startWakefulService(context, syncLaterService);
    }

    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SyncLaterAlarm.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //https://developer.android.com/training/scheduling/alarms.html
        Integer interval = 15*60; //15 minutes
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, interval * 1000, alarmIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
