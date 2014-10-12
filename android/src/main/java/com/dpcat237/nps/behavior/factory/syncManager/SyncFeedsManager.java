package com.dpcat237.nps.behavior.factory.syncManager;

import com.dpcat237.nps.common.constant.EntityConstants;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncFeedsManager extends SyncManager {
    private static final String TAG = "NPS:SyncFeedsManager";
    private FeedRepository feedRepo;
    private Feed[] feeds;


    protected void openDB() {
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
    }

    protected void closeDB() {
        feedRepo.close();
    }

    protected void checkNecessarySync() {
        if (!PreferencesHelper.getBooleanPreference(mContext, PreferenceConstants.FEEDS_SYNC_REQUIRED)) {
            error = true;
        }
    }

    protected void prepareJSON() {
        jsonData = new JSONObject();
        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("feeds", feedRepo.getFeedsToSync());
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_GET_FEEDS, jsonData);
        error = (Boolean) result.get("error");

        if (!error) {
            feeds = (Feed[]) result.get("feeds");
        }
    }

    protected Map<String, Object> getDownloadedItems() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("feeds", feeds);

        return result;
    }

    protected void processSpecificData() {
        if (feeds.length < 1) {
            return;
        }

        for (Feed feed : feeds) {
            if (feed.getStatus().equals(EntityConstants.STATUS_NEW)) {
                //Log.d(TAG, "tut: addFeed " + feed.getApiId() + " - " + feed.getTitle());
                feedRepo.addFeed(feed);
            }
            if (feed.getStatus().equals(EntityConstants.STATUS_CHANGED)) {
                //Log.d(TAG, "tut: updateFeed " + feed.getApiId() + " - " + feed.getTitle());
                feedRepo.updateFeed(feed);
            }
            if (feed.getStatus().equals(EntityConstants.STATUS_DELETED)) {
                //Log.d(TAG, "tut: deleteFeed " + feed.getApiId() + " - " + feed.getTitle());
                feedRepo.deleteFeed(feed.getApiId());
            }
        }
    }

    protected void beforeFinish() {
        if (error) {
            return;
        }
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.FEEDS_SYNC_REQUIRED, false);
    }
}