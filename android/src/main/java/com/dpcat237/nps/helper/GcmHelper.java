package com.dpcat237.nps.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.dpcat237.nps.behavior.task.GcmIdUpdateTask;

public class GcmHelper {
    private static final String TAG = "NPS:GcmHelper";
    private static final String GCM_KEY = "google_central_messaging_key";
    private static final String GCM_APP_VERSION = "google_central_messaging_app_version";

    public static String getRegId(Context context) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        String regId = userPref.getString(GCM_KEY, "");
        int registeredVersion = userPref.getInt(GCM_APP_VERSION, 1);
        int currentVersion = GoogleServicesHelper.getAppVersion(context);

        if (!regId.isEmpty() && registeredVersion == currentVersion) {
            return regId;
        }

        return "";
    }

    public static void setRegId(Context context, String regId) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();

        //set new key
        editor.putString(GCM_KEY, regId);
        //update version
        editor.putInt(GCM_APP_VERSION, GoogleServicesHelper.getAppVersion(context));
        editor.apply();
    }

    public static void checkRegId(Context context) {
        if (getRegId(context).isEmpty()) {
            GcmIdUpdateTask task = new GcmIdUpdateTask(context);
            task.execute();
        }
    }
}
