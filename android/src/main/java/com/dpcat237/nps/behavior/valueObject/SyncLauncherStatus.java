package com.dpcat237.nps.behavior.valueObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.dpcat237.nps.behavior.service.CreateSongsService;
import com.dpcat237.nps.behavior.service.DownloadSongsService;
import com.dpcat237.nps.behavior.service.SyncDictationItemsService;
import com.dpcat237.nps.behavior.service.SyncLaterService;
import com.dpcat237.nps.behavior.service.SyncNewsService;
import com.dpcat237.nps.constant.MainActivityConstants;

import java.util.HashMap;
import java.util.Map;

public class SyncLauncherStatus {
    private static final String TAG = "NPS:SyncLauncherStatus";
    private volatile static SyncLauncherStatus uniqueInstance;
    private Context mContext;
    private Map<String, Boolean> wait;


    private SyncLauncherStatus(Context context) {
        mContext = context;
        wait = new HashMap<String, Boolean>();
        wait.put("sync_0", false);
        wait.put("sync_1", false);
        wait.put("sync_2", false);
    }

    public static SyncLauncherStatus getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (SyncLauncherStatus.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SyncLauncherStatus(context);
                }
            }
        }

        return uniqueInstance;
    }

    public void requireSync(Integer service) {

        if (!wait.get("sync_"+service)) {
            launchSync(service);
            startTime(service);
        }
    }

    private void launchSync(Integer service) {
        if (service.equals(MainActivityConstants.DRAWER_MAIN_ITEMS)) {
            Intent syncNews = new Intent(mContext, SyncNewsService.class);
            mContext.startService(syncNews);
            Intent createSongs = new Intent(mContext, CreateSongsService.class);
            mContext.startService(createSongs);
            Intent downloadSongs = new Intent(mContext, DownloadSongsService.class);
            mContext.startService(downloadSongs);
        } else if (service.equals(MainActivityConstants.DRAWER_MAIN_LATER_ITEMS)) {
            Intent syncLater = new Intent(mContext, SyncLaterService.class);
            mContext.startService(syncLater);
        } else if (service.equals(MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS)) {
            Intent syncDictationItems = new Intent(mContext, SyncDictationItemsService.class);
            mContext.startService(syncDictationItems);
            Intent createSongs = new Intent(mContext, CreateSongsService.class);
            mContext.startService(createSongs);
            Intent downloadSongs = new Intent(mContext, DownloadSongsService.class);
            mContext.startService(downloadSongs);
        }
    }

    private void startTime(Integer service) {
        class OneShotTask {
            Integer serviceType;
            OneShotTask(Integer s) {
                serviceType = s;
            }

            public void launch() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    public void run() {
                        updateTimer(serviceType);
                    }
                }, 60000); // 1 minute
            }
        }

        wait.put("sync_" + service, true);
        OneShotTask task = new OneShotTask(service);
        task.launch();
    }

    public void updateTimer(Integer service) {
        wait.put("sync_" + service, false);
    }
}