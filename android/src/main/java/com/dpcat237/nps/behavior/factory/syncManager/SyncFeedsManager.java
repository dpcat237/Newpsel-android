package com.dpcat237.nps.behavior.factory.syncManager;

import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.common.constant.EntityConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Feed;

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
        if (!PreferencesHelper.getSyncRequired(mContext, SyncConstants.SYNC_FEEDS)) {
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
                feedRepo.addFeed(feed);
            }
            if (feed.getStatus().equals(EntityConstants.STATUS_CHANGED)) {
                feedRepo.updateFeed(feed);
            }
            if (feed.getStatus().equals(EntityConstants.STATUS_DELETED)) {
                feedRepo.deleteFeed(feed.getApiId());
            }
        }
    }

    protected void beforeFinish() {
        if (error) {
            return;
        }
        PreferencesHelper.setSyncRequired(mContext, SyncConstants.SYNC_FEEDS, false);
    }
}