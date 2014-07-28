package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.DictateItem;
import com.dpcat237.nps.model.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SyncDictationItemsManager {
    private static final String TAG = "NPS:SyncDictationItemsManager";
    private Context mContext;
    private DictateItemRepository dictateRepo;
    private SongRepository songRepo;
    private Integer downloadQuantity;
    private JSONObject jsonData;
    private Boolean error = false;
    private SharedPreferences preferences;

    public SyncDictationItemsManager(Context context)
    {
        mContext = context;
    }

    public void syncDictations()
    {
        Log.d(TAG, "tut: syncDictations");
        getRepositories();
        if (!checkIfNecessarySync()) {
            Log.d(TAG, "tut: arant necessary");
            finish();

            return;
        }

        DictateItem[] items = downloadItems();
        if (error || items.length < 1) {
            Log.d(TAG, "tut: arant items");
            dictateRepo.deleteReadItems();
            finish();

            return;
        }

        Integer newCount = syncDownloadedItems(items);
        Log.d(TAG, "tut: newCount "+newCount);

        if (newCount > 0) {
            setLastSyncCount(newCount);
        }
        dictateRepo.deleteReadItems();
        dictateRepo.close();
        finish();
        Log.d(TAG, "tut: finish syncDictations");
    }

    private Integer syncDownloadedItems(DictateItem[] items) {
        Integer newCount = 0;
        Log.d(TAG, "tut: downloaded items "+items.length);
        for (DictateItem item : items) {
            if (item.isUnread()) {
                if (!dictateRepo.checkItemExists(item.getApiId())) {
                    dictateRepo.addItem(item);
                    newCount++;
                    //Log.d(TAG, "tut: addItem: "+item.getApiId()+" - "+item.getTitle()+" - "+item.getText().length());
                }
            } else {
                //Log.d(TAG, "tut: removeItem: "+item.getApiId()+" - "+item.getTitle()+" - "+item.getText().length());
                removeItem(item.getApiId());
            }
        }

        return newCount;
    }

    private void getRepositories() {
        dictateRepo = new DictateItemRepository(mContext);
        dictateRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private Boolean checkIfNecessarySync() {
        downloadQuantity = Integer.parseInt(preferences.getString("pref_dictation_quantity", "25"));
        Boolean sync = false;
        Integer unreadCount = dictateRepo.countUnreadItems();
        Integer lastSyncCount = getLastSyncCount();

        if (unreadCount < downloadQuantity) {
            sync = true;
            downloadQuantity = ((downloadQuantity - unreadCount) < 10)? 10 : (downloadQuantity - unreadCount);
        }
        if (lastSyncCount > 0 && lastSyncCount.equals(unreadCount)) {
            sync = false;
        }
        Log.d(TAG, "tut: unreadCount "+unreadCount+" downloadQuantity "+downloadQuantity);

        return sync;
    }

    private void getData() {
        jsonData = new JSONObject();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("items", dictateRepo.getItemsForSync());
            jsonData.put("limit", downloadQuantity);
        } catch (JSONException e) {
            Log.e(TAG, "tut:  "+e.getMessage());
            error = true;
        }

    }

    private void finish() {
        dictateRepo.close();
        songRepo.close();
    }

    private void setLastSyncCount(Integer count) {
        SharedPreferences userPref = mContext.getSharedPreferences("UserPreference", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putInt("sync_dictate_items_last_count", count);
        editor.apply();

        PreferencesHelper.setNewDictationItems(mContext, true);
    }

    private Integer getLastSyncCount() {
        Integer result = 0;

        @SuppressWarnings("static-access")
        SharedPreferences userPref = mContext.getSharedPreferences("UserPreference", mContext.MODE_PRIVATE);
        Integer labelsUpdate = userPref.getInt("sync_dictate_items_last_count", 0);

        if (labelsUpdate != 0) {
            result = labelsUpdate;
        }

        return result;
    }

    private DictateItem[] downloadItems() {
        ApiFactoryManager apiFactoryManager = new ApiFactoryManager();
        getData();
        Log.d(TAG, "tut: request data");
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.TYPE_SYNC_DICTATE_ITEMS, jsonData);
        DictateItem[] items = (DictateItem[]) result.get("items");
        error = (Boolean) result.get("error");

        return items;
    }

    private void removeItem(Integer apiId) {
        DictateItem item = dictateRepo.getItemByApiId(apiId);
        dictateRepo.deleteItem(item.getItemApiId());
        songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
    }
}
