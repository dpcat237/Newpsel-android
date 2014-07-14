package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.receiver.AlarmSyncDictationsReceiver;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.behavior.manager.FilesManager;
import com.dpcat237.nps.behavior.manager.GrabDictationManager;

public class DownloadSongsService extends IntentService {
    private static final String TAG = "NPS:DownloadSongsService";
    private volatile static Boolean running = false;
    private GrabDictationManager grabDictation = null;
    private Intent mIntent;
    private Context mContext;
    private FilesManager filesManager;


    public DownloadSongsService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mIntent = intent;
        this.mContext = getApplicationContext();
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
        Boolean dictationTitleEnabled = pref.getBoolean("pref_dictation_title_enable", false);
        Boolean dictationLaterEnabled = pref.getBoolean("pref_dictation_later_enable", false);

        if (!dictationTitleEnabled && !dictationLaterEnabled) {
            return false;
        }

        if (!ConnectionHelper.hasConnection(mContext)) {
            return false;
        }

        Boolean wifiEnabled = pref.getBoolean("pref_dictation_wifi_enable", false);
        Boolean wifiConnection = ConnectionHelper.hasWifiConnection(mContext);
        if (wifiEnabled && wifiConnection) {
            return true;
        }

        Boolean mobileEnabled = pref.getBoolean("pref_dictation_mobile_enable", false);
        Boolean mobileConnection = ConnectionHelper.hasMobileConnection(mContext);
        if (mobileEnabled && mobileConnection) {
            return true;
        }

        return false;
    }

    private void startProcess() {
        if (grabDictation == null) {
            grabDictation = GrabDictationManager.getInstance(mContext);
        }
        if (!grabDictation.isRunning()) {
            grabDictation.startProcess();
        }

        running = false;
        AlarmSyncDictationsReceiver.completeWakefulIntent(mIntent);
        stopSelf();
    }
}
