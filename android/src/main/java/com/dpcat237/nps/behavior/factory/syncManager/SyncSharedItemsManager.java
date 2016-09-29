package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.SharedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncSharedItemsManager extends SyncManager {
    private static final String TAG = "NPS:SyncSharedItemsManager";
    private SharedRepository sharedRepo;
    private JSONArray sharedItems;

    protected void openDB() {
        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
    }

    protected void closeDB() {
        sharedRepo.close();
    }

    protected void checkNecessarySync() {
        sharedItems = sharedRepo.getSharedToSync();
        if (sharedItems.length() < 1) {
            error = true;
        }
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();

        try {
            jsonData.put(ApiConstants.DEVICE_ID, PreferencesHelper.generateKey(mContext));
            jsonData.put("sharedItems", sharedItems);
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_SHARED_ITEMS, jsonData);
        error = (Boolean) result.get("error");
    }

    protected Map<String, Object> getDownloadedItems() {
        return new HashMap<String, Object>();
    }

    protected void processSpecificData() { }

    protected void beforeFinish() {
        if (!error) {
            sharedRepo.removeSharedItems();
        }
    }
}