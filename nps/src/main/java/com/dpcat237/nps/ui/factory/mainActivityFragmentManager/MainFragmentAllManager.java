package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.MainActivityConstants;

public class MainFragmentAllManager extends MainFragmentFeedsManager {
    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_ITEM_ALL_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_item_all));
    }

    protected void getItems() {
        items = feedRepo.getAllFeedsWithItems();
    }
}