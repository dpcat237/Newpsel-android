package com.dpcat237.nps.behavior.manager;

import android.content.Context;

import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.constant.SyncConstants;

public class SyncNewsManager {
    private static final String TAG = "NPS:SyncNewsManager";
    private Context mContext;
    private SyncFactoryManager syncManager;

    public SyncNewsManager(Context context)
    {
        mContext = context;
        syncManager = new SyncFactoryManager();
    }

    public void startSync() {
        syncManager.syncProcess(mContext, SyncConstants.SYNC_FEEDS);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_ITEMS);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_LABELS);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_LABEL_ITEMS);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_SHARED_ITEMS);
    }
}
