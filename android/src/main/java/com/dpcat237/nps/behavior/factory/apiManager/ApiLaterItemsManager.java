package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.JsonHelper;
import com.dpcat237.nps.model.LaterItem;

import org.apache.http.client.methods.HttpPost;

public class ApiLaterItemsManager extends ApiPostManager {
    private LaterItem[] items;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SYNC_LATER_ITEMS);
        items = null;
    }

    protected void getRequestResult() {
        items = JsonHelper.getLaterItems(httpResponse);
        result.put("later_items", items);
    }
}