package com.dpcat237.nps.behavior.factory;

import android.content.Context;
import android.content.Intent;

import com.dpcat237.nps.behavior.factory.songManager.SongsDictateItemManager;
import com.dpcat237.nps.behavior.factory.songManager.SongsManager;
import com.dpcat237.nps.behavior.factory.songManager.SongsTitleManager;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.ui.activity.DictateItemActivity;
import com.dpcat237.nps.ui.activity.ItemActivity;

public class SongsFactory {
    public static SongsManager createManager(String type) {
        SongsManager songsManager = null;

        if (type.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            songsManager = new SongsTitleManager();
        } else if (type.equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            songsManager = new SongsDictateItemManager();
        }

        return songsManager;
    }

    public static Intent getActivityIntent(Context context, String type) {
        Intent intent = null;

        if (type.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            intent = new Intent(context, ItemActivity.class);
        } else if (type.equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            intent = new Intent(context, DictateItemActivity.class);
        }

        return intent;
    }
}