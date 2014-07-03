package com.dpcat237.nps.factory;

import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.factory.apiManager.ApiDictateItemsManager;
import com.dpcat237.nps.factory.apiManager.ApiManager;

public class ApiFactory {
    public static ApiManager createManager(String type) {
        ApiManager apiManager = null;

        if (type.equals(ApiConstants.TYPE_SYNC_DICTATE_ITEMS)) {
            apiManager = new ApiDictateItemsManager();
        }

        return apiManager;
    }
}