package com.dpcat237.nps.ui.factory;

import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentAllManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentDictateManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentUnreadManager;

public class MainFragmentFactory {
    public static MainFragmentManager createManager(int item) {
        MainFragmentManager fragmentManager = null;

        if (item == MainActivityConstants.DRAWER_ITEM_UNREAD_ITEMS) {
            fragmentManager = new MainFragmentUnreadManager();
        }

        if (item == MainActivityConstants.DRAWER_ITEM_ALL_ITEMS) {
            fragmentManager = new MainFragmentAllManager();
        }

        if (item == MainActivityConstants.DRAWER_ITEM_DICTATE_ITEMS) {
            fragmentManager = new MainFragmentDictateManager();
        }

        return fragmentManager;
    }
}