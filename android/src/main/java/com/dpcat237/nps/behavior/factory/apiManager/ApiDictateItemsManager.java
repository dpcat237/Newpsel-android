package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.DictateItem;
import com.dpcat237.nps.constant.ApiConstants;

public class ApiDictateItemsManager extends ApiManager {
    private DictateItem[] items = null;

    protected String getUrl() {
        return ApiConstants.URL_SYNC_DICTATE_ITEMS;
    }

    protected void getRequestResult() {
        items = JsonHelper.getDictateItems(response);
        result.put("items", items);
    }
}