package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import android.content.Intent;

import com.dpcat237.nps.R;
import com.dpcat237.nps.common.model.ListItem;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.ui.activity.DictateItemActivity;

public class MainFragmentDictationItems extends MainFragmentItems {
    private static final String TAG = "NPS:MainFragmentDictationItems";


    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_dictations_items));
    }

    protected void getItems() {
        items = dictateRepo.getGrabbedItems(preferences.getBoolean("pref_dictations_only_unread", true));
    }

    protected void showNextPage(ListItem item) {
        DictateItemRepository itemRepo = new DictateItemRepository(mActivity);
        SongRepository songRepo = new SongRepository(mActivity);
        itemRepo.open();
        songRepo.open();

        item.setIsUnread(false);
        itemRepo.readItem(item.getItemApiId(), false);
        songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
        itemRepo.close();
        songRepo.close();

        Intent intent = new Intent(mActivity, DictateItemActivity.class);
        intent.putExtra(ItemConstants.ITEM_API_ID, item.getItemApiId());
        mActivity.startActivity(intent);
    }
}