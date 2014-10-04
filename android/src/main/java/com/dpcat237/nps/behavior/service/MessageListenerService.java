package com.dpcat237.nps.behavior.service;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.common.constant.MessageConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.model.LabelItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * This service listens for messages back from the watch.
 */
public class MessageListenerService extends WearableListenerService {
    private static final String TAG = "NPS:MessageListenerService";
    private Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onMessageReceived(MessageEvent msgEvent) {
        Log.d(TAG, "tut: onMessageReceived "+msgEvent.getPath());
        if (msgEvent.getPath().equals(MessageConstants.PLAYER_PAUSED)) {
            PlayerService.pause(mContext);
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_PLAYING)) {
            PlayerServiceStatus playerStatus = PlayerServiceStatus.getInstance();
            if (playerStatus.hasActiveSong()) {
                PlayerService.play(mContext);
            } else {
                PlayerService.playpause(mContext, SongConstants.GRABBER_TYPE_DICTATE_ITEM, 0);
            }
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_BACKWARD)) {
            PlayerService.skipBack(mContext);
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_FORWARD)) {
            PlayerService.skipForward(mContext);
        } else if (msgEvent.getPath().equals(MessageConstants.PLAYER_SET_LABEL)) {
            JSONObject json = getData(msgEvent);
            if (json == null) {
                return;
            }

            try {
                Integer labelApiId = Integer.parseInt(json.getString("label_api_id"));
                Boolean set = setLabel(Integer.parseInt(json.getString("item_api_id")), labelApiId);
                responseSetLabel(labelApiId, set);
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

    private Boolean setLabel(Integer itemApiId, Integer labelApiId) {
        Boolean set = false;
        LabelRepository labelRepo = new LabelRepository(mContext);
        labelRepo.open();

        if (labelRepo.checkLabelSet(labelApiId, itemApiId)) {
            labelRepo.removeLabelItem(labelApiId, itemApiId);
        } else {
            LabelItem labelItem = new LabelItem();
            labelItem.setLabelApiId(labelApiId);
            labelItem.setItemApiId(itemApiId);

            labelRepo.setLabel(labelItem);
            set = true;
        }
        labelRepo.close();

        return set;
    }

    private void responseSetLabel(Integer labelApiId, Boolean set) {
        PlayerServiceStatus playerStatus = PlayerServiceStatus.getInstance();
        JSONObject json = new JSONObject();
        String message = "";
        try {
            json.put("label_api_id", labelApiId);
            json.put("set", set.toString());
            message = json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        playerStatus.sendWearMessage(MessageConstants.PLAYER_SET_LABEL, message);
    }
}