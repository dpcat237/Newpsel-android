package com.dpcat237.nps.behavior.factory.apiManager;

import com.dpcat237.nps.constant.ApiConstants;

public class ApiRecoveryPasswordManager extends ApiManager {
    protected String getUrl() {
        return ApiConstants.URL_RECOVERY_PASSWORD;
    }

    protected void getRequestResult() {
        if (!response.equals("100")) {
            result.put("error", true);
        }
    }
}