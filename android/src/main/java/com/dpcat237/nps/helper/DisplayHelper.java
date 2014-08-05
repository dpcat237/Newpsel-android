package com.dpcat237.nps.helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class DisplayHelper {
    private static final String TAG = "NPS:DisplayHelper";

    public static Boolean isTablet(Context context) {
        try {
            // Compute screen size
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float screenWidth  = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
            // Tablet devices should have a screen size greater than 6 inches

            return size >= 6;
        } catch(Throwable t) {
            Log.e(TAG, "Failed to compute screen size", t);

            return false;
        }
    }
}
