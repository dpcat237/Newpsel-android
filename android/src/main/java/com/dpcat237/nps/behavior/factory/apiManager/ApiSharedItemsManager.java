package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;

public class ApiSharedItemsManager extends ApiManager {
    protected String getUrl() {
        return ApiConstants.URL_SYNC_SHARED_ITEMS;
    }

    protected void getRequestResult() { }
}