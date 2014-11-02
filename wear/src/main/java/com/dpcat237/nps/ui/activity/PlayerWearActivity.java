package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.manager.PlayerStateManager;
import com.dpcat237.nps.common.constant.BroadcastConstants;

public class PlayerWearActivity extends Activity {
    private static final String TAG = "NPSW:PlayerWearActivity";
    private Context mContext;
    private BroadcastReceiver receiver;
    private PlayerStateManager stateManager;
    private TextView listTitleTxt;
    private TextView titleTxt;
    private ImageView playPauseBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_player_wear);
        View mView = this.findViewById(android.R.id.content).getRootView();
        stateManager = PlayerStateManager.getInstance(mContext);

        listTitleTxt = (TextView) mView.findViewById(R.id.songListTitle);
        titleTxt = (TextView) mView.findViewById(R.id.songTitle);
        playPauseBtn = (ImageView) mView.findViewById(R.id.buttonPlayPause);
        updateCurrentSong();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastUpdate(intent.getStringExtra(BroadcastConstants.PLAYER_ACTIVITY_MESSAGE));
            }
        };
    }

    public void onLabelClick(View view) {
        Intent intent = new Intent(this, LabelsWearActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(BroadcastConstants.PLAYER_ACTIVITY));
        stateManager.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentSong();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stateManager.destroy();
        super.onDestroy();
    }

    private void updateCurrentSong() {
        Log.d(TAG, "tut: updateCurrentSong "+stateManager.hasData().toString()+" - "+stateManager.isPlaying().toString());
        if (stateManager.hasData()) {
            listTitleTxt.setText(stateManager.getCurrentSong().getListTitle());
            titleTxt.setText(stateManager.getCurrentSong().getTitle());
            playPauseBtn.setImageResource(R.drawable.activity_button_pause);
        } else {
            listTitleTxt.setText(mContext.getString(R.string.player_activity_empty_list_title));
            titleTxt.setText("");
            playPauseBtn.setImageResource(R.drawable.activity_button_play);
        }
    }

    public void onPlayPause(View view) {
        if (stateManager.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.activity_button_play);
            stateManager.sendPause();
        } else {
            playPauseBtn.setImageResource(R.drawable.activity_button_pause);
            stateManager.sendPlay();
        }
    }

    public void onBackward(View view) {
        if (stateManager.isPlaying()) {
            stateManager.sendBackward();
        }
    }

    public void onForward(View view) {
        if (stateManager.isPlaying()) {
            stateManager.sendForward();
        }
    }

    public void broadcastUpdate(String command) {
        if (command.equals(BroadcastConstants.COMMAND_W_UPDATE_STATE)) {
            updateCurrentSong();
        } else if (command.equals(BroadcastConstants.COMMAND_W_PLAYING)) {
            playPauseBtn.setImageResource(R.drawable.activity_button_pause);
        } else if (command.equals(BroadcastConstants.COMMAND_W_PAUSED)) {
            playPauseBtn.setImageResource(R.drawable.activity_button_play);
        } else if (command.equals(BroadcastConstants.COMMAND_W_STOP)) {
            updateCurrentSong();
            stateManager.stop();
            finish();
        }
    }
}
