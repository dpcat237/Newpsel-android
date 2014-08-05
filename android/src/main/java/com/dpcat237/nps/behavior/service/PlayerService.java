package com.dpcat237.nps.behavior.service;

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
import com.dpcat237.nps.behavior.service.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.constant.NotificationConstants;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.helper.WidgetHelper;
import com.dpcat237.nps.model.Song;
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
    private NotificationManager notificationManager = null;
    private boolean isPlayerPrepared;
    protected boolean onPhone;
    protected Timer updateTimer;
    private Integer currentId;
    private Integer justStarted = 1;
    private SongsManager songGrabManager;
    private String playType;
    private Song currentSong;
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
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                Log.d(TAG, "tut: got a transient audio focus gain event somehow");
            }

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                PlayerService.stop(PlayerService.this);
            }

            if (playerStatus.isPaused() && focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                PlayerService.play(PlayerService.this);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                PlayerService.pause(PlayerService.this);
            }
        }
    };

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "tut: BroadcastReceiver onReceive");
            if (player.isPlaying()) {
                PlayerService.stop(context);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
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
        super.onDestroy();
        safeUnregisterReceiver(noisyReceiver);
    }

    private void setup() {
        if (queryManager == null) {
            queryManager = new PlayerQueueManager(mContext);
        }
        if (playerStatus == null) {
            playerStatus = PlayerServiceStatus.getInstance();
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
                    playNext();
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
        if (intent == null || intent.getExtras() == null) {
            return;
        }
        if (!intent.getExtras().containsKey(PlayerConstants.EXTRA_PLAYER_COMMAND)) {
            return;
        }

        setup();
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
                    playPreviousPodcast();
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
                if (player.isPlaying()) {
                    pause();
                } else if (playerStatus.hasActiveSong()) {
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
                grabAudioFocusAndResume();
                break;
            case PlayerConstants.PLAYER_COMMAND_PAUSE:
                pause();
                break;
            case PlayerConstants.PLAYER_COMMAND_STOP:
                stop();
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_SPECIFIC_SONG:
                Log.d(TAG, "tut:  PLAYER_COMMAND_PLAY_SPECIFIC_SONG");
                Integer itemApiId = intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);
                if (itemApiId.equals(currentId) && player.isPlaying()) {
                    pause();
                } else {
                    playType = intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE);
                    playSong(playType, itemApiId);
                }
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAY_LIST:
                play(intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE), intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1));
                break;
            case PlayerConstants.PLAYER_COMMAND_PLAYPAUSE_LIST:
                Integer listId = intent.getIntExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, -1);
                if (listId.equals(currentId) && player.isPlaying()) {
                    pause();
                } else {
                    playType = intent.getStringExtra(PlayerConstants.EXTRA_PLAYER_TYPE);
                    play(playType, listId);
                }
                if (justStarted > 1) {
                    changeNotificationPlayButton();
                }
                break;
        }

        if (justStarted == 1) {
            PreferencesHelper.setPlayerActive(mContext, true);
        }

        justStarted ++;
    }

    private void playSong(String playType, Integer itemApiId) {
        if (currentId == itemApiId) {
            grabAudioFocusAndResume();

            return;
        }

        queryManager.setCursorSong(playType, itemApiId);
        if (queryManager.areError()) {
            notifyStartProblem();

            return;
        }
        playFirstSong();
        currentId = itemApiId;
    }

    private void play(String playType, Integer listId) {
        if (currentId == listId) {
            grabAudioFocusAndResume();

            return;
        }

        queryManager.setCursorList(playType, listId);
        if (queryManager.areError()) {
            notifyStartProblem();

            return;
        }
        playFirstSong();
        currentId = listId;
    }

    private void notifyStartProblem() {
        NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.player_start_problem));
    }

    private void pause() {
        player.pause();
        updateActivePodcastPosition(player.getCurrentPosition());
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
            updateActivePodcastPosition(player.getCurrentPosition());
            player.stop();
        }

        updatePlayerStatus(PlayerConstants.STATUS_STOPPED);
        player = null;
        justStarted = 1;
        stopSelf();
    }

    private void updatePlayerStatus(int status) {
        playerStatus.updateStatus(status);
        switch (status) {
            case PlayerConstants.STATUS_QUEUEEMPTY:
                break;
            case PlayerConstants.STATUS_STOPPED:
                PreferencesHelper.setPlayerActive(mContext, false);
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
        if (!grabAudioFocus() || this == null) {
            return;
        }

        if (playerStatus.isPaused() && isPlayerPrepared) {
            player.start();
            updatePlayerStatus(PlayerConstants.STATUS_PLAYING);

            return;
        }

        currentSong = queryManager.getCurrentSong();
        prepareMediaPlayer(currentSong);

        lockscreenManager = new LockscreenManager();
        lockscreenManager.setupLockscreenControls(this, currentSong);

        updatePlayerStatus(PlayerConstants.STATUS_PLAYING);

        showNotification();
        //createUpdateTimer();
    }

    private void playFirstSong() {
        if (!grabAudioFocus() || this == null) {
            return;
        }

        currentSong = queryManager.getCurrentSong();
        prepareMediaPlayer(currentSong);

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
            PreferencesHelper.setCurrentItemApiId(mContext, song.getItemApiId());

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
                updateActivePodcastPosition(player.getCurrentPosition());
            }*/
        } else {
            playNextSong();
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

    private void playNext() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            //stopUpdateTimer();
            updateActivePodcastPosition(player.getCurrentPosition());
        }

        if (queryManager.areMoreParts()) {
            grabAudioFocusAndResume();

            return;
        }

        nextSong();
    }

    private void playNextSong() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            //stopUpdateTimer();
            updateActivePodcastPosition(player.getCurrentPosition());
        }

        nextSong();
    }

    private void nextSong() {
        markSongAsRead();
        if (queryManager.isLast()) {
            NotificationHelper.showSimpleToast(mContext, getNotificationMessage());
            stop();

            /* http://stackoverflow.com/questions/3873659/android-how-can-i-get-the-current-foreground-activity-from-a-service
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);*/

            return;
        }

        queryManager.setNextSong();
        grabAudioFocusAndResume();
    }

    private void markSongAsRead() {
        songGrabManager = SongsFactory.createManager(playType);
        songGrabManager.setup(mContext);
        songGrabManager.markAsPlayed(currentSong);
        songGrabManager.finish();
        songGrabManager = null;
    }

    private String getNotificationMessage() {
        String message = "";
        if (playType.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            message = mContext.getString(R.string.player_nt_finish_titles);
        }
        if (playType.equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            message = mContext.getString(R.string.player_nt_finish_dictations);
        }

        return message;
    }

    private void playPreviousPodcast() {
        if (player != null) {
            // stop the player and the updating while we do some administrative stuff
            player.pause();
            //stopUpdateTimer();
            updateActivePodcastPosition(player.getCurrentPosition());
        }

        if (!queryManager.isFirst()) {
            queryManager.setPreviousSong();
        }
        grabAudioFocusAndResume();
    }

    private void showNotification() {
        notificationView = new RemoteViews(getPackageName(), R.layout.notification_player);
        notificationView.setTextViewText(R.id.songListTitle, currentSong.getListTitle());
        notificationView.setTextViewText(R.id.songTitle, currentSong.getTitle());

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
        } else {
            notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.av_play_white);
        }
        startNotification();
    }

    private void changeNotificationPlayButtonPause() {
        if (notificationView == null || player == null) {
            return;
        }
        notificationView.setImageViewResource(R.id.buttonPausePlay, R.drawable.ic_activity_pause);
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
        notificationManager.notify(NotificationConstants.ID_PLAYER_MANAGER, bld.build());
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
}