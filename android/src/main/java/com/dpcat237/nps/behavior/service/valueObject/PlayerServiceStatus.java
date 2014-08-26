package com.dpcat237.nps.behavior.service.valueObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.dpcat237.nps.behavior.manager.WearConnectionManager;
import com.dpcat237.nps.common.constant.MessageConstants;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Song;

import java.io.UnsupportedEncodingException;

public class PlayerServiceStatus {
    private static final String TAG = "NPS:PlayerServiceStatus";
    private Context mContext;
    private volatile static PlayerServiceStatus uniqueInstance;
    private Integer currentStatus = 0;
    private Song currentSong = null;
    private WearConnectionManager wearConnection;
    private Integer trySend;
    private String sendPath;
    private String sendMessage;


    private PlayerServiceStatus() {
        wearConnection = new WearConnectionManager();
    }

    public static PlayerServiceStatus getInstance() {
        if (uniqueInstance == null) {
            synchronized (PlayerServiceStatus.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new PlayerServiceStatus();
                }
            }
        }

        return uniqueInstance;
    }

    public void setCurrentSong(Song song) {
        this.currentSong = song;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void updateStatus(int status) {
        this.currentStatus = status;
        if (status == PlayerConstants.STATUS_STOPPED) {
            sendWearMessage(MessageConstants.PLAYER_STOP, "");
            stop();
        }
    }

    public Boolean isPaused() {
        return (currentStatus == PlayerConstants.STATUS_PAUSED);
    }

    public Boolean isPlaying() {
        return (currentStatus == PlayerConstants.STATUS_PLAYING);
    }

    public Boolean hasActiveSong() {
        return (currentStatus != 0 && currentStatus != PlayerConstants.STATUS_STOPPED);
    }

    public void setup(Context context) {
        wearConnection.setup(context);
        wearConnection.start();
        mContext = context;
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.WEAR_LABELS_SENT, false);
    }

    public void stop() {
        wearConnection.stop();
    }

    public void sendWearMessage(String path, String message) {
        sendPath = path;
        sendMessage = message;
        if (wearConnection.isConnected()) {
            sendMessage();

            return;
        }

        trySend = 0;
        trySendMessage();
    }

    private void trySendMessage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (wearConnection.isConnected()) {
                    sendMessage();
                } else {
                    if (trySend < 3 && PreferencesHelper.getBooleanPreference(mContext, PreferenceConstants.WEAR_CONNECTED_BEFORE)) {
                        trySend++;
                        trySendMessage();
                    }
                }
            }
        }, 100);
    }

    private void sendMessage() {
        Log.d(TAG, "tut: sendWearMessage "+sendPath);
        Boolean sent = false;
        try {
            wearConnection.sendMessage(new WearConnectionManager.Message(sendPath, sendMessage.getBytes("utf-8")));
            sent = true;
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "tut: sendWearMessage Error: "+e.getMessage());
        }

        if (sent && sendPath.equals(MessageConstants.PLAYING_SONG)) {
            PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.WEAR_LABELS_SENT, true);
        }
        sendPath = "";
        sendMessage = "";
    }
}