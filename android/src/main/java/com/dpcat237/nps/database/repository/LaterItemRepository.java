package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.common.model.LaterItem;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.LaterItemTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaterItemRepository extends BaseRepository {
    private static final String TAG = "NPS:LaterItemRepository";
	private String[] allColumns = {
            LaterItemTable.COLUMN_API_ID,
            LaterItemTable.COLUMN_ITEM_ID,
            LaterItemTable.COLUMN_LATER_ID,
            LaterItemTable.COLUMN_IS_UNREAD,
            LaterItemTable.COLUMN_DATE_ADD,
            LaterItemTable.COLUMN_LINK,
            LaterItemTable.COLUMN_TITLE,
            LaterItemTable.COLUMN_CONTENT
			};
    private String[] forListColumns = {
            LaterItemTable.COLUMN_ID,
            LaterItemTable.COLUMN_API_ID,
            LaterItemTable.COLUMN_ITEM_ID,
            LaterItemTable.COLUMN_LATER_ID,
            LaterItemTable.COLUMN_IS_UNREAD,
            LaterItemTable.COLUMN_LINK,
            LaterItemTable.COLUMN_TITLE,
    };


	public LaterItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

    public void addItem(LaterItem item) {
        if (!checkItemExists(item.getApiId())) {
            ContentValues values = new ContentValues();
            values.put(LaterItemTable.COLUMN_API_ID, item.getApiId());
            values.put(LaterItemTable.COLUMN_ITEM_ID, item.getItemApiId());
            values.put(LaterItemTable.COLUMN_FEED_ID, item.getFeedApiId());
            values.put(LaterItemTable.COLUMN_LATER_ID, item.getLabelApiId());
            values.put(LaterItemTable.COLUMN_TITLE, item.getTitle());
            values.put(LaterItemTable.COLUMN_LINK, item.getLink());
            values.put(LaterItemTable.COLUMN_CONTENT, item.getContent());
            values.put(LaterItemTable.COLUMN_IS_UNREAD, item.isUnread());
            values.put(LaterItemTable.COLUMN_DATE_ADD, item.getDateAdd());
            values.put(LaterItemTable.COLUMN_LANGUAGE, item.getLanguage());
            database.insert(LaterItemTable.TABLE_NAME, null, values);
        }
    }

    public Boolean checkItemExists(long apiId){
        Boolean result = false;
        String[] columns = new String[] {LaterItemTable.COLUMN_API_ID};
        String where = LaterItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+apiId+""};

        Cursor cursor = database.query(LaterItemTable.TABLE_NAME, columns, where, args, null, null, null);
        if (cursor.getCount() > 0) {
            result = true;
        }

        return result;
    }

    public Integer countUnreadItems() {
        String sql = "SELECT COUNT(tb."+LaterItemTable.COLUMN_ID+") AS total " +
                " FROM "+LaterItemTable.TABLE_NAME+" AS tb WHERE tb."+LaterItemTable.COLUMN_IS_UNREAD+"=1;";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        Integer count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public JSONArray getItemsToSync(String labels) {
        JSONArray items = new JSONArray();
        String sql = "SELECT tb."+LaterItemTable.COLUMN_API_ID+", tb."+LaterItemTable.COLUMN_IS_UNREAD+
                " FROM "+LaterItemTable.TABLE_NAME+" AS tb WHERE tb."+LaterItemTable.COLUMN_LATER_ID+" IN("+labels+");";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                JSONObject item = new JSONObject();
                item.put("api_id", cursor.getLong(0));
                item.put("is_unread", cursor.getInt(1));
                items.put(item);
            } catch (JSONException e) {
                Log.e(TAG, "tut: getItemsToSync ", e);
            }
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    public void deleteItem(Integer apiId) {
        String where = LaterItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+apiId+""};
        database.delete(LaterItemTable.TABLE_NAME, where, args);
    }

    public void removeReadItems() {
        String where = LaterItemTable.COLUMN_IS_UNREAD+"=?";
        String[] args = new String[] {""+0+""};
        database.delete(LaterItemTable.TABLE_NAME, where, args);
    }

    public ArrayList<LaterItem> getForList(Integer labelApiId, Boolean unread) {
        ArrayList<LaterItem> items = new ArrayList<LaterItem>();
        String where;
        String[] args;
        if (unread) {
            where = LaterItemTable.COLUMN_LATER_ID+"=? AND "+LaterItemTable.COLUMN_IS_UNREAD+"=?";
            args = new String[] {labelApiId.toString(), ""+1+""};
        } else {
            where = LaterItemTable.COLUMN_LATER_ID+"=?";
            args = new String[] {labelApiId.toString()};
        }
        String orderBy = LaterItemTable.COLUMN_DATE_ADD+" DESC";
        Cursor cursor = database.query(LaterItemTable.TABLE_NAME, forListColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LaterItem item = cursorForList(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    private LaterItem cursorForList(Cursor cursor) {
        LaterItem item = new LaterItem();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setItemApiId(cursor.getInt(2));
        item.setLabelApiId(cursor.getInt(3));
        item.setIsUnread(cursor.getInt(4)>0);
        item.setLink(cursor.getString(5));
        item.setTitle(cursor.getString(6));

        return item;
    }

    public void readItem(Integer apiId, Boolean isUnread) {
        ContentValues values = new ContentValues();
        values.put(LaterItemTable.COLUMN_IS_UNREAD, isUnread);
        String where = LaterItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+apiId+""};
        database.update(LaterItemTable.TABLE_NAME, values, where, args);
    }

    public LaterItem getItem(Integer apiId) {
        String where = LaterItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+apiId+""};
        Cursor cursor = database.query(LaterItemTable.TABLE_NAME, allColumns, where, args, null, null, null);
        cursor.moveToFirst();
        LaterItem item = cursorToItem(cursor);
        cursor.moveToNext();

        cursor.close();
        return item;
    }

    private LaterItem cursorToItem(Cursor cursor) {
        LaterItem item = new LaterItem();
        item.setApiId(cursor.getInt(0));
        item.setItemApiId(cursor.getInt(1));
        item.setLabelApiId(cursor.getInt(2));
        item.setIsUnread(cursor.getInt(3)>0);
        item.setDateAdd(cursor.getInt(4));
        item.setLink(cursor.getString(5));
        item.setTitle(cursor.getString(6));
        item.setContent(cursor.getString(7));

        return item;
    }
}
