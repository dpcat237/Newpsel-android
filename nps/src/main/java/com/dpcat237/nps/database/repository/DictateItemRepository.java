package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.DictateItemTable;
import com.dpcat237.nps.database.table.SongTable;
import com.dpcat237.nps.model.DictateItem;
import com.dpcat237.nps.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DictateItemRepository extends BaseRepository {
    private static final String TAG = "NPS:DictateItemRepository";
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
            DictateItemTable.COLUMN_TEXT
			};
    private String[] syncColumns = {
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_IS_UNREAD,
    };
    private String[] listItemColumns = {
            DictateItemTable.COLUMN_ID,
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_ITEM_ID,
            DictateItemTable.COLUMN_FEED_ID,
            DictateItemTable.COLUMN_IS_UNREAD,
            DictateItemTable.COLUMN_LANGUAGE,
            DictateItemTable.COLUMN_TITLE,
            DictateItemTable.COLUMN_CONTENT,
            DictateItemTable.COLUMN_TEXT
    };
    private String[] forListColumns = {
            DictateItemTable.COLUMN_ID,
            DictateItemTable.COLUMN_API_ID,
            DictateItemTable.COLUMN_ITEM_ID,
            DictateItemTable.COLUMN_FEED_ID,
            DictateItemTable.COLUMN_IS_UNREAD,
            DictateItemTable.COLUMN_TITLE,
    };


	public DictateItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void addItem(DictateItem item) {
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
        values.put(DictateItemTable.COLUMN_HAS_TTS_ERROR, item.hasTtsError());
        database.insert(DictateItemTable.TABLE_NAME, null, values);
	}
	
	public Boolean checkItemExists(Integer apiId){
		String[] columns = new String[] {DictateItemTable.COLUMN_API_ID};
		String where = DictateItemTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+apiId+""};
		Cursor cursor = database.query(DictateItemTable.TABLE_NAME, columns, where, args, null, null, null);
		
        return (cursor.getCount() > 0);
	}

    public Integer countUnreadItems() {
        String sql = "SELECT COUNT(tb."+DictateItemTable.COLUMN_ID+") AS total " +
                " FROM "+DictateItemTable.TABLE_NAME+" AS tb WHERE tb."+DictateItemTable.COLUMN_IS_UNREAD+"=1 AND tb."+DictateItemTable.COLUMN_HAS_TTS_ERROR+"=0;";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        Integer count = cursor.getInt(0);
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
        Log.d(TAG, "tut: getItemsForSync "+cursor.getCount());

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

    public void deleteItemsTtsError() {
        String where = DictateItemTable.COLUMN_HAS_TTS_ERROR+"=?";
        String[] args = new String[] {""+1+""};
        database.delete(DictateItemTable.TABLE_NAME, where, args);
    }

    public void deleteItem(Integer itemApiId) {
        String where = DictateItemTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        database.delete(DictateItemTable.TABLE_NAME, where, args);
    }

    public ArrayList<ListItem> getUnreadItems() {
        ArrayList<ListItem> items = new ArrayList<ListItem>();
        Integer isUnread = 1;
        Integer hasError = 0;
        String where = DictateItemTable.COLUMN_IS_UNREAD+"=? AND "+DictateItemTable.COLUMN_HAS_TTS_ERROR+"=?";
        String[] args = new String[] {""+isUnread+"", ""+hasError+""};
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

    public ArrayList<ListItem> getUnreadGrabbedItems() {
        ArrayList<ListItem> items = new ArrayList<ListItem>();
        String sql = "SELECT" +columnsToString("tb1", forListColumns)+
                "FROM "+DictateItemTable.TABLE_NAME+" AS tb1 "+
                "LEFT JOIN "+ SongTable.TABLE_SONG+" AS tb2 ON tb1."+DictateItemTable.COLUMN_ITEM_ID+"=tb2."+SongTable.COLUMN_ITEM_ID+" "+
                "WHERE tb1."+DictateItemTable.COLUMN_IS_UNREAD+"=1 AND tb2."+SongTable.COLUMN_IS_GRABBED+"=1"+";";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListItem item = cursorToItemForList(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    public Integer countUnreadGrabberItems() {
        String sql = "SELECT COUNT(tb1."+DictateItemTable.COLUMN_ID+") AS total "+
                "FROM "+DictateItemTable.TABLE_NAME+" AS tb1 "+
                "LEFT JOIN "+ SongTable.TABLE_SONG+" AS tb2 ON tb1."+DictateItemTable.COLUMN_ITEM_ID+"=tb2."+SongTable.COLUMN_ITEM_ID+" "+
                "WHERE tb1."+DictateItemTable.COLUMN_IS_UNREAD+"=1 AND tb2."+SongTable.COLUMN_IS_GRABBED+"=1"+";";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        Integer count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    private ListItem cursorToItemForList(Cursor cursor) {
        ListItem item = new ListItem();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setItemApiId(cursor.getInt(2));
        item.setListApiId(cursor.getInt(3));
        item.setIsUnread(cursor.getInt(4)>0);
        item.setTitle(cursor.getString(5));

        return item;
    }

    private ListItem cursorToListItem(Cursor cursor) {
        ListItem item = new ListItem();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setItemApiId(cursor.getInt(2));
        item.setListApiId(cursor.getInt(3));
        item.setIsUnread(cursor.getInt(4)>0);
        item.setLanguage(cursor.getString(5));
        item.setTitle(cursor.getString(6));
        item.setContent(cursor.getString(7));
        item.setText(cursor.getString(8));

        return item;
    }

    private DictateItem cursorToItem(Cursor cursor) {
        DictateItem item = new DictateItem();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setItemApiId(cursor.getInt(2));
        item.setFeedApiId(cursor.getInt(3));
        item.setLabelApiId(cursor.getInt(4));
        item.setIsUnread(cursor.getInt(5)>0);
        item.setDateAdd(cursor.getInt(6));
        item.setLanguage(cursor.getString(7));
        item.setLink(cursor.getString(8));
        item.setTitle(cursor.getString(9));
        item.setContent(cursor.getString(10));
        item.setText(cursor.getString(11));

        return item;
    }

    public DictateItem getItem(Integer itemApiId) {
        String where = DictateItemTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, allColumns, where, args, null, null, null);

        if (cursor.getCount() < 1) {
            return null;
        }
        cursor.moveToFirst();
        DictateItem item = cursorToItem(cursor);
        cursor.close();

        return item;
    }

    public DictateItem getItemByApiId(Integer apiId) {
        String where = DictateItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+apiId+""};
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, allColumns, where, args, null, null, null);

        if (cursor.getCount() < 1) {
            return null;
        }
        cursor.moveToFirst();
        DictateItem item = cursorToItem(cursor);
        cursor.close();

        return item;
    }

    public ListItem getListItem(Integer itemApiId) {
        ListItem listItem = null;
        String where = DictateItemTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        Cursor cursor = database.query(DictateItemTable.TABLE_NAME, listItemColumns, where, args, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            listItem = cursorToListItem(cursor);
        }
        cursor.close();

        return listItem;
    }

    public void readItem(Integer itemApiId, Boolean isUnread) {
        ContentValues values = new ContentValues();
        values.put(DictateItemTable.COLUMN_IS_UNREAD, isUnread);
        String where = DictateItemTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        database.update(DictateItemTable.TABLE_NAME, values, where, args);
    }

    public void markItemTtsError(Integer itemApiId ) {
        ContentValues values = new ContentValues();
        values.put(DictateItemTable.COLUMN_HAS_TTS_ERROR, true);
        String where = DictateItemTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        database.update(DictateItemTable.TABLE_NAME, values, where, args);
    }
}
