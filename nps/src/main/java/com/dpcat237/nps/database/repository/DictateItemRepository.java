package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dpcat237.nps.database.table.DictateItemTable;
import com.dpcat237.nps.database.table.NPSDatabase;
import com.dpcat237.nps.model.DictateItem;
import com.dpcat237.nps.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DictateItemRepository {
    private static final String TAG = "NPS:DictateItemRepository";
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
			DictateItemTable.COLUMN_ID,
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_ITEM_ID,
            DictateItemTable.COLUMN_FEED_ID,
            DictateItemTable.COLUMN_LATER_ID,
            DictateItemTable.COLUMN_IS_UNREAD,
            DictateItemTable.COLUMN_DATE_ADD,
            DictateItemTable.COLUMN_LANGUAGE,
            DictateItemTable.COLUMN_LINK,
            DictateItemTable.COLUMN_TITLE,
            DictateItemTable.COLUMN_CONTENT,
            DictateItemTable.COLUMN_TEXT,
			};
    private String[] syncColumns = {
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_IS_UNREAD
    };
    private String[] listItemColumns = {
            DictateItemTable.COLUMN_ID,
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_LANGUAGE,
            DictateItemTable.COLUMN_TITLE,
            DictateItemTable.COLUMN_CONTENT,
            DictateItemTable.COLUMN_TEXT
    };


	public DictateItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addItem(DictateItem item) {
		if (!checkItemExists(item.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(DictateItemTable.COLUMN_API_ID, item.getApiId());
            values.put(DictateItemTable.COLUMN_ITEM_ID, item.getItemApiId());
            values.put(DictateItemTable.COLUMN_FEED_ID, item.getFeedApiId());
            values.put(DictateItemTable.COLUMN_LATER_ID, item.getLabelApiId());
            values.put(DictateItemTable.COLUMN_IS_UNREAD, item.isUnread());
            values.put(DictateItemTable.COLUMN_DATE_ADD, item.getDateAdd());
            values.put(DictateItemTable.COLUMN_LANGUAGE, item.getLanguage());
            values.put(DictateItemTable.COLUMN_LINK, item.getLink());
            values.put(DictateItemTable.COLUMN_TITLE, item.getTitle());
            values.put(DictateItemTable.COLUMN_CONTENT, item.getContent());
            values.put(DictateItemTable.COLUMN_TEXT, item.getText());
			database.insert(DictateItemTable.TABLE_NAME, null, values);
		}
	}
	
	public Boolean checkItemExists(Integer apiId){
		Boolean result = false;
		String[] columns = new String[] {DictateItemTable.COLUMN_API_ID};
		String where = DictateItemTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+apiId+""};
		Cursor cursor = database.query(DictateItemTable.TABLE_NAME, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}

    public Integer countUnreadItems() {
        Integer count = 0;
        String sql = "SELECT COUNT(tb."+DictateItemTable.COLUMN_ID+") AS total " +
                " FROM "+DictateItemTable.TABLE_NAME+" AS tb WHERE tb."+DictateItemTable.COLUMN_IS_UNREAD+"=1;";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public JSONArray getItemsForSync() {
        JSONArray items = new JSONArray();
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, syncColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.put(cursorToItemSync(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return items;
    }

    private JSONObject cursorToItemSync(Cursor cursor) {
        JSONObject item = new JSONObject();
        try {
            item.put("api_id", cursor.getInt(0));
            item.put("is_unread", cursor.getInt(1));
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return item;
    }

    public void deleteReadItems() {
        String where = DictateItemTable.COLUMN_IS_UNREAD+"=?";
        String[] args = new String[] {""+0+""};
        database.delete(DictateItemTable.TABLE_NAME, where, args);
    }

    public void deleteItem(Integer itemApiId) {
        String where = DictateItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        database.delete(DictateItemTable.TABLE_NAME, where, args);
    }

    public ArrayList<ListItem> getUnreadItemsByFeed(Integer feedId) {
        ArrayList<ListItem> items = new ArrayList<ListItem>();
        Integer isUnread = 1;
        String where = DictateItemTable.COLUMN_FEED_ID+"=? AND "+DictateItemTable.COLUMN_IS_UNREAD+"=?";
        String[] args = new String[] {""+feedId+"", ""+isUnread+""};
        String orderBy = DictateItemTable.COLUMN_DATE_ADD+" DESC";
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, listItemColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListItem item = cursorToListItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    private ListItem cursorToListItem(Cursor cursor) {
        ListItem item = new ListItem();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setLanguage(cursor.getString(2));
        item.setTitle(cursor.getString(3));
        item.setContent(cursor.getString(4));
        item.setText(cursor.getString(5));

        return item;
    }

    public ListItem getListItem(Integer itemId) {
        String where = DictateItemTable.COLUMN_ID+"=?";
        String[] args = new String[] {""+itemId+""};
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, listItemColumns, where, args, null, null, null);

        cursor.moveToFirst();
        ListItem listItem = cursorToListItem(cursor);
        cursor.close();

        return listItem;
    }

    public void readItem(Integer itemId, Boolean isUnread) {
        ContentValues values = new ContentValues();
        values.put(DictateItemTable.COLUMN_IS_UNREAD, isUnread);
        String where = DictateItemTable.COLUMN_ID+"=?";
        String[] args = new String[] {""+itemId+""};
        database.update(DictateItemTable.TABLE_NAME, values, where, args);
    }
}
