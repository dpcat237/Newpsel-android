package com.dpcat237.nps.behavior.factory.apiManager;

import com.dpcat237.nps.constant.ApiConstants;


public class ApiAddFeedManager extends ApiManager {
    protected String getUrl() {
        return ApiConstants.URL_ADD_FEED;
    }

    protected void getRequestResult() {
        if (!response.equals("100")) {
            error = true;
        }
    }
}