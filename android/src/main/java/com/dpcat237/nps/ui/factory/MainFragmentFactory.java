package com.dpcat237.nps.ui.factory;

import android.content.Context;
import android.view.MenuItem;

import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentDictationItemsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentLabelsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentUnreadManager;

public class MainFragmentFactory {
    public static MainFragmentManager createManager(int item) {
        MainFragmentManager fragmentManager = null;

        if (item == MainActivityConstants.DRAWER_MAIN_ITEMS) {
            fragmentManager = new MainFragmentUnreadManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS) {
            fragmentManager = new MainFragmentDictationItemsManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_LATER_ITEMS) {
            fragmentManager = new MainFragmentLabelsManager();
        }

        return fragmentManager;
    }

    public static void updateItemsCount(Context context, int position) {
        if (position == MainActivityConstants.DRAWER_MAIN_ITEMS) {
            FeedRepository feedRepo = new FeedRepository(context);
            feedRepo.open();
            feedRepo.unreadCountUpdate();
            feedRepo.close();
        }

        if (position == MainActivityConstants.DRAWER_MAIN_LATER_ITEMS) {
            LabelRepository labelRepo = new LabelRepository(context);
            labelRepo.open();
            labelRepo.unreadCountUpdate();
            labelRepo.close();
        }
    }

    public static void showSyncButton(int position, MenuItem buttonSync) {
        if (position == MainActivityConstants.DRAWER_MAIN_ITEMS) {
            buttonSync.setVisible(true);
        } else {
            buttonSync.setVisible(false);
        }
    }
}