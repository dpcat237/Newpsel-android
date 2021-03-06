package com.dpcat237.nps.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.dpcat237.nps.behavior.alarm.AlarmsControlAlarm;
import com.dpcat237.nps.behavior.alarm.GcmUpdateIdAlarm;
import com.dpcat237.nps.behavior.alarm.RemoveOldAlarm;
import com.dpcat237.nps.behavior.alarm.SyncDictationsAlarm;
import com.dpcat237.nps.behavior.alarm.SyncLaterAlarm;
import com.dpcat237.nps.behavior.alarm.SyncNewsAlarm;
import com.dpcat237.nps.behavior.receiver.BootReceiver;

public class ReceiverHelper {
    private static final String TAG = "NPS:ReceiverHelper";

    public static void enableBootReceiver(Context context) {
        Log.d(TAG, "tut: enableBootReceiver");
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        enableAlarms(context);
    }

    public static void enableAlarms(Context context) {
        Log.d(TAG, "tut: enableAlarms");
        SyncNewsAlarm syncNewsAlarm = new SyncNewsAlarm();
        syncNewsAlarm.setAlarm(context);

        SyncDictationsAlarm syncDictationsAlarm = new SyncDictationsAlarm();
        syncDictationsAlarm.setAlarm(context);

        SyncLaterAlarm syncLaterAlarm = new SyncLaterAlarm();
        syncLaterAlarm.setAlarm(context);

        RemoveOldAlarm removeOldAlarm = new RemoveOldAlarm();
        removeOldAlarm.setAlarm(context);

        GcmUpdateIdAlarm updateGcmIdAlarm = new GcmUpdateIdAlarm();
        updateGcmIdAlarm.setAlarm(context);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        cancelAlarms(context);
    }

    private static void cancelAlarms(Context context) {
        SyncNewsAlarm syncNewsAlarm = new SyncNewsAlarm();
        syncNewsAlarm.cancelAlarm(context);

        SyncDictationsAlarm syncDictationsAlarm = new SyncDictationsAlarm();
        syncDictationsAlarm.cancelAlarm(context);

        SyncLaterAlarm syncLaterAlarm = new SyncLaterAlarm();
        syncLaterAlarm.cancelAlarm(context);

        RemoveOldAlarm removeOldAlarm = new RemoveOldAlarm();
        removeOldAlarm.cancelAlarm(context);

        GcmUpdateIdAlarm updateGcmIdAlarm = new GcmUpdateIdAlarm();
        updateGcmIdAlarm.cancelAlarm(context);

        AlarmsControlAlarm alarmsControlAlarm = new AlarmsControlAlarm();
        alarmsControlAlarm.cancelAlarm(context);
    }
}
