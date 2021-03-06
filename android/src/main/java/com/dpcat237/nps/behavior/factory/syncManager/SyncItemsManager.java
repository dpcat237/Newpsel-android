package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncItemsManager extends SyncManager {
    private static final String TAG = "NPS:SyncItemsManager";
    private Integer itemsSyncLimit = 300;
    private ItemRepository itemRepo;
    private JSONArray viewedItems;
    private Item[] items;


    protected void openDB() {
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }

    protected void closeDB() {
        itemRepo.close();
    }

    protected void checkNecessarySync() {
        if (!preferences.getBoolean("pref_items_download_enable", true)) {
            error = true;

            return;
        }

        Integer unreadCount = itemRepo.countUnreadItems();
        if (unreadCount >= itemsSyncLimit) {
            error = true;

            return;
        }
        itemsSyncLimit = ((itemsSyncLimit - unreadCount) < 10)? 10 : (itemsSyncLimit - unreadCount);
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();
        viewedItems = itemRepo.getItemsToSync();

        try {
            jsonData.put(ApiConstants.DEVICE_ID, PreferencesHelper.generateKey(mContext));
            jsonData.put("items", viewedItems);
            jsonData.put("limit", itemsSyncLimit);
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_ITEMS_UNREAD, jsonData);
        error = (Boolean) result.get("error");

        if (!error) {
            items = (Item[]) result.get("items");
        }
    }

    protected Map<String, Object> getDownloadedItems() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("items", items);

        return result;
    }

    protected void processSpecificData() {
        if (items.length < 1) {
            return;
        }

        SongRepository songRepo = new SongRepository(mContext);
        songRepo.open();
        for (Item item : items) {
            if (item.isUnread()) {
                //Log.d(TAG, "tut: addItem "+item.getApiId()+" - "+item.getTitle());
                itemRepo.addItem(item);
            } else {
                deleteItem(songRepo, item.getApiId(), item.getItemApiId());
            }
        }
        songRepo.close();
    }

    private void deleteItem(SongRepository songRepo, Integer apiId, Integer itemApiId) {
        if (itemApiId.equals(PreferencesHelper.getIntPreference(mContext, PreferenceConstants.ITEM_NOW_OPENED))) {
            return;
        }

        //Log.d(TAG, "tut: deleteItem "+item.getApiId()+" - "+item.getTitle());
        itemRepo.deleteItem(apiId);
        songRepo.markAsPlayed(itemApiId, SongConstants.GRABBER_TYPE_TITLE, true);
    }

    protected void beforeFinish() {
        if (error) {
            return;
        }

        //remove read items
        if (viewedItems.length() > 0) {
            itemRepo.removeReadItems();
        }

        if (items.length > 1) {
            PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.ITEMS_ARE_NEW, true);
        }

        //update feeds items count
        FeedRepository feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        feedRepo.unreadCountUpdate();
        feedRepo.close();
    }
}