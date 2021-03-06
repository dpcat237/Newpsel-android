package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.MainActivityConstants;

public class MainFragmentItemsManager extends MainFragmentFeeds {
    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_item));
    }

    protected void getItems() {
        if (preferences.getBoolean("pref_feeds_only_unread", true)) {
            items = feedRepo.getAllFeedsUnread();
        } else {
            items = feedRepo.getAllFeedsWithItems();
        }
    }
}