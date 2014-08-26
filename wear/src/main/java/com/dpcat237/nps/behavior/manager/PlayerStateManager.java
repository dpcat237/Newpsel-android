package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.PhoneConnection;
import com.dpcat237.nps.common.constant.MessageConstants;
import com.dpcat237.nps.common.helper.JsonHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.constant.BroadcastConstants;
import com.dpcat237.nps.helper.NotificationWearHelper;
import com.dpcat237.nps.ui.activity.PlayerWearActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerStateManager {
    private static final String TAG = "NPSW:PlayerStateManager";
    private volatile static PlayerStateManager uniqueInstance;
    private static Context mContext;
    private Boolean started = false;
    private Boolean isPlaying = false;
    private Boolean hasData = false;
    private Song currentSong;
    private ArrayList<Label> labels;
    private LocalBroadcastManager broadcaster;
    private PhoneConnection phoneConnection;

    private PlayerStateManager() {
        broadcaster = LocalBroadcastManager.getInstance(mContext);
        phoneConnection = new PhoneConnection();
        phoneConnection.setup(mContext);

        NotificationManagerCompat.from(mContext).cancelAll();
    }

    public static PlayerStateManager getInstance(Context context) {
        mContext = context;
        if (uniqueInstance == null) {
            synchronized (PlayerStateManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new PlayerStateManager();
                }
            }
        }

        return uniqueInstance;
    }

    public void start() {
        started = true;
        phoneConnection.start();
    }

    public void stop() {
        isPlaying = false;
        hasData = false;
        phoneConnection.stop();
    }

    public void destroy() {
        started = false;
    }

    public void sendResult(String message) {
        Intent intent = new Intent(BroadcastConstants.PLAYER_ACTIVITY);
        if(message != null){
            intent.putExtra(BroadcastConstants.PLAYER_ACTIVITY_MESSAGE, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    public void playSong(JSONObject json) {
        try {
            currentSong = JsonHelper.getSong(json.getString("song"));
            setLabels(json.getString("labels"));
        } catch (JSONException e) {
            Log.e(TAG, "tut:  playSong Error "+e.getMessage());

            return;
        }

        Log.d(TAG, "tut: playSong started "+started.toString());
        if (started) {
            sendResult(BroadcastConstants.COMMAND_UPDATE_STATE);
        } else {
            Intent startIntent = new Intent(mContext, PlayerWearActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(startIntent);
        }
        isPlaying = true;
        hasData = true;
    }

    private void setLabels(String labelsData) {
        if (labelsData.length() < 3) {
            return;
        }

        Label[] labelsColl = JsonHelper.getLabels(labelsData);
        labels = new ArrayList<Label>(Arrays.asList(labelsColl));
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public Boolean isPlaying() {
        return isPlaying;
    }

    public Boolean hasData() {
        return hasData;
    }

    public void onPlay() {
        sendResult(BroadcastConstants.COMMAND_PLAYING);
        isPlaying = true;
    }

    public void onPause() {
        sendResult(BroadcastConstants.COMMAND_PAUSED);
        isPlaying = false;
    }

    public void onStop() {
        sendResult(BroadcastConstants.COMMAND_STOP);
    }

    public void sendPause() {
        if (!started) {
            return;
        }
        phoneConnection.sendMessage(new PhoneConnection.Message(MessageConstants.PLAYER_PAUSED, null));
        isPlaying = false;
    }

    public void sendPlay() {
        if (!started) {
            return;
        }
        phoneConnection.sendMessage(new PhoneConnection.Message(MessageConstants.PLAYER_PLAYING, null));
        isPlaying = true;
    }

    public void sendBackward() {
        if (!started) {
            return;
        }
        phoneConnection.sendMessage(new PhoneConnection.Message(MessageConstants.PLAYER_BACKWARD, null));
    }

    public void sendForward() {
        if (!started) {
            return;
        }
        phoneConnection.sendMessage(new PhoneConnection.Message(MessageConstants.PLAYER_FORWARD, null));
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }

    public void sendSetLabel(Label label) {
        if (!started) {
            return;
        }

        JSONObject json = new JSONObject();
        String message = "";
        try {
            json.put("item_api_id", currentSong.getItemApiId());
            json.put("label_api_id", label.getApiId());
            message = json.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        try {
            phoneConnection.sendMessage(new PhoneConnection.Message(MessageConstants.PLAYER_SET_LABEL, message.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "tut: sendWearMessage Error: "+e.getMessage());
        }
    }

    public void onSetLabel(Integer labelApiId, Boolean set) {
        Label label = getLabel(labelApiId);
        if (set) {
            NotificationWearHelper.showSimpleToast(mContext, mContext.getString(R.string.toast_set_label, label.getName()));
        } else {
            NotificationWearHelper.showSimpleToast(mContext, mContext.getString(R.string.toast_label_removed, label.getName()));
        }
    }

    private Label getLabel(Integer labelApiId) {
        for(Label label : labels) {
            if (labelApiId == label.getApiId()) {
                return label;
            }
        }

        return null;
    }
}
