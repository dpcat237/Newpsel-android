package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.constant.ApiConstants;

public class ApiLabelsManager extends ApiManager {
    private Label[] labels;

    protected String getUrl() {
        return ApiConstants.URL_SYNC_LABELS;
    }

    protected void getRequestResult() {
        labels = JsonHelper.getLabels(response);
        result.put("tag", labels);
    }
}