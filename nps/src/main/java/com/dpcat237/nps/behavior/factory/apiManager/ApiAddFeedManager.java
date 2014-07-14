package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.JsonHelper;
import com.dpcat237.nps.model.Item;

import org.apache.http.client.methods.HttpPost;

public class ApiAddFeedManager extends ApiPostManager {
    private Item[] items;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_ADD_FEED);
        items = null;
    }

    protected void getRequestResult() {
        items = JsonHelper.getItems(httpResponse);
        result.put("items", items);
    }
}