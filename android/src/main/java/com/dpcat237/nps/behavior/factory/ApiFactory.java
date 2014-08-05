package com.dpcat237.nps.behavior.factory;

import com.dpcat237.nps.behavior.factory.apiManager.ApiAddFeedManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiGetFeedsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiItemsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiLabelsItemsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiLaterItemsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiSharedItemsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiSignInManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiSignUpManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.behavior.factory.apiManager.ApiDictateItemsManager;
import com.dpcat237.nps.behavior.factory.apiManager.ApiManager;

public class ApiFactory {
    public static ApiManager createManager(String type) {
        ApiManager apiManager = null;

        if (type.equals(ApiConstants.URL_ADD_FEED)) {
            apiManager = new ApiAddFeedManager();
        }
        if (type.equals(ApiConstants.URL_GET_FEEDS)) {
            apiManager = new ApiGetFeedsManager();
        }
        if (type.equals(ApiConstants.TYPE_SYNC_DICTATE_ITEMS)) {
            apiManager = new ApiDictateItemsManager();
        }
        if (type.equals(ApiConstants.URL_SIGN_IN)) {
            apiManager = new ApiSignInManager();
        }
        if (type.equals(ApiConstants.URL_SIGN_UP)) {
            apiManager = new ApiSignUpManager();
        }
        if (type.equals(ApiConstants.URL_SYNC_ITEMS_UNREAD)) {
            apiManager = new ApiItemsManager();
        }
        if (type.equals(ApiConstants.URL_SYNC_SHARED_ITEMS)) {
            apiManager = new ApiSharedItemsManager();
        }
        if (type.equals(ApiConstants.URL_SYNC_LABELS)) {
            apiManager = new ApiLabelsItemsManager();
        }
        if (type.equals(ApiConstants.URL_SYNC_LATER_ITEMS)) {
            apiManager = new ApiLaterItemsManager();
        }

        return apiManager;
    }
}