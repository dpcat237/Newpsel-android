package com.dpcat237.nps.common.constant;

public class BroadcastConstants {
    /* channel*/
    public static final String PLAYER_ACTIVITY = "broadcast.player.activity";
    public static final String MAIN_ACTIVITY = "broadcast.main.activity";
    public static final String ITEM_ACTIVITY = "broadcast.item.activity";

    /* message key */
    public static final String PLAYER_ACTIVITY_MESSAGE = "broadcast.player.activity.message";
    public static final String MAIN_ACTIVITY_MESSAGE = "broadcast.main.activity.message";
    public static final String ITEM_ACTIVITY_MESSAGE = "broadcast.item.activity.message";

    /* MESSAGES */
    /* App */
    public static final String COMMAND_A_MAIN_RELOAD_ITEMS = "broadcast.app.activity.main.reload.items";
    public static final String COMMAND_A_MAIN_RELOAD_LATER = "broadcast.app.activity.main.reload.later";
    public static final String COMMAND_A_MAIN_RELOAD_DICTATIONS = "broadcast.app.activity.main.reload.dictations";
    public static final String COMMAND_A_ITEM_TTS_ACTIVE = "broadcast.app.activity.item.tts.active";
    public static final String COMMAND_A_ITEM_TTS_FINISHED = "broadcast.app.activity.item.tts.finished";
    /* Wear */
    public static final String COMMAND_W_UPDATE_STATE = "broadcast.wear.player.update.state";
    public static final String COMMAND_W_PLAYING = "broadcast.wear.player.playing";
    public static final String COMMAND_W_PAUSED = "broadcast.wear.player.paused";
    public static final String COMMAND_W_STOP = "broadcast.wear.player.stop";
}
