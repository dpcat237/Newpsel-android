package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.constant.ApiConstants;

public class ApiGetFeedsManager extends ApiManager {
    private Feed[] feeds = null;

    protected String getUrl() {
        return ApiConstants.URL_GET_FEEDS;
    }

    protected void getRequestResult() {
        feeds = JsonHelper.getFeeds(response);
        result.put("feeds", feeds);
    }
}