package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dpcat237.nps.behavior.receiver.GcmBroadcastReceiver;
import com.dpcat237.nps.constant.GcmConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    private static final String TAG = "NPS:GcmIntentService";
    private Context mContext;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mContext = getApplicationContext();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "tut: Send error: "+extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "tut: Deleted messages on server: "+extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                selectCommand(extras.getString("title"), extras.getString("message"));
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void syncCommand(String type) {
        if (type.equals(SyncConstants.SYNC_FEEDS)) {
            PreferencesHelper.setSyncRequired(mContext, SyncConstants.SYNC_FEEDS, true);
        }
        if (type.equals(SyncConstants.SYNC_LABELS)) {
            PreferencesHelper.setSyncRequired(mContext, SyncConstants.SYNC_LABELS, true);
        }
    }

    private void selectCommand(String command, String type) {
        if (command.equals(GcmConstants.COMMAND_SYNC_REQUIRED)) {
            syncCommand(type);
        }
    }
}
