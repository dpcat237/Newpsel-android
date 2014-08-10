package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.GcmHelper;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncGcmIdManager extends SyncManager {
    private static final String TAG = "NPS:SyncGcmIdManager";

    protected void openDB() { }

    protected void closeDB() { }

    protected void checkNecessarySync() { }

    protected void prepareJSON() {
        jsonData = new JSONObject();
        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("gcm_id", GcmHelper.getRegId(mContext));
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_ADD_GCM_ID, jsonData);
        error = (Boolean) result.get("error");
    }

    protected Map<String, Object> getDownloadedItems() {
        return new HashMap<String, Object>();
    }

    protected void processSpecificData() { }

    protected void beforeFinish() {
        if (error) {
            GcmHelper.setRegId(mContext, "");
        }
    }
}