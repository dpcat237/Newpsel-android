package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.MainActivityConstants;

public class MainFragmentUnreadManager extends MainFragmentFeedsManager {
    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_item));
    }

    protected void getItems() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        if (preferences.getBoolean("pref_feeds_only_unread", true)) {
            items = feedRepo.getAllFeedsUnread();
        } else {
            items = feedRepo.getAllFeedsWithItems();
        }
    }
}