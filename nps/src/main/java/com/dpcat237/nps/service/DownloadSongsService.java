package com.dpcat237.nps.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.factory.SongsFactoryManager;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.manager.FilesManager;
import com.dpcat237.nps.receiver.AlarmReceiver;

public class DownloadSongsService extends IntentService {
    public DownloadSongsService() {
        super("SchedulingService");
    }
    
    private static final String TAG = "NPS:DownloadSongsService";
    private volatile static Boolean running = false;
    private GrabDictationService grabDictation = null;
    private Intent mIntent;
    private Context mContext;
    private SongsFactoryManager songsFactoryManager;
    private FilesManager filesManager;


    @Override
    protected void onHandleIntent(Intent intent) {
        this.mIntent = intent;
        this.mContext = getApplicationContext();
        if (songsFactoryManager == null) {
            songsFactoryManager = new SongsFactoryManager();
        }
        if (filesManager == null) {
            filesManager = new FilesManager();
        }

        if (!running) {
            synchronized ("running") {
                if (!running && checkCanRun()) {
                    running = true;

                    try {
                        filesManager.deletePlayedSongs(mContext);
                        startProcess();
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
        }

    }

    private Boolean checkCanRun() {
        Log.d(TAG, "tut: check startProcess");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean dictationEnabled = pref.getBoolean("pref_dictation_title_enable", false);

        if (!dictationEnabled || !GenericHelper.hasConnection(mContext)) {
            return false;
        }

        Boolean wifiEnabled = pref.getBoolean("pref_dictation_title_wifi_enable", false);
        if (wifiEnabled && GenericHelper.hasWifiConnection(mContext)) {
            return true;
        }

        Boolean mobileEnabled = pref.getBoolean("pref_dictation_title_mobile_enable", false);
        if (mobileEnabled && GenericHelper.hasMobileConnection(mContext)) {
            return true;
        }

        return false;
    }

    private void startProcess() {
        Log.d(TAG, "tut: startProcess");
        songsFactoryManager.createSongs(SongConstants.GRABBER_TYPE_TITLE, mContext);
        if (grabDictation == null) {
            grabDictation = GrabDictationService.getInstance(mContext);
        }
        if (!grabDictation.isRunning()) {
            grabDictation.startProcess();
        }

        running = false;
        AlarmReceiver.completeWakefulIntent(mIntent);
        stopSelf();
    }
}
