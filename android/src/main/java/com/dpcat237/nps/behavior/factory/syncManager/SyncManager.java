package com.dpcat237.nps.behavior.factory.syncManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dpcat237.nps.behavior.factory.ApiFactoryManager;

import org.json.JSONObject;

import java.util.Map;

public abstract class SyncManager {
    private static final String TAG = "NPS:SyncManager";
    protected Context mContext;
    protected Boolean error;
    protected String errorMessage;
    protected SharedPreferences preferences;
    protected ApiFactoryManager apiFactoryManager;
    protected JSONObject jsonData;


    public void setup(Context context) {
        mContext = context;
        error = false;
        errorMessage = "";
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        apiFactoryManager = new ApiFactoryManager();

        openDB();
        checkNecessarySync();
    }

    public void makeRequest() {
        prepareJSON();
        if (error) {
            return;
        }
        makeSpecificRequest();
    }

    public Boolean getResult() {
        return (!areError());
    }

    public Boolean areError() {
        return error;
    }

    public Map<String, Object> getDownloaded() {
        return getDownloadedItems();
    }

    public void processData() {
        processSpecificData();
    }

    public void finish() {
        beforeFinish();
        closeDB();
    }

    protected abstract void openDB();
    protected abstract void closeDB();
    protected abstract void beforeFinish();
    protected abstract void processSpecificData();
    protected abstract void checkNecessarySync();
    protected abstract void prepareJSON();
    protected abstract void makeSpecificRequest();
    protected abstract Map<String, Object> getDownloadedItems();
}