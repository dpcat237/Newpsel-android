package com.dpcat237.nps.behavior.factory.apiManager;

import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiRecoveryPasswordManager extends ApiPostManager {

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_RECOVERY_PASSWORD);
    }

    protected void getRequestResult() {
        if (!httpResponse.equals("100")) {
            result.put("error", true);
        }
    }
}