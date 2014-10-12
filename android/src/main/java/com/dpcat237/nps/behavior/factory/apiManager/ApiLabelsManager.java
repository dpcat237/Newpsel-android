package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiLabelsManager extends ApiPostManager {
    private Label[] labels;

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SYNC_LABELS);
        labels = null;
    }

    protected void getRequestResult() {
        labels = JsonHelper.getLabels(httpResponse);
        result.put("labels", labels);
    }
}