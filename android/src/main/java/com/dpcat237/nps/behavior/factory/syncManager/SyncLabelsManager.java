package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.common.constant.EntityConstants;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncLabelsManager extends SyncManager {
    private static final String TAG = "NPS:SyncLabelsManager";
    private LabelRepository labelRepo;
    private Label[] labels;


    protected void openDB() {
        labelRepo = new LabelRepository(mContext);
        labelRepo.open();
    }

    protected void closeDB() {
        labelRepo.close();
    }

    protected void checkNecessarySync() {
        if (!PreferencesHelper.getBooleanPreference(mContext, PreferenceConstants.LABELS_SYNC_REQUIRED)) {
            error = true;
        }
    }

    @SuppressWarnings("unchecked")
    protected void prepareJSON() {
        jsonData = new JSONObject();
        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("labels", labelRepo.getLabelsToSync());
        } catch (JSONException e) {
            error = true;
        }
    }

    protected void makeSpecificRequest() {
        Map<String, Object> result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LABELS, jsonData);
        error = (Boolean) result.get("error");

        if (!error) {
            labels = (Label[]) result.get("labels");
        }
    }

    protected Map<String, Object> getDownloadedItems() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("labels", labels);

        return result;
    }

    protected void processSpecificData() {
        if (labels.length < 1) {
            return;
        }

        for (Label label : labels) {
            if (label.getStatus().equals(EntityConstants.STATUS_NEW)) {
                labelRepo.addLabel(label);
            }
            if (label.getStatus().equals(EntityConstants.STATUS_CHANGED)) {
                labelRepo.updateLabel(label);
            }
            if (label.getStatus().equals(EntityConstants.STATUS_DELETED)) {
                labelRepo.deleteLabel(label.getApiId());
            }
        }
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.WEAR_LABELS_SENT, false);
    }

    protected void beforeFinish() {
        if (error) {
            return;
        }
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.LABELS_SYNC_REQUIRED, false);
    }
}