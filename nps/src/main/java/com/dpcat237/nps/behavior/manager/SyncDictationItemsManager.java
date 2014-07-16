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
    private Integer labelId;

    public SyncDictationItemsManager(Context context)
    {
        mContext = context;
    }

    public void syncDictations()
    {
        Log.d(TAG, "tut: syncDictations");

        getRepositories();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        downloadQuantity = Integer.parseInt(pref.getString("pref_dictation_quantity", "25"));
        if (!checkIfNecessarySync()) {
            Log.d(TAG, "tut: arant necessary");
            finish();

            return;
        }

        getData();
        ApiFactoryManager apiFactoryManager = new ApiFactoryManager();
        Log.d(TAG, "tut: request data");
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.TYPE_SYNC_DICTATE_ITEMS, jsonData);
        DictateItem[] items = (DictateItem[]) result.get("items");
        error = (Boolean) result.get("error");
        if (error || items.length < 1) {
            Log.d(TAG, "tut: arant items");
            dictateRepo.deleteReadItems();
            finish();

            return;
        }

        Integer newCount = 0;
        Log.d(TAG, "tut: downloaded items "+items.length);
        for (DictateItem item : items) {
            if (item.isUnread()) {
                if (!dictateRepo.checkItemExists(item.getApiId())) {
                    dictateRepo.addItem(item);
                    newCount++;
                    //Log.d(TAG, "tut: addItem: "+item.getApiId()+" - "+item.getTitle()+" - "+item.getText());
                }
            } else {
                removeItem(item.getItemApiId());
            }
        }

        Log.d(TAG, "tut: newCount "+newCount);
        if (newCount > 0) {
            setLastSyncCount(newCount);
        }
        dictateRepo.deleteReadItems();
        dictateRepo.close();
        finish();
        Log.d(TAG, "tut: finish syncDictations");
    }

    private void getRepositories() {
        dictateRepo = new DictateItemRepository(mContext);
        dictateRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }

    private Boolean checkIfNecessarySync()
    {
        Boolean sync = false;
        Integer unreadCount = dictateRepo.countUnreadItems();
        Integer lastSyncCount = getLastSyncCount();
        Log.d(TAG, "tut: unreadCount "+unreadCount);

        if (unreadCount < downloadQuantity) {
            sync = true;
        }
        if (lastSyncCount > 0 && lastSyncCount.equals(unreadCount)) {
            sync = false;
        }

        return sync;
    }

    private void getData()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        labelId = Integer.parseInt(pref.getString("dictation_label_id", ""));
        jsonData = new JSONObject();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("items", dictateRepo.getItemsForSync());
            jsonData.put("laterId", labelId);
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

        if (labelsUpdate != null) {
            result = labelsUpdate;
        }

        return result;
    }

    private void removeItem(Integer itemApiId) {
        dictateRepo.deleteItem(itemApiId);
        songRepo.markAsPlayed(itemApiId, SongConstants.GRABBER_TYPE_DICTATE_ITEM);
    }
}
