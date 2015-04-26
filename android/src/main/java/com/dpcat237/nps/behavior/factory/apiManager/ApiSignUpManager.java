package com.dpcat237.nps.behavior.factory.apiManager;

import android.util.Log;

import com.dpcat237.nps.constant.ApiConstants;

public class ApiSignUpManager extends ApiManager {
    private static final String TAG = "NPS:ApiSignUpManager";

    protected String getUrl() {
        return ApiConstants.URL_SIGN_UP;
    }

    protected void getRequestResult() {
        result.put("result", response);
    }
}