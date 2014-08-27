package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.SongsFactoryManager;
import com.dpcat237.nps.behavior.alarm.SyncDictationsAlarm;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.PreferencesHelper;

public class CreateSongsService extends IntentService {
    private static final String TAG = "NPS:CreateSongsService";
    private volatile static Boolean running = false;
    private Context mContext;
    private SongsFactoryManager songsFactoryManager;


    public CreateSongsService() {
        super("CreateSongsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = getApplicationContext();
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
        Boolean dictationEnabled = pref.getBoolean("pref_dictation_title_enable", false);

        return (!dictationEnabled || !PreferencesHelper.getBooleanPreference(mContext, PreferenceConstants.ITEMS_ARE_NEW));
    }

    private void startProcess() {
        Log.d(TAG, "tut: startProcess");
        songsFactoryManager.createSongs(SongConstants.GRABBER_TYPE_TITLE, mContext);
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.ITEMS_ARE_NEW, false);

        running = false;
        stopSelf();
    }
}
