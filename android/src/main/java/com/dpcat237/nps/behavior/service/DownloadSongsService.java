package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.manager.FilesManager;
import com.dpcat237.nps.behavior.manager.GrabDictationManager;
import com.dpcat237.nps.behavior.alarm.SyncDictationsAlarm;
import com.dpcat237.nps.helper.BatteryHelper;
import com.dpcat237.nps.helper.ConnectionHelper;

public class DownloadSongsService extends IntentService {
    private static final String TAG = "NPS:DownloadSongsService";
    private volatile static Boolean running = false;
    private GrabDictationManager grabDictation = null;
    private Intent mIntent;
    private Context mContext;
    private FilesManager filesManager;


    public DownloadSongsService() {
        super("DownloadSongsService");
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
        SyncDictationsAlarm.completeWakefulIntent(mIntent);
    }

    private Boolean checkCanRun() {
        Log.d(TAG, "tut: check startProcess");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean dictationTitleEnabled = pref.getBoolean("pref_dictation_title_enable", false);
        Boolean dictationLaterEnabled = pref.getBoolean("pref_dictation_later_enable", false);

        if (!dictationTitleEnabled && !dictationLaterEnabled) {
            return false;
        }

        Boolean enoughBattery = BatteryHelper.isEnoughtBattery(mContext);
        if (!ConnectionHelper.hasConnection(mContext) || !enoughBattery) {
            return false;
        }

        Boolean onlyWifi = pref.getBoolean("pref_dictation_wifi_enable", true);
        Boolean wifiConnection = ConnectionHelper.hasWifiConnection(mContext);

        return (!onlyWifi || wifiConnection);
    }

    private void startProcess() {
        if (grabDictation == null) {
            grabDictation = GrabDictationManager.getInstance(mContext);
        }
        if (!grabDictation.isRunning()) {
            grabDictation.startProcess();
        }

        running = false;
        stopSelf();
    }
}
