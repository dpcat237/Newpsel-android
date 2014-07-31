package com.dpcat237.nps.helper;

import android.content.Context;

import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Label;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiRequestHelper {
    public static Map<String, Object> feedsSyncRequest(Context context, ApiFactoryManager apiFactoryManager, FeedRepository feedRepo) {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();
        ArrayList<Feed> feedsNow = feedRepo.getAllFeeds();
        Integer feedsUpdate = 0;
        if (feedsNow.size() > 0) {
            feedsUpdate = PreferencesHelper.getLastFeedsUpdate(context);
        }

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(context));
            jsonData.put("lastUpdate", feedsUpdate);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_GET_FEEDS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        return result;
    }

    public static Map<String, Object> itemsSyncRequest(Context context, ApiFactoryManager apiFactoryManager, JSONArray items, Integer itemsSyncLimit) {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(context));
            jsonData.put("items", items);
            jsonData.put("limit", itemsSyncLimit);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_ITEMS_UNREAD, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        return result;
    }

    public static Map<String, Object> labelsSyncRequest(Context context, ApiFactoryManager apiFactoryManager, Map<String, Object> resultChanged) {
        JSONArray changedLabels = (JSONArray) resultChanged.get("labelsJson");
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(context));
            jsonData.put("changedLabels", changedLabels);
            jsonData.put("lastUpdate", PreferencesHelper.getLastLabelsUpdate(context));
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LABELS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        return result;
    }

    public static Map<String, Object> labelsSyncRequest(Context context, ApiFactoryManager apiFactoryManager, JSONArray selectedItems) {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(context));
            jsonData.put("laterItems", selectedItems);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LATER_ITEMS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        return result;
    }

    public static Map<String, Object> sharedItemsSyncRequest(Context context, ApiFactoryManager apiFactoryManager, JSONArray sharedItems) {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(context));
            jsonData.put("sharedItems", sharedItems);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_SHARED_ITEMS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        return result;
    }
}
