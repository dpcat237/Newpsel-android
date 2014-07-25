package com.dpcat237.nps.behavior.service.valueObject;

import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.model.Song;

public class PlayerServiceStatus {
    private volatile static PlayerServiceStatus uniqueInstance;
    private Integer currentStatus = 0;
    private Song currentSong = null;

    private PlayerServiceStatus() {}

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
}