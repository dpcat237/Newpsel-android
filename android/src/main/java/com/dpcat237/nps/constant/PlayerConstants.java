package com.dpcat237.nps.constant;

public class PlayerConstants {
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
    public static final int PLAYER_COMMAND_PLAYPAUSE_DICTATION = 13;

    //status
    public static final int STATUS_QUEUEEMPTY = 0;
    public static final int STATUS_STOPPED = 1;
    public static final int STATUS_PAUSED = 2;
    public static final int STATUS_PLAYING = 3;

    //temporary variables
    public static final String EXTRA_PLAYER_COMMAND = "com.dpcat237.nps.player_command";
    public static final String EXTRA_PLAYER_TYPE = "com.dpcat237.nps.player_type";
    public static final String EXTRA_PLAYER_COMMAND_ARG = "com.dpcat237.nps.player_arg";

    //intent data
    public static final String INTENT_DATA_PLAYPAUSE = "newpsel://playercommand/playpause";
    public static final String INTENT_DATA_FORWARD = "newpsel://playercommand/forward";
    public static final String INTENT_DATA_BACKWARD = "newpsel://playercommand/backward";
    public static final String INTENT_DATA_STOP = "newpsel://playercommand/stop";
}
