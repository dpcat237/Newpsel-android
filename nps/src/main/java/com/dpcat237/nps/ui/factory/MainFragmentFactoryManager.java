package com.dpcat237.nps.ui.factory;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;

public class MainFragmentFactoryManager {
    private static final String TAG = "NPS:MainFragmentFactoryManager";

    public Boolean prepareView(int item, Activity activity, ListView listView) {
        Boolean error = false;
        MainFragmentManager fragmentManager;

        fragmentManager = MainFragmentFactory.createManager(item);
        try {
            fragmentManager.setup(activity, listView);
            fragmentManager.prepareAdapter();
            fragmentManager.finish();
        } catch (Exception e) {
            error = true;
            Log.d(TAG, "tut: Exception "+e.getMessage());
        }

        return error;
    }


}
