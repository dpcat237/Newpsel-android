package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.DictateItemTable;
import com.dpcat237.nps.database.table.LaterItemTable;
import com.dpcat237.nps.database.table.SongTable;
import com.dpcat237.nps.model.DictateItem;
import com.dpcat237.nps.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaterItemRepository extends BaseRepository {
    private static final String TAG = "NPS:LaterItemRepository";
	private String[] allColumns = {
			LaterItemTable.COLUMN_ID,
            LaterItemTable.COLUMN_API_ID,
            LaterItemTable.COLUMN_ITEM_ID,
            LaterItemTable.COLUMN_FEED_ID,
            LaterItemTable.COLUMN_LATER_ID,
            LaterItemTable.COLUMN_IS_UNREAD,
            LaterItemTable.COLUMN_DATE_ADD,
            LaterItemTable.COLUMN_LANGUAGE,
            LaterItemTable.COLUMN_LINK,
            LaterItemTable.COLUMN_TITLE,
            LaterItemTable.COLUMN_CONTENT
			};
    private String[] syncColumns = {
            LaterItemTable.COLUMN_API_ID,
            LaterItemTable.COLUMN_IS_UNREAD,
    };
    private String[] forListColumns = {
            LaterItemTable.COLUMN_ID,
            LaterItemTable.COLUMN_API_ID,
            LaterItemTable.COLUMN_ITEM_ID,
            LaterItemTable.COLUMN_FEED_ID,
            LaterItemTable.COLUMN_IS_UNREAD,
            LaterItemTable.COLUMN_TITLE,
    };


	public LaterItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}
}
