package com.dpcat237.nps.behavior.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.model.Song;

public abstract class PlayerServiceCommands extends Service {
    public static void play(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY);
    }

    public static void play(Context context, String type, Integer listId) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY_LIST, type, listId);
    }

    public static void playpause(Context context, int pause_reason) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE, pause_reason);
    }

    public static void playpause(Context context, String type, Integer listId) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAYPAUSE_LIST, type, listId);
    }

    public static void pause(Context context, int pause_reason) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PAUSE, pause_reason);
    }

    public static void stop(Context context) {
        PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_STOP);
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

    public static void play(Context context, Song song) {
        //PlayerService.sendCommand(context, PlayerConstants.PLAYER_COMMAND_PLAY_SPECIFIC_SONG, song.getId());
    }

    protected static void sendCommand(Context context, int command) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        context.startService(intent);
    }

    protected static void sendCommand(Context context, int command, int arg) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, arg);
        context.startService(intent);
    }

    protected static void sendCommand(Context context, int command, String type, Integer listId) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND, command);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_TYPE, type);
        intent.putExtra(PlayerConstants.EXTRA_PLAYER_COMMAND_ARG, listId);
        context.startService(intent);
    }
}