package com.dpcat237.nps.behavior.factory;

import android.util.Log;

import com.dpcat237.nps.behavior.factory.apiManager.ApiManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiFactoryManager {
    private static final String TAG = "NPS:ApiFactoryManager";

    public Map<String, Object> makeRequest(String type, JSONObject jsonData) {
        Map<String, Object> result = new HashMap<String, Object>();
        ApiManager apiManager;

        apiManager = ApiFactory.createManager(type);
        try {
            apiManager.setup(jsonData);
            apiManager.makeRequest();
            result = apiManager.getResult();
        } catch (Exception e) {
            result.put("error", true);
            result.put("errorMessage", e.getMessage());
            Log.d(TAG, "tut: Exception "+e.getMessage());
        }

        return result;
    }


}
