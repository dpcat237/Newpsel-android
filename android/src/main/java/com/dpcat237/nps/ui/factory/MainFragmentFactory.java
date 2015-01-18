package com.dpcat237.nps.ui.factory;

import android.content.Context;
import android.view.MenuItem;

import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentDictationItems;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentFeedsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentLabelsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentUnread;

import java.util.Arrays;

public class MainFragmentFactory {
    public static final Integer[] syncArray = {0,1,2};

    public static MainFragmentManager createManager(int item) {
        MainFragmentManager fragmentManager = null;

        if (item == MainActivityConstants.DRAWER_MAIN_ITEMS) {
            fragmentManager = new MainFragmentUnread();
        } else if (item == MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS) {
            fragmentManager = new MainFragmentDictationItems();
        } else if (item == MainActivityConstants.DRAWER_MAIN_LATER_ITEMS) {
            fragmentManager = new MainFragmentLabelsManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_MANAGE_FEEDS) {
            fragmentManager = new MainFragmentFeedsManager();
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

    public static Boolean necessarySync(int position)
    {
        return Arrays.asList(syncArray).contains(position);
    }
}