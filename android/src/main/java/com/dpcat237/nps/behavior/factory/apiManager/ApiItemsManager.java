package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Item;

import org.apache.http.client.methods.HttpPost;

public class ApiItemsManager extends ApiPostManager {
    private Item[] items;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SYNC_ITEMS_UNREAD);
        items = null;
    }

    protected void getRequestResult() {
        items = JsonHelper.getItems(httpResponse);
        result.put("items", items);
    }
}