package com.dpcat237.nps.behavior.factory;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.songManager.SongsManager;

public class SongsFactoryManager {
    private static final String TAG = "NPS:SongsFactoryManager";

    public Boolean createSongs(String type, Context context) {
        Boolean error = false;
        SongsManager songCreator;

        songCreator = SongsFactory.createManager(type);
        try {
            songCreator.setup(context);
            songCreator.createSongs();
            songCreator.finish();
        } catch (Exception e) {
            error = true;
            Log.d(TAG, "tut: Exception "+e.getMessage());
        }

        return error;
    }


}
