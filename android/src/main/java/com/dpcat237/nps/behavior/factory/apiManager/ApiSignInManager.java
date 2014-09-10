package com.dpcat237.nps.behavior.factory.apiManager;

import com.dpcat237.nps.constant.ApiConstants;

import org.apache.http.client.methods.HttpPost;

public class ApiSignInManager extends ApiPostManager {
    private static final String TAG = "NPS:ApiSignInManager";

    protected void setupExtra() {
        post = new HttpPost(ApiConstants.URL_SIGN_IN);
    }

    protected void getRequestResult() {
        if (!httpResponse.equals("100")) {
            error = true;
            errorMessage = httpResponse;
        }
    }
}