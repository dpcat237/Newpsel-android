package com.dpcat237.nps.behavior.factory.apiManager;

import android.util.Log;

import com.dpcat237.nps.constant.ApiConstants;

public class ApiSignInManager extends ApiManager {
    private static final String TAG = "NPS:ApiSignInManager";

    protected String getUrl() {
        return ApiConstants.URL_SIGN_IN;
    }

    protected void getRequestResult() {
        if (!response.equals("100")) {
            error = true;
        }
    }
}