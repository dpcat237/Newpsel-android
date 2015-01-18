package com.dpcat237.nps.ui.factory;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentDictationItems;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentFeedsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentItemsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentLabelsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentLaterItemsManager;
import com.dpcat237.nps.ui.factory.mainActivityFragmentManager.MainFragmentManager;

import java.util.Arrays;

public class MainFragmentFactory {
    public static final Integer[] syncArray = {0,1,2};

    public static MainFragmentManager createManager(int item) {
        MainFragmentManager fragmentManager = null;

        if (item == MainActivityConstants.DRAWER_MAIN_ITEMS) {
            fragmentManager = new MainFragmentItemsManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS) {
            fragmentManager = new MainFragmentDictationItems();
        } else if (item == MainActivityConstants.DRAWER_MAIN_LATER_ITEMS) {
            fragmentManager = new MainFragmentLaterItemsManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_MANAGE_FEEDS) {
            fragmentManager = new MainFragmentFeedsManager();
        } else if (item == MainActivityConstants.DRAWER_MAIN_MANAGE_LABELS) {
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

    public static void showRequiredMenuItems(Context context, Menu menu, int position, Boolean itemsActivated) {
        MenuItem buttonAddFeed = menu.findItem(R.id.buttonAddFeed);
        MenuItem buttonSync = menu.findItem(R.id.buttonSync);
        MenuItem buttonDictate = menu.findItem(R.id.buttonDictate);
        MenuItem buttonCreateLabel = menu.findItem(R.id.buttonCreateLabel);

        buttonAddFeed.setVisible(false);
        buttonSync.setVisible(false);
        buttonDictate.setVisible(false);
        buttonCreateLabel.setVisible(false);

        switch (position) {
            case MainActivityConstants.DRAWER_MAIN_ITEMS:
                showAddFeedButton(context, buttonAddFeed, itemsActivated);
                buttonSync.setVisible(true);
                break;
            case MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS:
            case MainActivityConstants.DRAWER_MAIN_LATER_ITEMS:
                DictateItemRepository dictateRepo = new DictateItemRepository(context);
                dictateRepo.open();
                if (dictateRepo.countUnreadGrabberItems() > 0) {
                    buttonDictate.setVisible(true);
                }
                dictateRepo.close();
                break;
            case MainActivityConstants.DRAWER_MAIN_MANAGE_FEEDS:
                showAddFeedButton(context, buttonAddFeed, itemsActivated);
                break;
            case MainActivityConstants.DRAWER_MAIN_MANAGE_LABELS:
                buttonCreateLabel.setVisible(true);
                break;
        }
    }

    private static void showAddFeedButton(Context context, MenuItem buttonAddFeed, Boolean itemsActivated) {
        if (itemsActivated && ConnectionHelper.hasConnection(context)) {
            buttonAddFeed.setVisible(true);
        }
    }

    public static Boolean necessarySync(int position)
    {
        return Arrays.asList(syncArray).contains(position);
    }
}