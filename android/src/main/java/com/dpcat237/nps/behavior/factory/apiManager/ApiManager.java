package com.dpcat237.nps.behavior.factory.apiManager;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public abstract class ApiManager {
    private static final String TAG = "NPS:ApiManager";
    protected String jsonData;
    protected Map<String, Object> result;
    protected Boolean error;
    protected String errorMessage;
    protected String response = "";
    protected HttpsURLConnection conn;

    public void setup(JSONObject jsonData) {
        this.jsonData = jsonData.toString();
        result = new HashMap<String, Object>();
        error = false;
        errorMessage = "";

        setupUrl();
        if (error) {
            return;
        }
        setupConnection();
    }

    private void setupUrl() {
        try {
            URL url = new URL(getUrl());
            Log.d(TAG, "tut: getUrl" +getUrl());
            conn = (HttpsURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            Log.d(TAG, "tut: MalformedURLException: "+e.getMessage());
            error = true;
        } catch (IOException e) {
            Log.d(TAG, "tut: IOException setupUrl: "+e.getMessage());
            error = true;
        }
    }

    private void setupConnection() {
        try {
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout( 15000 /* milliseconds */ );
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(jsonData.getBytes().length);
            //make some HTTP header nicety
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        } catch (ProtocolException e) {
            Log.d(TAG, "tut: ProtocolException: "+e.getMessage());
            error = true;
            conn.disconnect();
        }
    }

    public void makeRequest() {
        try {
            Log.d(TAG, "tut: send: "+jsonData);
            conn.connect();
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(jsonData.getBytes());
            os.flush();
            getResponse();
            os.close();
        } catch (IOException e) {
            Log.d(TAG, "tut: IOException makeRequest: "+e.getMessage());
            error = true;
        }
        conn.disconnect();
    }

    private void getResponse() {
        try {
            Log.d(TAG, "tut: getResponse: ");
            if (conn.getResponseCode() != 200) {
                Log.d(TAG, "tut: getResponseCode: not 200");
                error = true;
                return;
            }

            Log.d(TAG, "tut: getInputStream: ");
            Scanner inStream = new Scanner(conn.getInputStream());
            response = "";
            while (inStream.hasNextLine()) {
                response+= (inStream.nextLine());
            }
            inStream.close();
            Log.d(TAG, "tut: response: "+response);
        } catch (IOException e) {
            Log.d(TAG, "tut: IOException: "+e.getMessage());
            error = true;
        }

        if (error) {
            return;
        }
        getRequestResult();
    }

    public Map<String, Object> getResult() {
        result.put("error", error);
        result.put("errorMessage", errorMessage);

        return result;
    }

    abstract String getUrl();
    abstract void getRequestResult();
}