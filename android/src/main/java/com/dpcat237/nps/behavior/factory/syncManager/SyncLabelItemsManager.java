package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncLabelItemsManager extends SyncManager {
    private static final String TAG = "NPS:SyncLabelItemsManager";
    private LabelRepository labelRepo;
    private JSONArray selectedItems;

    protected void openDB() {
        labelRepo = new LabelRepository(mContext);
        labelRepo.open();
    }

    protected void closeDB() {
        labelRepo.close();
    }

    protected void checkNecessarySync() {
        selectedItems = labelRepo.getSelectedItemsToSync();
        if (selectedItems.length() < 1) {
            error = true;
        }
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("laterItems", selectedItems);
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LABEL_ITEMS, jsonData);
        error = (Boolean) result.get("error");
    }

    protected Map<String, Object> getDownloadedItems() {
        return new HashMap<String, Object>();
    }

    protected void processSpecificData() { }

    protected void beforeFinish() {
        if (!error) {
            labelRepo.removeLabelItems();
        }
    }
}