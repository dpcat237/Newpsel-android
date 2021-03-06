package com.dpcat237.nps.behavior.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.SongsFactory;
import com.dpcat237.nps.behavior.factory.songManager.SongsManager;
import com.dpcat237.nps.behavior.manager.LockscreenManager;
import com.dpcat237.nps.behavior.manager.PlayerQueueManager;
import com.dpcat237.nps.behavior.receiver.LockscreenReceiver;
import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.common.constant.MessageConstants;
import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.constant.NotificationConstants;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.helper.WearHelper;
import com.dpcat237.nps.helper.WidgetHelper;
import com.dpcat237.nps.ui.dialog.PlayerLabelsDialog;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerService extends PlayerServiceCommands {
    private static final String TAG = "NPS:PlayerService";
    protected MediaPlayer player;
    private LockscreenManager lockscreenManager = null;
    private PlayerQueueManager queryManager = null;
    private PlayerServiceStatus playerStatus;
    private Context mContext;
    private RemoteViews notificationView = null;
    private NotificationCompat.Builder bld = null;
    private boolean isPlayerPrepared;
    protected boolean onPhone;
    protected Timer updateTimer;
    private Integer justStarted = 1;
    private SongsManager songGrabManager;
    private Song currentSong;
    private Boolean notificationCreated = false;
    private Boolean isPausedFocusLost = false;

    private class UpdatePositionTimerTask extends TimerTask {
        protected int lastPosition = 0;
        public void run() {
                if (player != null && !player.isPlaying())
                    return;
                int oldPosition = lastPosition;
                lastPosition = player.getCurrentPosition();
                if (oldPosition / 1000 != lastPosition / 1000)
                    updateActiveSongPosition(lastPosition);
            }
    };

    private final AudioManager.OnAudioFocusChangeListener _afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                Log.d(TAG, "tut: got a transient audio focus gain event somehow");
            }

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d(TAG, "tut: onAudioFocusChange b");
                PlayerService.pause(PlayerService.this);
                changeNotificationPlayButton();
                isPausedFocusLost = playerStatus.isPaused();
            }
            if (playerStatus.isPaused() && focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d(TAG, "tut: onAudioFocusChange c");
                PlayerService.play(PlayerService.this);
                changeNotificationPlayButton();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d(TAG, "tut: onAudioFocusChange d");
                PlayerService.pause(PlayerService.this);
                changeNotificationPlayButton();
            }
        }
    };

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "tut: BroadcastReceiver onReceive");
            if (player.isPlaying()) {
                Log.d(TAG, "tut: BroadcastReceiver stop");
                PlayerService.stop(context);
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "tut: onBind");

        return null;
    }

    private void createUpdateTimer() {
        if (updateTimer != null) {
            return;
        }

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
        Log.d(TAG, "tut: onDestroy");
        super.onDestroy();
        safeUnregisterReceiver(noisyReceiver);
    }

    private void setup() {
        if (queryManager == null) {
            queryManager = new PlayerQueueManager(mContext);
        }
        if (playerStatus == null) {
            playerStatus = PlayerServiceStatus.getInstance();
            playerStatus.setup(mContext);
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

                    //stopUpdateTimer();
                    player.reset();
                    isPlayerPrepared = false;

                    return true;
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    playNextSong();
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

    public PlayerServiceStatus getCurrentStatus() {
        return playerStatus;
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "tut: handleIntent ");
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        if (!intent.getExtras().containsKey(PlayerConstants.EXTRA_PLAYER_COMMAND)) {
            return;
        }

        Log.d(TAG, "tut: before setup");
        setup();
        Log.d(TAG, "tut: intent "+intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, -1));
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
                playNextSong();
                break;
            case PlayerConstants.PLAYER_COMMAND_RESTART:
                Log.d(TAG, "tut:  PLAYER_COMMAND_RESTART");
                //PodaxLog.log(this, "PlayerService got a command: restart");
                restart();
                break;
            case PlayerConstants.PLAYER_COMMAND_SKIPBACK:
                Log.d(TAG, "tut:  PLAYER_COMMAND_SKIPBACK");
                if (playerStatus.hasActiveSong()) {
                    playPreviousSong();
                }
                break;
            case PlayerConstants.PLAYER_COMMAND_SKIPFORWARD:
                Log.d(TAG, "tut:  PLAYER_COMMAND_SKIPFORWARD");
                if (!playerStatus.hasActiveSong()) {
                    break;
                }
                playNextSong();
                changeNotificationPlayButtonPause();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAYPAUSE:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAYPAUSE a");
                if (player.isPlaying()) {
                    Log.d(TAG, "tut:  PLAYER_COMMAND_PLAYPAUSE b");
                    pause();
                } else if (playerStatus.hasActiveSong()) {
                    Log.d(TAG, "tut:  PLAYER_COMMAND_PLAYPAUSE c");
                    grabAudioFocusAndResume();
                } else {
                    Log.d(TAG, "tut:  PLAYER_COMMAND_PLAYPAUSE d");
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
                if (isPausedFocusLost) {
                    return;
                }
                grabAudioFocusAndResume();
                changeNotificationPlayButtonPause();
                break;
            case PlayerConstants.PLAYER_COMMAND_PAUSE:
                pause();
                changeNotificationPlayButton();
                break;
            case PlayerConstants.PLAYER_COMMAND_STOP:
                stop();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_SPECIFIC_SONG:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAY_SPECIFIC_SONG");
                Integer itemApiId =  Integer.valueOf(intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1));
                if (itemApiId.equals(playerStatus.getCurrentId()) && player.isPlaying()) {
                    pause();
                } else {
                    playSong(intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE), itemApiId);
                }
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_LIST:
                play(intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE), intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1));
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAYPAUSE_LIST:
                Integer listId = intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);
                if (listId.equals(playerStatus.getCurrentId()) && player.isPlaying()) {
                    pause();
                } else {
                    play(intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE), listId);
                }
                if (justStarted > 1) {
                    changeNotificationPlayButton();
                }
                break;
        }
        justStarted ++;
    }

    private void playSong(String playType, Integer itemApiId) {
        if (playerStatus.getCurrentId().equals(itemApiId) && playerStatus.hasActiveSong()) {
            grabAudioFocusAndResume();
            return;
        }

        queryManager.setCursorSong(playType, itemApiId);
        if (queryManager.areError()) {
            notifyStartProblem();
            return;
        }
        playFirstSong();
        playerStatus.setCurrentItemId(itemApiId);
        playerStatus.setPlayerType(playType);
    }

    private void play(String playType, Integer listId) {
        if (playerStatus.getCurrentId().equals(listId) && !listId.equals(0)) {
            grabAudioFocusAndResume();
            return;
        }

        queryManager.setCursorList(playType, listId);
        if (queryManager.areError()) {
            notifyStartProblem();
            return;
        }
        playFirstSong();
        playerStatus.setCurrentListId(listId);
        playerStatus.setPlayerType(playType);
    }

    private void notifyStartProblem() {
        NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.player_start_problem));
    }

    private void pause() {
        player.pause();
        updateActiveSongPosition(player.getCurrentPosition());
        updatePlayerStatus(PlayerConstants.STATUS_PAUSED);
        lockscreenManager.setLockscreenPaused();
    }

    private void stop() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(_afChangeListener);

        //stopUpdateTimer();
        removeNotification();
        lockscreenManager.removeLockscreenControls();

        if (player != null && player.isPlaying()) {
            player.pause();
            updateActiveSongPosition(player.getCurrentPosition());
            player.stop();
        }

        updatePlayerStatus(PlayerConstants.STATUS_STOPPED);
        player = null;
        justStarted = 1;
        stopSelf();
    }

    private void updatePlayerStatus(int status) {
        Log.d(TAG, "tut: updatePlayerStatus "+status);
        playerStatus.updateStatus(status);
        switch (status) {
            case PlayerConstants.STATUS_QUEUEEMPTY:
                break;
            case PlayerConstants.STATUS_STOPPED:
                queryManager.finish();
                break;
            case PlayerConstants.STATUS_PAUSED:
                break;
            case PlayerConstants.STATUS_PLAYING:
                lockscreenManager.setLockscreenPlaying();
                break;
        }
        WidgetHelper.updateWidgets(mContext);
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
        Log.d(TAG, "tut: grabAudioFocusAndResume: 1");


        if (!grabAudioFocus()) {
            return;
        }

        isPausedFocusLost = false;
        if (playerStatus.isPaused() && isPlayerPrepared) {
            Log.d(TAG, "tut: player.start: 1");
            player.start();
            Log.d(TAG, "tut: player.start: 2");
            updatePlayerStatus(PlayerConstants.STATUS_PLAYING);

            return;
        }

        Log.d(TAG, "tut: currentSong: 1");
        currentSong = queryManager.getCurrentSong();
        Log.d(TAG, "tut: currentSong: "+currentSong.getTitle());
        prepareMediaPlayer(currentSong);

        lockscreenManager = new LockscreenManager();
        lockscreenManager.setupLockscreenControls(this, currentSong);

        updatePlayerStatus(PlayerConstants.STATUS_PLAYING);

        showNotification();
        //createUpdateTimer();
    }

    private void playFirstSong() {
        Log.d(TAG, "tut: playFirstSong: 1");
        if (!grabAudioFocus()) {
            return;
        }

        Log.d(TAG, "tut: currentSong: 1");
        currentSong = queryManager.getCurrentSong();
        prepareMediaPlayer(currentSong);
        Log.d(TAG, "tut: currentSong: "+currentSong.getTitle());

        lockscreenManager = new LockscreenManager();
        lockscreenManager.setupLockscreenControls(this, currentSong);

        updatePlayerStatus(PlayerConstants.STATUS_PLAYING);

        showNotification();
        //createUpdateTimer();
    }

    private boolean prepareMediaPlayer(Song song) {
        try {
            String songPath = FileHelper.getSongPath(mContext, song.getFilename());
            if (!songExists(songPath)) {
                markSongAsRead();
                stop();

                return false;
            }

            player.reset();
            player.setDataSource(songPath);
            player.prepare();
            //player.seekTo(song.getLastPosition());
            playerStatus.setCurrentSong(song);
            isPlayerPrepared = true;
            playerStatus.setItemApiId(song.getItemApiId());

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

    private Boolean songExists(String songPath) {
        File songFile = new File(songPath);

        return songFile.exists();
    }

    private void skip(int secs) {
        if (player.isPlaying()) {
            /*Integer newPosition = player.getCurrentPosition() + secs * 1000;
            if (player.getDuration() > newPosition) {
                player.seekTo(player.getCurrentPosition() + secs * 1000);
                updateActiveSongPosition(player.getCurrentPosition());
            }*/
        } else {
            playNextSong();
        }
    }

    private void skipTo(int secs) {
        if (player.isPlaying()) {
            player.seekTo(secs * 1000);
            updateActiveSongPosition(player.getCurrentPosition());
        } else {
            updateActiveSongPosition(secs * 1000);
        }
    }

    private void restart() {
        if (player.isPlaying()) {
            player.seekTo(0);
            updateActiveSongPosition(player.getCurrentPosition());
        } else {
            updateActiveSongPosition(0);
        }
    }

    private void playNextSong() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            //stopUpdateTimer();
        }
        markSongAsRead();
        if (!queryManager.isLast()) {
            queryManager.setNextSong();
            grabAudioFocusAndResume();
            return;
        }

        if (!playerStatus.isList()) {
            NotificationHelper.showSimpleToast(mContext, getNotificationMessage());
            stop();
            return;
        }

        queryManager.setCursorList(playerStatus.getPlayerType(), playerStatus.getCurrentId());
        if (!queryManager.isLast()) {
            grabAudioFocusAndResume();
            return;
        }

        NotificationHelper.showSimpleToast(mContext, getNotificationMessage());
        stop();
    }

    private void markSongAsRead() {
        songGrabManager = SongsFactory.createManager(playerStatus.getPlayerType());
        songGrabManager.setup(mContext);
        songGrabManager.markAsPlayed(currentSong);
        songGrabManager.finish();
        songGrabManager = null;
    }

    private String getNotificationMessage() {
        String message = "";
        if (playerStatus.getPlayerType().equals(SongConstants.GRABBER_TYPE_TITLE)) {
            message = mContext.getString(R.string.player_nt_finish_titles);
        }
        if (playerStatus.getPlayerType().equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            message = mContext.getString(R.string.player_nt_finish_dictations);
        }

        return message;
    }

    private void playPreviousSong() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            //stopUpdateTimer();
            updateActiveSongPosition(player.getCurrentPosition());
        }

        if (!queryManager.isFirst()) {
            queryManager.setPreviousSong();
        }
        grabAudioFocusAndResume();
    }

    private void showNotification() {
        //notify wear device about current playing song
        if (PreferencesHelper.getBooleanPreference(mContext, PreferenceConstants.WEAR_LABELS_SENT)) {
            playerStatus.sendWearMessage(MessageConstants.PLAYING_SONG, WearHelper.prepareSongData(currentSong));
        } else {
            playerStatus.sendWearMessage(MessageConstants.PLAYING_SONG, WearHelper.preparePlayerData(mContext, currentSong));
        }

        Log.d(TAG, "tut: showNotification: start");
        Log.d(TAG, "tut: showNotification: "+currentSong.getTitle());
        notificationView = new RemoteViews(getPackageName(), R.layout.notification_player);
        notificationView.setTextViewText(R.id.songListTitle, currentSong.getListTitle());
        notificationView.setTextViewText(R.id.songTitle, currentSong.getTitle());
        Log.d(TAG, "tut: showNotification: done");

        //set up details intent
        Intent detailsIntent = SongsFactory.getActivityIntent(mContext, currentSong.getType());
        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent showItemIntent = PendingIntent.getActivity(mContext, 0, detailsIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonDetails, showItemIntent);
        notificationView.setOnClickPendingIntent(R.id.lineSongInfo, showItemIntent);

        // set up pause intent
        Intent pauseIntent = new Intent(mContext, PlayerService.class);
        // use data to make intent unique
        pauseIntent.setData(Uri.parse(PlayerConstants.INTENT_DATA_PLAYPAUSE));
        pauseIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonPausePlay, pausePendingIntent);

        // set up forward intent
        Intent forwardIntent = new Intent(mContext, PlayerService.class);
        forwardIntent.setData(Uri.parse(PlayerConstants.INTENT_DATA_FORWARD));
        forwardIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_SKIPFORWARD);
        PendingIntent forwardPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonForward, forwardPendingIntent);

        // set up remove intent
        Intent removeIntent = new Intent(mContext, PlayerService.class);
        removeIntent.setData(Uri.parse(PlayerConstants.INTENT_DATA_STOP));
        removeIntent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_STOP);
        PendingIntent removePendingIntent = PendingIntent.getService(this, 0, removeIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonRemove, removePendingIntent);

        //set up label intent - show labels popup
        Intent labelIntent = new Intent(mContext, PlayerLabelsDialog.class);
        labelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent showLabelIntent = PendingIntent.getActivity(mContext, 0, labelIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.buttonAddLabel, showLabelIntent);

        startNotification();
    }

    private void changeNotificationPlayButton() {
        if (notificationView == null || player == null) {
            return;
        }

        if (player.isPlaying()) {
            notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.ic_activity_pause);
            playerStatus.sendWearMessage(MessageConstants.PLAYER_PLAYING, "");
        } else {
            notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.av_play_white);
            playerStatus.sendWearMessage(MessageConstants.PLAYER_PAUSED, "");
        }
        startNotification();
    }

    private void changeNotificationPlayButtonPause() {
        if (notificationView == null || player == null) {
            return;
        }
        notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.ic_activity_pause);
        playerStatus.sendWearMessage(MessageConstants.PLAYER_PLAYING, "");
        startNotification();
    }

    private void startNotification() {
        if (bld == null) {
            bld = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLocalOnly(true)
                    .setOngoing(true);
        }
        bld.setContent(notificationView);
        Notification notification = bld.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE |
                Notification.FLAG_NO_CLEAR |
                Notification.FLAG_ONGOING_EVENT;

        if (!notificationCreated) {
            startForeground(NotificationConstants.ID_PLAYER_MANAGER, notification);
            notificationCreated = true;
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationConstants.ID_PLAYER_MANAGER, notification);
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstants.ID_PLAYER_MANAGER);
    }

    private void updateActiveSongPosition(int position) {
        queryManager.setLastPosition(position);
    }
}