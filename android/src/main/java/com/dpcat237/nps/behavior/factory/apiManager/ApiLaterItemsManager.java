package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.LaterItem;
import com.dpcat237.nps.constant.ApiConstants;

public class ApiLaterItemsManager extends ApiManager {
    private LaterItem[] items = null;

    protected String getUrl() {
        return ApiConstants.URL_SYNC_LATER_ITEMS;
    }

    protected void getRequestResult() {
        items = JsonHelper.getLaterItems(response);
        result.put("later_items", items);
    }
}