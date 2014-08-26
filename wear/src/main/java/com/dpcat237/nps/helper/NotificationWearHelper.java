package com.dpcat237.nps.helper;

import android.content.Context;
import android.widget.Toast;

public class NotificationWearHelper {
    public static void showSimpleToast(Context context, String message) {
        if (message.length() < 3) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
