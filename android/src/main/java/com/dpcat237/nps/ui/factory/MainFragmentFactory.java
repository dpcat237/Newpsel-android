package com.dpcat237.nps.ui.factory;

import android.content.Context;
import android.view.MenuItem;

import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentAllManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentDictationItemsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentLabelsManager;
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
            fragmentManager = new MainFragmentDictationItemsManager();
        }
        if (item == MainActivityConstants.DRAWER_ITEM_LATER_ITEMS) {
            fragmentManager = new MainFragmentLabelsManager();
        }

        return fragmentManager;
    }

    public static void updateItemsCount(Context context, int position) {
        if (position == MainActivityConstants.DRAWER_ITEM_UNREAD_ITEMS || position == MainActivityConstants.DRAWER_ITEM_ALL_ITEMS) {
            FeedRepository feedRepo = new FeedRepository(context);
            feedRepo.open();
            feedRepo.unreadCountUpdate();
            feedRepo.close();
        }

        if (position == MainActivityConstants.DRAWER_ITEM_LATER_ITEMS) {
            LabelRepository labelRepo = new LabelRepository(context);
            labelRepo.open();
            labelRepo.unreadCountUpdate();
            labelRepo.close();
        }
    }

    public static void showSyncButton(int position, MenuItem buttonSync) {

        if (position == MainActivityConstants.DRAWER_ITEM_UNREAD_ITEMS || position == MainActivityConstants.DRAWER_ITEM_ALL_ITEMS) {
            buttonSync.setVisible(true);
        } else {
            buttonSync.setVisible(false);
        }
    }
}