package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.constant.ApiConstants;

public class ApiItemsManager extends ApiManager {
    private static final String TAG = "NPS:ApiItemsManager";
    private Item[] items = null;

    protected String getUrl() {
        return ApiConstants.URL_SYNC_ITEMS_UNREAD;
    }

    protected void getRequestResult() {
        items = JsonHelper.getItems(response);
        result.put("items", items);
    }
}