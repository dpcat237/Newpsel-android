package com.dpcat237.nps.behavior.factory.syncManager;


import android.util.Log;

import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.DictateItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncDictationItemsManager extends SyncManager {
    private static final String TAG = "NPS:SyncDictationItemsManager";
    private DictateItemRepository dictationRepo;
    private SongRepository songRepo;
    private Integer downloadQuantity;
    private DictateItem[] items;
    private Integer newCount = 0;

    protected void openDB() {
        dictationRepo = new DictateItemRepository(mContext);
        songRepo = new SongRepository(mContext);
        dictationRepo.open();
        songRepo.open();
    }

    protected void closeDB() {
        dictationRepo.close();
        songRepo.close();
    }

    protected void checkNecessarySync() {
        downloadQuantity = Integer.parseInt(preferences.getString("pref_dictation_quantity", "25"));
        Boolean sync = false;
        Integer unreadCount = dictationRepo.countUnreadItems();
        Integer lastSyncCount = PreferencesHelper.getLastSyncCount(mContext);

        if (unreadCount < downloadQuantity) {
            sync = true;
            downloadQuantity = ((downloadQuantity - unreadCount) < 10)? 10 : (downloadQuantity - unreadCount);
        }
        if (lastSyncCount > 0 && lastSyncCount.equals(unreadCount)) {
            sync = false;
        }
        Log.d(TAG, "tut: unreadCount "+unreadCount+" downloadQuantity "+downloadQuantity);

        if (!sync) {
            Log.d(TAG, "tut: arant necessary");
            error = true;
        }
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("items", dictationRepo.getItemsForSync());
            jsonData.put("limit", downloadQuantity);
        } catch (JSONException e) {
            Log.e(TAG, "tut:  "+e.getMessage());
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.TYPE_SYNC_DICTATE_ITEMS, jsonData);
        error = (Boolean) result.get("error");

        if (!error) {
            items = (DictateItem[]) result.get("items");
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

        newCount = 0;
        Log.d(TAG, "tut: downloaded items "+items.length);
        for (DictateItem item : items) {
            addRemoveItem(item);
        }
    }

    private void addRemoveItem(DictateItem item) {
        if (item.isUnread()) {
            if (!dictationRepo.checkItemExists(item.getApiId())) {
                try {
                    dictationRepo.addItem(item);
                    newCount++;
                    //Log.d(TAG, "tut: addItem: "+item.getApiId()+" - "+item.getTitle()+" - "+item.getText().length());
                } catch (Exception e) {
                    Log.d(TAG, "tut: Error "+e.getMessage());
                }
            }
        } else {
            //Log.d(TAG, "tut: removeItem: "+item.getApiId()+" - "+item.getTitle()+" - "+item.getText().length());
            removeItem(item.getApiId());
        }
    }

    private void removeItem(Integer apiId) {
        DictateItem item = dictationRepo.getItemByApiId(apiId);
        dictationRepo.deleteItem(item.getItemApiId());
        songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
    }

    protected void beforeFinish() {
        if (newCount > 0) {
            PreferencesHelper.setLastSyncCount(mContext, newCount);
        }

        if (!error) {
            dictationRepo.deleteReadItems();
        }
    }
}