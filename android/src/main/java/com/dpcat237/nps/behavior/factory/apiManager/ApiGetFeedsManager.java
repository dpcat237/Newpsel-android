package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiGetFeedsManager extends ApiPostManager {
    private Feed[] feeds;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_GET_FEEDS);
        feeds = null;
    }

    protected void getRequestResult() {
        feeds = JsonHelper.getFeeds(httpResponse);
        result.put("feeds", feeds);
    }
}