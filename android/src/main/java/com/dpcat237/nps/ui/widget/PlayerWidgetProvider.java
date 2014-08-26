package com.dpcat237.nps.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.SongsFactory;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.service.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.ui.activity.MainActivity;
import com.dpcat237.nps.ui.dialog.PlayerLabelsDialog;


public class PlayerWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "NPS:PlayerWidgetProvider";
    private Context mContext;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (appWidgetIds.length == 0) {
            return;
        }
        mContext = context;

        PlayerServiceStatus playerStatus = PlayerServiceStatus.getInstance();
        Song song = playerStatus.getCurrentSong();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_player);

        //check if are dictations to play
        DictateItemRepository dictateRepo = new DictateItemRepository(context);
        dictateRepo.open();
        Integer unreadCount = dictateRepo.countUnreadGrabberItems();
        dictateRepo.close();

        for (int widgetId : appWidgetIds) {
            updateSongDetails(playerStatus, song, views);
            if (!playerStatus.hasActiveSong()) {
                views.setOnClickPendingIntent(R.id.buttonDetails, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, new Intent(), 0);
                views.setOnClickPendingIntent(R.id.buttonBackward, pendingIntent);
                if (unreadCount > 0) {
                    views.setOnClickPendingIntent(R.id.buttonPausePlay, getPlayIntent());
                } else {
                    views.setOnClickPendingIntent(R.id.buttonPausePlay, pendingIntent);
                }
                views.setOnClickPendingIntent(R.id.buttonForward, pendingIntent);
                views.setOnClickPendingIntent(R.id.buttonAddLabel, pendingIntent);
                views.setOnClickPendingIntent(R.id.songTitle, pendingIntent);

                appWidgetManager.updateAppWidget(widgetId, views);
                continue;
            }

            // set up pending intents
            setClickIntent(views, R.id.buttonBackward, PlayerConstants.INTENT_DATA_BACKWARD, PlayerConstants.PLAYER_COMMAND_SKIPBACK);
            setClickIntent(views, R.id.buttonPausePlay, PlayerConstants.INTENT_DATA_PLAYPAUSE, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE);
            setClickIntent(views, R.id.buttonForward, PlayerConstants.INTENT_DATA_FORWARD, PlayerConstants.PLAYER_COMMAND_SKIPFORWARD);

            // show item view
            Intent detailsIntent = SongsFactory.getActivityIntent(context, song.getType());
            detailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent showItemIntent = PendingIntent.getActivity(context, 0, detailsIntent, 0);
            views.setOnClickPendingIntent(R.id.buttonDetails, showItemIntent);
            views.setOnClickPendingIntent(R.id.songTitle, showItemIntent);

            // show labels popup
            Intent labelIntent = new Intent(context, PlayerLabelsDialog.class);
            labelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent showLabelIntent = PendingIntent.getActivity(context, 0, labelIntent, 0);
            views.setOnClickPendingIntent(R.id.buttonAddLabel, showLabelIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

    private void updateSongDetails(PlayerServiceStatus playerStatus, Song song, RemoteViews views) {
        if (playerStatus.hasActiveSong()) {
            views.setTextViewText(R.id.songTitle, song.getTitle());

            int imageRes = playerStatus.isPlaying() ? R.drawable.ic_activity_pause : R.drawable.av_play_white;
            views.setImageViewResource(R.id.buttonPausePlay, imageRes);
        } else {
            views.setTextViewText(R.id.songTitle, "Newpsel - Dictations widget");
            views.setImageViewResource(R.id.buttonPausePlay, R.drawable.av_play_white);
        }
    }

    private void setClickIntent(RemoteViews views, int resourceId, String data, int command) {
        Intent intent = new Intent(mContext, PlayerService.class);
        intent.setData(Uri.parse(data));
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, 0);
        views.setOnClickPendingIntent(resourceId, pendingIntent);
    }

    private PendingIntent getPlayIntent() {
        Intent intent = new Intent(mContext, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE_LIST);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_TYPE, SongConstants.GRABBER_TYPE_DICTATE_ITEM);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, 0);

        return PendingIntent.getService(mContext, 0, intent, 0);
    }
}