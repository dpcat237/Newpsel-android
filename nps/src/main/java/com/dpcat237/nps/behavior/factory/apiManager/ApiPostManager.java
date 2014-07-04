package com.dpcat237.nps.behavior.factory.apiManager;


import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public abstract class ApiPostManager extends ApiManager {
    private static final String TAG = "NPS:ApiPostManager";
    protected HttpPost post;

    protected void setRequestData(StringEntity jsonEntity) {
        post.setEntity(jsonEntity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
    }

    protected void executeRequest() {
        try {
            HttpResponse response = httpClient.execute(post);
            httpResponse = EntityUtils.toString(response.getEntity());
        } catch(Exception e) {
            Log.e(TAG, "Error", e);
            error = true;
        }
    }
}