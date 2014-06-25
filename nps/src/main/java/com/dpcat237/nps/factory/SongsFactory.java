package com.dpcat237.nps.factory;

import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.factory.songManager.SongsManager;
import com.dpcat237.nps.factory.songManager.SongsManagerTitle;

public class SongsFactory {
    public static SongsManager createManager(String type) {
        SongsManager songsManager = null;

        if (type.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            songsManager = new SongsManagerTitle();
        }

        return songsManager;
    }
}