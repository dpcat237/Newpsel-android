package com.dpcat237.nps.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.dpcat237.nps.R;
import com.dpcat237.nps.activity.MainActivity;

public class NotificationHelper {
    public static final int NOTIFICATION_ID = 1;

    public static Boolean areNotificationActivated(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean activated = pref.getBoolean("pref_notifications_enable", false);

        return activated;
    }

    public static void showSimpleNotification(Context context, String msg) {
        if (!NotificationHelper.areNotificationActivated(context)) {
            return;
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(context.getString(R.string.nt_download))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
