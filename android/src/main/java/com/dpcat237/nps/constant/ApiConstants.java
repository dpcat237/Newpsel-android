package com.dpcat237.nps.constant;

public class ApiConstants {
    public static final String TYPE_SYNC_DICTATE_ITEMS = "sync_dictate_items";
    public static final String DEVICE_ID = "deviceId";

    /* URLs */
    public static final String API_URL = "https://api.newpsel.com/";
    public static final String URL_ADD_FEED = API_URL+"feed/add";
    public static final String URL_ADD_GCM_ID = API_URL+"gcm/add_id";
    public static final String URL_GET_FEEDS = API_URL+"feed/sync";
    public static final String URL_RECOVERY_PASSWORD = API_URL+"user/password_recovery";
    public static final String URL_SIGN_IN = API_URL+"user/login";
    public static final String URL_SIGN_UP = API_URL+"user/register";
    public static final String URL_SYNC_DICTATE_ITEMS = API_URL+"saved_article/dictation/sync";
    public static final String URL_SYNC_ITEMS_UNREAD = API_URL+"article/sync";
    public static final String URL_SYNC_LABELS = API_URL+"tag/sync";
    public static final String URL_SYNC_LABEL_ITEMS = API_URL+"saved_article/add_saved";
    public static final String URL_SYNC_LATER_ITEMS = API_URL+"saved_article/sync";
    public static final String URL_SYNC_SHARED_ITEMS = API_URL+"saved_article/add_shared";

    /* ERRORS */
    public static final int ERROR_LOGIN_DATA = 301;
    public static final int ERROR_NO_LOGGED = 303;
}
