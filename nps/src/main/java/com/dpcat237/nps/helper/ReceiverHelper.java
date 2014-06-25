package com.dpcat237.nps.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.dpcat237.nps.receiver.AlarmReceiver;
import com.dpcat237.nps.receiver.BootReceiver;

import java.util.Locale;

public class ReceiverHelper {
    private static final String TAG = "NPS:ReceiverHelper";

    public static void enableBootReceiver(Context context) {
        Log.d(TAG, "tut: enableBootReceiver");
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(context);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        AlarmReceiver alarm = new AlarmReceiver();
        alarm.cancelAlarm(context);
    }
}
