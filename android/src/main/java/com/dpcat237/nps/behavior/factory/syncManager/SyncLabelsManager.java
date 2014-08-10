package com.dpcat237.nps.behavior.factory.syncManager;


import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Label;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncLabelsManager extends SyncManager {
    private static final String TAG = "NPS:SyncLabelsManager";
    private LabelRepository labelRepo;
    private Label[] labels;
    private Integer lastUpdate = 0;
    private ArrayList<Label> changedLabels;


    protected void openDB() {
        labelRepo = new LabelRepository(mContext);
        labelRepo.open();
    }

    protected void closeDB() {
        labelRepo.close();
    }

    protected void checkNecessarySync() {
        if (!PreferencesHelper.getSyncRequired(mContext, SyncConstants.SYNC_LABELS)) {
            error = true;
        }
    }

    @SuppressWarnings("unchecked")
    protected void prepareJSON() {
        jsonData = new JSONObject();
        Map<String, Object> resultChanged = labelRepo.getLabelsToSync();
        changedLabels = (ArrayList<Label>) resultChanged.get("labelsArray");
        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("changedLabels", resultChanged.get("labelsJson"));
            jsonData.put("lastUpdate", PreferencesHelper.getLastLabelsUpdate(mContext));
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
            if (label.getId() > 0) {
                labelRepo.setApiId(label.getId(), label.getApiId());
                labelRepo.setApiId(label.getId(), label.getApiId());
            } else {
                labelRepo.addLabel(label, false);
            }
            lastUpdate = label.getLastUpdate();
        }
    }

    protected void beforeFinish() {
        if (lastUpdate.equals(0)) {
            return;
        }
        PreferencesHelper.setLastLabelsUpdate(mContext, lastUpdate);
        PreferencesHelper.setSyncRequired(mContext, SyncConstants.SYNC_LABELS, false);

        if (changedLabels.size() < 1) {
            return;
        }
        for (Label changedLabel : changedLabels) {
            labelRepo.setChanged(changedLabel.getId(), false);
        }
    }
}