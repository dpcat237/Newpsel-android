package com.dpcat237.nps.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastHelper {
    public static void launchBroadcast(Context context, String intentType, String messageType, String message) {
        Intent intent = new Intent(intentType);
        intent.putExtra(messageType, message);
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
        broadcaster.sendBroadcast(intent);
    }
}
