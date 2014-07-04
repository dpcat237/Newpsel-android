package com.dpcat237.nps.behavior.factory.apiManager;

import android.util.Log;

import com.dpcat237.nps.helper.PreferencesHelper;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiManager {
    protected JSONObject jsonData;
    protected Map<String, Object> result;
    protected Boolean error;
    protected String errorMessage;
    protected HttpClient httpClient;
    protected StringEntity jsonEntity;
    protected String jsonString;
    protected String httpResponse;

    public void setup(JSONObject jsonData) {
        this.jsonData = jsonData;
        result = new HashMap<String, Object>();
        error = false;
        errorMessage = "";
        httpClient = new DefaultHttpClient();
        setupExtra();
    }

    public void makeRequest() {
        prepareData();
        if (error) {
            return;
        }

        execute();
    }

    private void prepareData() {
        try {
            jsonString = jsonData.toString();
            jsonEntity = new StringEntity(jsonString);
            setRequestData(jsonEntity);
        } catch (UnsupportedEncodingException e) {
            Log.e("ApiHelper - getFeeds", "Error", e);
            error = true;
        }
    }

    private void execute() {
        executeRequest();
        if (error) {
            return;
        }

        if (!PreferencesHelper.isNumeric(httpResponse)) {
            getRequestResult();
        } else {
            error = true;
            errorMessage = httpResponse;
        }
    }

    public Map<String, Object> getResult() {
        result.put("error", error);
        result.put("errorMessage", errorMessage);

        return result;
    }

    abstract void setupExtra();
    abstract void setRequestData(StringEntity jsonEntity);
    abstract void executeRequest();
    abstract void getRequestResult();
}