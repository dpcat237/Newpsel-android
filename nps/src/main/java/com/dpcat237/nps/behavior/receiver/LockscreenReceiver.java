package com.dpcat237.nps.behavior.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.constant.PlayerConstants;

public class LockscreenReceiver extends BroadcastReceiver {
    private static final String TAG = "NPS:LockscreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return;
        }

        switch(event.getKeyCode()) {
            // Simple headsets only send KEYCODE_HEADSETHOOK
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if  (event.getRepeatCount() == 0) {
                    PlayerService.playpause(context, PlayerConstants.PAUSE_MEDIABUTTON);
                } else if (event.getRepeatCount() == 2) {
                    PlayerService.skipForward(context);
                    PlayerService.play(context);
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                PlayerService.pause(context, PlayerConstants.PAUSE_MEDIABUTTON);
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                PlayerService.stop(context);
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                PlayerService.skipForward(context);
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                PlayerService.skipBack(context);
                break;
            default:
                Log.d(TAG, "tut: No matched event: " + event.getKeyCode());
        }

        if (this.isOrderedBroadcast()) {
            abortBroadcast();
        }
    }
}