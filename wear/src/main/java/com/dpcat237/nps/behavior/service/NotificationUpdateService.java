package com.dpcat237.nps.behavior.service;

import android.util.Log;

import com.dpcat237.nps.behavior.manager.PlayerStateManager;
import com.dpcat237.nps.common.constant.MessageConstants;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class NotificationUpdateService extends WearableListenerService {
    private static final String TAG = "NPS Wear:NotificationUpdateService";
    private PlayerStateManager stateManager;


    @Override
    public void onCreate() {
        super.onCreate();
        stateManager = PlayerStateManager.getInstance(getApplicationContext());
    }

    @Override
    public void onMessageReceived(MessageEvent msgEvent) {
        Log.d(TAG, "tut: onMessageReceived "+msgEvent.getPath());

        if (msgEvent.getPath().equals(MessageConstants.PLAYING_SONG)) {
            JSONObject json = getData(msgEvent);
            if (json == null) {
                return;
            }

            stateManager.playSong(json);
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_PLAYING)) {
            stateManager.onPlay();
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_PAUSED)) {
            stateManager.onPause();
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_STOP)) {
            stateManager.onStop();
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_SET_LABEL)) {
            JSONObject json = getData(msgEvent);
            if (json == null) {
                return;
            }

            try {
                stateManager.onSetLabel(Integer.parseInt(json.getString("label_api_id")), Boolean.parseBoolean(json.getString("set")));
            } catch (JSONException e) {
                Log.d(TAG, "tut: onMessageReceived Error: "+e.getMessage());
            }
        }
    }

    private JSONObject getData(MessageEvent msgEvent) {
        JSONObject json = null;
        String data = "";
        try {
            data = new String(msgEvent.getData(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "tut: onMessageReceived Error: "+e.getMessage());
        }

        try {
            json = new JSONObject(data);
        } catch (JSONException e) {
            Log.d(TAG, "tut: onMessageReceived Error: "+e.getMessage());
        }

        return json;
    }
}