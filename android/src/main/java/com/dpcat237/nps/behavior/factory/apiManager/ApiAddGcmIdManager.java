package com.dpcat237.nps.behavior.factory.apiManager;


import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiAddGcmIdManager extends ApiPostManager {

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_ADD_GCM_ID);
    }

    protected void getRequestResult() { }
}