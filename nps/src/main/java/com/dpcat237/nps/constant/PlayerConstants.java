package com.dpcat237.nps.constant;

public class PlayerConstants {
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_UPDATE = 1;
    public static final int SUBSCRIPTION_UPDATE_ERROR = 2;
    public static final int NOTIFICATION_PLAYING = 3;

    //command
    public static final int PLAYER_COMMAND_SKIPTOEND = 0;
    public static final int PLAYER_COMMAND_RESTART = 1;
    public static final int PLAYER_COMMAND_SKIPBACK = 2;
    public static final int PLAYER_COMMAND_SKIPFORWARD = 3;
    public static final int PLAYER_COMMAND_PLAYPAUSE = 4;
    public static final int PLAYER_COMMAND_PLAY = 5;
    public static final int PLAYER_COMMAND_PAUSE = 6;
    public static final int PLAYER_COMMAND_SKIPTO = 7;
    public static final int PLAYER_COMMAND_PLAY_LIST = 8;
    public static final int PLAYER_COMMAND_PLAY_SPECIFIC_SONG = 9;
    public static final int PLAYER_COMMAND_STOP = 10;
    public static final int PLAYER_COMMAND_PLAYSTOP = 11;
    public static final int PLAYER_COMMAND_PLAYPAUSE_LIST = 12;

    //status
    public static final int PLAYER_STATUS_QUEUEEMPTY = 0;
    public static final int PLAYER_STATUS_STOPPED = 1;
    public static final int PLAYER_STATUS_PAUSED = 2;
    public static final int PLAYER_STATUS_PLAYING = 3;

    //reasons for pausing
    public static final int PAUSE_AUDIOFOCUS = 0;
    public static final int PAUSE_MEDIABUTTON = 1;
    public static final int PAUSE_NOTIFICATION = 2;
    public static final int PAUSE_COUNT = 3;
    public static final int PAUSE_ACTIONBUTTON = 4;

    //temporary variables
    public static final String EXTRA_PLAYER_COMMAND = "com.dpcat237.nps.player_command";
    public static final String EXTRA_PLAYER_TYPE = "com.dpcat237.nps.player_type";
    public static final String EXTRA_PLAYER_COMMAND_ARG = "com.dpcat237.nps.player_arg";
}
