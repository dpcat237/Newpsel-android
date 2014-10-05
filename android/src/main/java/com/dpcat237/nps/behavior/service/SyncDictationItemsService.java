package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.SongsFactoryManager;
import com.dpcat237.nps.behavior.manager.SyncDictationItemsManager;
import com.dpcat237.nps.behavior.alarm.SyncDictationsAlarm;
import com.dpcat237.nps.common.constant.BroadcastConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.BroadcastHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.PreferencesHelper;

public class SyncDictationItemsService extends IntentService {
    private static final String TAG = "NPS:SyncDictationItemsService";
    private volatile static Boolean running = false;
    private Context mContext;
    private SyncDictationItemsManager syncManager;
    private SongsFactoryManager songsFactoryManager;


    public SyncDictationItemsService() {
        super("SyncDictationItemsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mContext = getApplicationContext();
        if (syncManager == null) {
            syncManager = new SyncDictationItemsManager(mContext);
        }
        if (songsFactoryManager == null) {
            songsFactoryManager = new SongsFactoryManager();
        }

        if (!running) {
            synchronized ("running") {
                if (!running && checkCanRun()) {
                    running = true;

                    try {
                        startProcess();
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
        }

        SyncDictationsAlarm.completeWakefulIntent(intent);
    }

    private Boolean checkCanRun() {
        Log.d(TAG, "tut: check startProcess");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean dictationEnabled = pref.getBoolean("pref_dictation_later_enable", false);

        return (dictationEnabled && ConnectionHelper.hasConnection(mContext));
    }

    private void startProcess() {
        Log.d(TAG, "tut: startProcess");
        syncManager.syncDictations();
        if (PreferencesHelper.areNewDictationItems(mContext)) {
            songsFactoryManager.createSongs(SongConstants.GRABBER_TYPE_DICTATE_ITEM, mContext);
            PreferencesHelper.setNewDictationItems(mContext, false);
            Log.d(TAG, "tut: createSongs done");
        }

        running = false;
        stopSelf();
    }
}
