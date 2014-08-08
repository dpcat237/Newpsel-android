package com.dpcat237.nps.behavior.factory.syncManager;


import android.util.Log;

import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.LaterItemRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.LaterItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SyncLaterItemsManager extends SyncManager {
    private static final String TAG = "NPS:SyncLaterItemsManager";
    private Integer itemsSyncLimit;
    private LaterItemRepository laterItemRepo;
    private JSONArray viewedItems;
    private LaterItem[] items;
    private JSONArray labels;
    private String stringLabels = "";


    protected void openDB() {
        laterItemRepo = new LaterItemRepository(mContext);
        laterItemRepo.open();
    }

    protected void closeDB() {
        laterItemRepo.close();
    }

    protected void checkNecessarySync() {
        if (!preferences.getBoolean("pref_later_items_enable", false) || !PreferencesHelper.getLaterItemsSync(mContext)) {
            error = true;

            return;
        }

        if (!getLabels()) {
            error = true;

            return;
        }

        itemsSyncLimit = Integer.parseInt(preferences.getString("pref_later_items_quantity", "50"));
        Integer unreadCount = laterItemRepo.countUnreadItems();
        if (unreadCount >= itemsSyncLimit) {
            error = true;

            return;
        }
        itemsSyncLimit = ((itemsSyncLimit - unreadCount) < 10)? 10 : (itemsSyncLimit - unreadCount);
    }

    private Boolean getLabels() {
        Set<String> prefLabels = preferences.getStringSet("pref_later_items_labels", null);
        if (prefLabels.size() < 1) {
            return false;
        }

        labels = new JSONArray();
        try {
            Integer count = 1;
            for (String prefLabel : prefLabels) {
                //put labels ids to JSON
                JSONObject label = new JSONObject();
                label.put("api_id", prefLabel);
                labels.put(label);

                //put labels ids to string
                if (count.equals(1)) {
                    stringLabels += prefLabel;
                } else {
                    stringLabels += ", "+prefLabel;
                }
                count++;
            }
        } catch (JSONException e) {
            Log.e(TAG, "tut: getLabels: "+e.getMessage());
        }

        return true;
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();
        viewedItems = laterItemRepo.getItemsToSync(stringLabels);

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("later_items", viewedItems);
            jsonData.put("labels", labels);
            jsonData.put("limit", itemsSyncLimit);
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LATER_ITEMS, jsonData);
        error = (Boolean) result.get("error");

        if (!error) {
            items = (LaterItem[]) result.get("later_items");
        }
    }

    protected Map<String, Object> getDownloadedItems() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("later_items", items);

        return result;
    }

    protected void processSpecificData() {
        if (items.length < 1) {
            return;
        }

        for (LaterItem item : items) {
            if (item.isUnread()) {
                laterItemRepo.addItem(item);
            } else {
                laterItemRepo.deleteItem(item.getApiId());
            }
        }
    }

    protected void beforeFinish() {
        if (error) {
            return;
        }

        //remove read items
        if (viewedItems.length() > 0) {
            laterItemRepo.removeReadItems();
        }

        //update feeds items count
        LabelRepository labelRepo = new LabelRepository(mContext);
        labelRepo.open();
        labelRepo.unreadCountUpdate();
        labelRepo.close();

        PreferencesHelper.setLaterItemsSync(mContext, false);
    }
}