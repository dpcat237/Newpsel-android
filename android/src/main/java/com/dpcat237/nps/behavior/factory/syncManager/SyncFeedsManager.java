package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncFeedsManager extends SyncManager {
    private static final String TAG = "NPS:SyncFeedsManager";
    private FeedRepository feedRepo;
    private Feed[] feeds;
    private Integer lastUpdate = 0;


    protected void openDB() {
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
    }

    protected void closeDB() {
        feedRepo.close();
    }

    protected void checkNecessarySync() { }

    protected void prepareJSON() {
        jsonData = new JSONObject();
        ArrayList<Feed> feedsNow = feedRepo.getAllFeeds();
        Integer feedsUpdate = 0;
        if (feedsNow.size() > 0) {
            feedsUpdate = PreferencesHelper.getLastFeedsUpdate(mContext);
        }

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("lastUpdate", feedsUpdate);
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
            if (feedRepo.checkFeedExists(feed.getApiId())) {
                feedRepo.updateFeed(feed);
            } else {
                feedRepo.addFeed(feed);
            }

            if (feed.getLastUpdate() > lastUpdate) {
                lastUpdate = feed.getLastUpdate();
            }
        }
    }

    protected void beforeFinish() {
        if (lastUpdate != 0 && feeds.length > 0) {
            PreferencesHelper.setLastFeedsUpdate(mContext, lastUpdate);
        }
    }
}