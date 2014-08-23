package com.dpcat237.nps.behavior.factory;

import com.dpcat237.nps.behavior.factory.syncManager.SyncDictationItemsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncFeedsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncGcmIdManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncItemsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncLabelItemsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncLabelsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncLaterItemsManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncSharedItemsManager;
import com.dpcat237.nps.constant.SyncConstants;

public class SyncFactory {
    public static SyncManager createManager(String type) {
        SyncManager syncManager = null;

        if (type.equals(SyncConstants.SYNC_FEEDS)) {
            syncManager = new SyncFeedsManager();
        } else if (type.equals(SyncConstants.SYNC_DICTATION_ITEMS)) {
            syncManager = new SyncDictationItemsManager();
        } else if (type.equals(SyncConstants.SYNC_GCM_ID)) {
            syncManager = new SyncGcmIdManager();
        } else if (type.equals(SyncConstants.SYNC_ITEMS)) {
            syncManager = new SyncItemsManager();
        } else if (type.equals(SyncConstants.SYNC_LABEL_ITEMS)) {
            syncManager = new SyncLabelItemsManager();
        } else if (type.equals(SyncConstants.SYNC_LABELS)) {
            syncManager = new SyncLabelsManager();
        } else if (type.equals(SyncConstants.SYNC_LATER_ITEMS)) {
            syncManager = new SyncLaterItemsManager();
        } else if (type.equals(SyncConstants.SYNC_SHARED_ITEMS)) {
            syncManager = new SyncSharedItemsManager();
        }

        return syncManager;
    }
}