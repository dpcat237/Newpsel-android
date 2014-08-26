package com.dpcat237.nps.behavior.manager;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.util.Log;

import com.dpcat237.nps.behavior.receiver.LockscreenReceiver;
import com.dpcat237.nps.common.model.Song;

public class LockscreenManager {
    private static final String TAG = "NPS:LockscreenManager";
    private RemoteControlClient remoteControlClient;

    public void setupLockscreenControls(Context context, Song song) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (remoteControlClient == null) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.setComponent(new ComponentName(context, LockscreenReceiver.class));
            remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(context, 0, intent, 0));
            audioManager.registerRemoteControlClient(remoteControlClient);
        }

        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        // android built-in lockscreen only supports play/pause/playpause/stop, previous, and next
        // next is destructive in podax because it deletes the cached file
        remoteControlClient.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
                        | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
                        | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);

        try {
            final int METADATA_KEY_ARTWORK = 100;

            // Update the remote controls
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient
                    .editMetadata(true)
                    .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getListTitle())
                    .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getTitle());
                    //.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, song.getDuration());
            metadataEditor.apply();
        } catch (Exception e) {
            Log.d(TAG, "Updating lockscreen: " + e.toString());
        }
    }

    public void removeLockscreenControls() {
        if (remoteControlClient != null) {
            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
        }
    }

    public void setLockscreenPaused() {
        if (remoteControlClient == null) {
            return;
        }
        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
    }

    public void setLockscreenPlaying() {
        if (remoteControlClient == null) {
            return;
        }
        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
    }
}
