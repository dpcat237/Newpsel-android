package com.dpcat237.nps.ui.factory;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;

public class MainFragmentFactoryManager {
    private static final String TAG = "NPS:MainFragmentFactoryManager";

    public Integer prepareView(int item, Activity activity, ListView listView) {
        Integer countItems = 0;
        MainFragmentManager fragmentManager;

        fragmentManager = MainFragmentFactory.createManager(item);
        try {
            fragmentManager.setup(activity, listView);
            countItems = fragmentManager.prepareAdapter();
            fragmentManager.finish();
        } catch (Exception e) {
            Log.d(TAG, "tut: Exception "+e.getMessage());
        }

        return countItems;
    }


}
