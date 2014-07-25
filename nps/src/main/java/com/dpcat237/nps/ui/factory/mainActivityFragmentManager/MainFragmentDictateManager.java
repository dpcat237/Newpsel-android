package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import android.content.Intent;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.ui.activity.DictateItemActivity;

public class MainFragmentDictateManager extends MainFragmentItemsManager {
    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_ITEM_DICTATE_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_item_dictate));
    }

    protected void getItems() {
        items = dictateRepo.getUnreadItems();
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
        PreferencesHelper.setCurrentItemApiId(mActivity, 0);
        mActivity.startActivity(intent);
    }
}