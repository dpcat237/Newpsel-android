package com.dpcat237.nps.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GoogleServicesHelper {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Context context, Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        boolean result = false;
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: // because of error: google play services updated but AVD not
                result = true;
                break;
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_DISABLED:
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
                break;
        }

        return result;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
