package com.dpcat237.nps.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.dpcat237.nps.behavior.receiver.AlarmRemoveOldReceiver;
import com.dpcat237.nps.behavior.receiver.AlarmSyncDictationsReceiver;
import com.dpcat237.nps.behavior.receiver.AlarmSyncNewsReceiver;
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

    private static void enableAlarms(Context context) {
        AlarmSyncNewsReceiver alarmSyncNews = new AlarmSyncNewsReceiver();
        alarmSyncNews.setAlarm(context);

        AlarmSyncDictationsReceiver alarmSyncDictations = new AlarmSyncDictationsReceiver();
        alarmSyncDictations.setAlarm(context);

        AlarmRemoveOldReceiver alarmRemoveOld = new AlarmRemoveOldReceiver();
        alarmRemoveOld.setAlarm(context);
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
        AlarmSyncNewsReceiver alarmSyncNews = new AlarmSyncNewsReceiver();
        alarmSyncNews.cancelAlarm(context);

        AlarmSyncDictationsReceiver alarmSyncDictations = new AlarmSyncDictationsReceiver();
        alarmSyncDictations.cancelAlarm(context);

        AlarmRemoveOldReceiver alarmRemoveOld = new AlarmRemoveOldReceiver();
        alarmRemoveOld.cancelAlarm(context);
    }
}
