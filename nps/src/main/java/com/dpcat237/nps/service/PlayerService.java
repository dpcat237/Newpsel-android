package com.dpcat237.nps.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.dialog.PlayerLabelsDialog;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.receiver.LockscreenReceiver;
import com.dpcat237.nps.manager.LockscreenManager;
import com.dpcat237.nps.manager.PlayerQueueManager;
import com.dpcat237.nps.model.Song;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerService extends Service {

    private static final String TAG = "NPS:PlayerService";
    private class UpdatePositionTimerTask extends TimerTask {
        protected int lastPosition = 0;
        public void run() {
            if (player != null && !player.isPlaying())
                return;
            int oldPosition = lastPosition;
            lastPosition = player.getCurrentPosition();
            if (oldPosition / 1000 != lastPosition / 1000)
                updateActivePodcastPosition(lastPosition);
        }
    };

    private final AudioManager.OnAudioFocusChangeListener _afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "tut: OnAudioFocusChangeListener");
            /*PodaxLog.log(PlayerService.this, "audio focus change event");

            if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                PodaxLog.log(PlayerService.this, "got a transient audio focus gain event somehow");

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS)
                PlayerService.stop(PlayerService.this);

            if (PlayerStatus.getCurrentState(PlayerService.this).isPaused() &&
                    focusChange == AudioManager.AUDIOFOCUS_GAIN)
                PlayerService.resume(PlayerService.this, Constants.PAUSE_AUDIOFOCUS);
            else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
                PlayerService.pause(PlayerService.this, Constants.PAUSE_AUDIOFOCUS);*/
        }
    };

    protected MediaPlayer player;
    private LockscreenManager lockscreenManager = null;
    private PlayerQueueManager queryManager = null;
    private Context mContext;
    private RemoteViews notificationView = null;
    private NotificationCompat.Builder bld = null;
    private NotificationManager notificationManager = null;
    private boolean isPlayerPrepared;
    protected boolean onPhone;
    protected Timer updateTimer;
    private boolean pausingFor[] = new boolean[] {false, false, false};

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "tut: BroadcastReceiver onReceive");
            /*if (PlayerStatus.getCurrentState(context).isPlaying()) {
                PlayerService.stop(context);
            }*/
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createUpdateTimer() {
        if (updateTimer != null)
            return;

        updateTimer = new Timer();
        updateTimer.schedule(new UpdatePositionTimerTask(), 250, 250);
    }

    private void stopUpdateTimer() {
        if (updateTimer == null) {
            return;
        }

        updateTimer.cancel();
        updateTimer = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        setup();
        setupReceiver(noisyReceiver, AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        safeUnregisterReceiver(noisyReceiver);
    }

    private void setup() {
        if (queryManager == null) {
            queryManager = new PlayerQueueManager(mContext);
        }

        if (player == null) {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // handle errors so the onCompletionListener doens't get called
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player, int what, int extra) {
                    String message = String.format(Locale.US, "mediaplayer error - what: %d, extra: %d", what, extra);
                    Log.d(TAG, message);

                    stopUpdateTimer();
                    player.reset();
                    isPlayerPrepared = false;

                    return true;
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    playNextPodcast();
                }
            });

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });

            isPlayerPrepared = false;
        }

        if (lockscreenManager == null) {
            lockscreenManager = new LockscreenManager();
        }
    }

    private void setupReceiver(BroadcastReceiver receiver, String action) {
        safeUnregisterReceiver(receiver);
        registerReceiver(receiver, new IntentFilter(action));
    }

    private void safeUnregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null)
            return;
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) { }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        if (!intent.getExtras().containsKey(PlayerConstants.EXTRA_PLAYER_COMMAND)) {
            return;
        }

        setup();

        int pauseReason = intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);

        switch (intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, -1)) {
            case -1:
                return;
            case PlayerConstants.PLAYER_COMMAND_SKIPTO:
                Log.d(TAG, "tut:  PLAYER_COMMAND_SKIPTO");
                //PodaxLog.log(this, "PlayerService got a command: skip to");
                //skipTo(intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, 0));
                break;
            case PlayerConstants.PLAYER_COMMAND_SKIPTOEND:
                Log.d(TAG, "tut:  PLAYER_COMMAND_SKIPTOEND");
                //PodaxLog.log(this, "PlayerService got a command: skip to end");
                playNextPodcast();
                break;
            case PlayerConstants.PLAYER_COMMAND_RESTART:
                Log.d(TAG, "tut:  PLAYER_COMMAND_RESTART");
                //PodaxLog.log(this, "PlayerService got a command: restart");
                restart();
                break;
            case PlayerConstants.PLAYER_COMMAND_SKIPBACK:
                playPreviousPodcast();
                break;
            case PlayerConstants.PLAYER_COMMAND_SKIPFORWARD:
                if (queryManager.isLast()) {
                    Toast.makeText(this, R.string.notification_last_track, Toast.LENGTH_SHORT).show();
                }
                playNextPodcast();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAYPAUSE:
                if (player.isPlaying()) {
                    pause(pauseReason);
                } else {
                    grabAudioFocusAndResume();
                }
                changeNotificationPlayButton();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAYSTOP:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAYSTOP");
                //PodaxLog.log(this, "PlayerService got a command: playstop");
                if (player.isPlaying()) {
                    Log.d(TAG, "tut: PLAYER_COMMAND_PLAYSTOP");
                    stop();
                } else {
                    //PodaxLog.log(this, "  resuming");
                    grabAudioFocusAndResume();
                }
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAY");
                //grabAudioFocusAndResume();
                break;
            case PlayerConstants.PLAYER_COMMAND_PAUSE:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PAUSE");
                //PodaxLog.log(this, "PlayerService got a command: pause");
                pause(pauseReason);
                break;
            case PlayerConstants.PLAYER_COMMAND_STOP:
                stop();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_SPECIFIC_SONG:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAY_SPECIFIC_SONG");
                //PodaxLog.log(this, "PlayerService got a command: play specific podcast");
                //long songId = intent.getLongExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);
                //play(songId);
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_LIST:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAY_LIST");
                String playType = intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE);
                long listId = intent.getLongExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);
                play(playType, listId);
                Log.d(TAG, "tut: PLAYER_COMMAND_PLAY_LIST "+playType+" - "+listId);

                break;
        }
    }

    private void pause(int reason) {
        if (reason == -1){
            return;
        }

        pausingFor[reason] = true;
        player.pause();
        updateActivePodcastPosition(player.getCurrentPosition());
        queryManager.setCurrentStatus(PlayerConstants.PLAYER_STATUS_PAUSED);
        lockscreenManager.setLockscreenPaused();
    }

    private void stop() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(_afChangeListener);

        stopUpdateTimer();
        removeNotification();
        lockscreenManager.removeLockscreenControls();

        if (player != null && player.isPlaying()) {
            player.pause();
            updateActivePodcastPosition(player.getCurrentPosition());
            player.stop();
        }

        queryManager.setCurrentStatus(PlayerConstants.PLAYER_STATUS_STOPPED);
        player = null;
        stopSelf();
    }

    private boolean grabAudioFocus() {
        if (onPhone) {
            return false;
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(_afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            stop();
            return false;
        }

        // grab the media button when we have audio focus
        audioManager.registerMediaButtonEventReceiver(new ComponentName(this, LockscreenReceiver.class));

        return true;
    }

    private void grabAudioFocusAndResume() {
        if (!grabAudioFocus() || this == null) {
            return;
        }

        // make sure we don't pause for media button when audio focus event happens
        for (int i = 0; i < PlayerConstants.PAUSE_COUNT; ++i) {
            pausingFor[i] = false;
        }

        if (queryManager.isPaused() && isPlayerPrepared) {
            player.start();
            queryManager.setCurrentStatus(PlayerConstants.PLAYER_STATUS_PLAYING);
            lockscreenManager.setLockscreenPlaying();

            return;
        }

        Song song = queryManager.getCurrentSong();
        prepareMediaPlayer(song);

        lockscreenManager = new LockscreenManager();
        lockscreenManager.setupLockscreenControls(this, song);

        queryManager.setCurrentStatus(PlayerConstants.PLAYER_STATUS_PLAYING);

        showNotification();
        //createUpdateTimer();
    }

    private boolean prepareMediaPlayer(Song song) {
        try {
            player.reset();
            String songPath = FileHelper.getSongPath(song.getFilename());
            player.setDataSource(songPath);
            player.prepare();
            //player.seekTo(song.getLastPosition());
            isPlayerPrepared = true;
            return true;
        } catch (IllegalStateException e) {
            // called if player is not in idle state
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private void play(String playType, long listId) {
        Log.d(TAG, "tut: PLAYER_COMMAND_PLAY");

        queryManager.setCurrentList(playType, listId);
        grabAudioFocusAndResume();
    }

    private void skip(int secs) {
        if (player.isPlaying()) {
            /*Integer newPosition = player.getCurrentPosition() + secs * 1000;
            if (player.getDuration() > newPosition) {
                player.seekTo(player.getCurrentPosition() + secs * 1000);
                updateActivePodcastPosition(player.getCurrentPosition());
            }*/
        } else {
            playNextPodcast();
        }
    }

    private void skipTo(int secs) {
        if (player.isPlaying()) {
            player.seekTo(secs * 1000);
            updateActivePodcastPosition(player.getCurrentPosition());
        } else {
            updateActivePodcastPosition(secs * 1000);
        }
    }

    private void restart() {
        if (player.isPlaying()) {
            player.seekTo(0);
            updateActivePodcastPosition(player.getCurrentPosition());
        } else {
            updateActivePodcastPosition(0);
        }
    }

    private void playNextPodcast() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            stopUpdateTimer();
            updateActivePodcastPosition(player.getCurrentPosition());
        }

        if (queryManager.isLast()) {
            stop();
        } else {
            queryManager.setNextSong();
            grabAudioFocusAndResume();
        }
    }

    private void playPreviousPodcast() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            stopUpdateTimer();
            updateActivePodcastPosition(player.getCurrentPosition());
        }

        if (!queryManager.isFirst()) {
            queryManager.setPreviousSong();
        }
        grabAudioFocusAndResume();
    }

    private void showNotification() {
        Song song = queryManager.getCurrentSong();
        notificationView = new RemoteViews(getPackageName(), R.layout.player_notification);
        notificationView.setTextViewText(R.id.songListTitle, song.getListTitle());
        notificationView.setTextViewText(R.id.songTitle, song.getTitle());
        //notificationView.setOnClickPendingIntent(R.id.show_btn, showPendingIntent);

        // set up pause intent
        Intent pauseIntent = new Intent(this, PlayerService.class);
        // use data to make intent unique
        pauseIntent.setData(Uri.parse("newpsel://playercommand/pause"));
        pauseIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE);
        pauseIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, PlayerConstants.PAUSE_NOTIFICATION);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonPausePlay, pausePendingIntent);

        // set up forward intent
        Intent forwardIntent = new Intent(this, PlayerService.class);
        forwardIntent.setData(Uri.parse("newpsel://playercommand/forward"));
        forwardIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_SKIPFORWARD);
        PendingIntent forwardPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonForward, forwardPendingIntent);

        // set up remove intent
        Intent removeIntent = new Intent(this, PlayerService.class);
        removeIntent.setData(Uri.parse("newpsel://playercommand/stop"));
        removeIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_STOP);
        PendingIntent removePendingIntent = PendingIntent.getService(this, 0, removeIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonRemove, removePendingIntent);

        Intent labelIntent = new Intent(this, PlayerLabelsDialog.class);
        labelIntent.putExtra(ItemConstants.ITEM_ID, queryManager.getItemId());
        PendingIntent showLabelIntent = PendingIntent.getActivity(this, 0, labelIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonAddLabel, showLabelIntent);

        startNotification();
    }

    private void changeNotificationPlayButton() {
        if (notificationView == null) {
            return;
        }

        if (player.isPlaying()) {
            notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.ic_media_pause);
        } else {
            notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.av_play_white);
        }
        startNotification();
    }

    private void startNotification() {
        if (bld == null) {
            bld = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true);
        }
        bld.setContent(notificationView);

        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        notificationManager.notify(PlayerConstants.NOTIFICATION_ID, bld.build());
    }

    private void removeNotification() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        notificationManager.cancelAll();
    }

    private void updateActivePodcastPosition(int position) {
        queryManager.setLastPosition(position);
    }

    // static functions for easier controls
    public static void play(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY);
    }

    public static void pause(Context context, int pause_reason) {
        Log.d(TAG, "pausing received");
        //PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PAUSE, pause_reason);
    }

    public static void stop(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_STOP);
    }

    public static void playpause(Context context, int pause_reason) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE, pause_reason);
    }

    public static void playstop(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAYSTOP);
    }

    public static void skipForward(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_SKIPFORWARD);
    }

    public static void skipBack(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_SKIPBACK);
    }

    public static void restart(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_RESTART);
    }

    public static void skipTo(Context context, int secs) {
        //PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_SKIPTO, secs);
    }

    public static void play(Context context, String type, long listId) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY_LIST, type, listId);
    }

    public static void play(Context context, Song song) {
        if (song == null) {
            return;
        }
        //PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY_SPECIFIC_SONG, song.getId());
    }

    private static void sendCommand(Context context, int command) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        context.startService(intent);
    }

    private static void sendCommand(Context context, int command, int arg) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, arg);
        context.startService(intent);
    }

    private static void sendCommand(Context context, int command, String type, long listId) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_TYPE, type);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, listId);
        context.startService(intent);
    }

}