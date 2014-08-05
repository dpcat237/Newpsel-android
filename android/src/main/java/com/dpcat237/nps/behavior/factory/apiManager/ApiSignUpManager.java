package com.dpcat237.nps.behavior.factory.apiManager;

import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiSignUpManager extends ApiPostManager {

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SIGN_UP);
    }

    protected void getRequestResult() {
        result.put("result", httpResponse);
    }
}