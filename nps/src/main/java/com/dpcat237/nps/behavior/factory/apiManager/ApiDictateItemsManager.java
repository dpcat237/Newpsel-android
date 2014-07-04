package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.JsonHelper;
import com.dpcat237.nps.model.DictateItem;

import org.apache.http.client.methods.HttpPost;

public class ApiDictateItemsManager extends ApiPostManager {
    private DictateItem[] items;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SYNC_DICTATE_ITEMS);
        items = null;
    }

    protected void getRequestResult() {
        items = JsonHelper.getDictateItems(httpResponse);
        result.put("items", items);
    }
}