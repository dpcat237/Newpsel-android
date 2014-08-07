package com.dpcat237.nps.behavior.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dpcat237.nps.behavior.alarm.RemoveOldAlarm;
import com.dpcat237.nps.behavior.alarm.SyncDictationsAlarm;
import com.dpcat237.nps.behavior.alarm.SyncLaterAlarm;
import com.dpcat237.nps.behavior.alarm.SyncNewsAlarm;
import com.dpcat237.nps.helper.LoginHelper;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
public class BootReceiver extends BroadcastReceiver {
    private SyncNewsAlarm syncNewsAlarm = new SyncNewsAlarm();
    private SyncDictationsAlarm syncDictationsAlarm = new SyncDictationsAlarm();
    private SyncLaterAlarm syncLaterAlarm = new SyncLaterAlarm();
    private RemoveOldAlarm removeOldAlarm = new RemoveOldAlarm();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (!LoginHelper.checkLogged(context)) {
                return;
            }

            syncNewsAlarm.setAlarm(context);
            syncDictationsAlarm.setAlarm(context);
            syncLaterAlarm.setAlarm(context);
            removeOldAlarm.setAlarm(context);
        }
    }
}