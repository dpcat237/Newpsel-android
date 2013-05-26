package com.dpcat237.nps.repository;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dpcat237.nps.database.ItemTable;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.model.Item;

public class ItemRepository {

	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
			ItemTable.COLUMN_ID,
			ItemTable.COLUMN_API_ID,
			ItemTable.COLUMN_FEED_ID,
			ItemTable.COLUMN_TITLE,
			ItemTable.COLUMN_LINK,
			ItemTable.COLUMN_CONTENT,
			ItemTable.COLUMN_IS_STARED,
			ItemTable.COLUMN_IS_UNREAD,
			ItemTable.COLUMN_DATE_ADD
			};

	public ItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addItem(Item item){
		if (!checkItemExists(item.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(ItemTable.COLUMN_API_ID, item.getApiId());
			values.put(ItemTable.COLUMN_FEED_ID, item.getFeedId());
			values.put(ItemTable.COLUMN_TITLE, item.getTitle());
			values.put(ItemTable.COLUMN_LINK, item.getLink());
			values.put(ItemTable.COLUMN_CONTENT, item.getContent());
			values.put(ItemTable.COLUMN_IS_STARED, item.isStared());
			values.put(ItemTable.COLUMN_IS_UNREAD, item.isUnread());
			values.put(ItemTable.COLUMN_DATE_ADD, item.getDateAdd());
			database.insert(ItemTable.TABLE_ITEM, null, values);
		}
	}
	
	public Boolean checkItemExists(long apiId){
		Boolean result = false;
		String[] columns = new String[] {"api_id"};
		String where = ItemTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+apiId+""};
		
		Cursor cursor = database.query(ItemTable.TABLE_ITEM, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}
	
	public ArrayList<Item> getIsUnreadItems(Integer feedId, Boolean isUnread) {
		ArrayList<Item> items = new ArrayList<Item>();
		String where = "";
		String[] args = null;
		if (!isUnread) {
			Integer isUnr = 0;
			where = ItemTable.COLUMN_FEED_ID+"=? AND "+ItemTable.COLUMN_IS_UNREAD+"=?";
			 args = new String[] {""+feedId+"", ""+isUnr+""};
		} else {
			where = ItemTable.COLUMN_FEED_ID+"=?";
			args = new String[] {""+feedId+""};
		}
		String orderBy = "api_id DESC";

		Cursor cursor = database.query(ItemTable.TABLE_ITEM, allColumns, where, args, null, null, orderBy);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}

		cursor.close();
		return items;
	}
	
	public JSONArray getItemsToSync() {
		JSONArray items = new JSONArray();
		String where = ItemTable.COLUMN_IS_UNREAD+"=?";
		String[] args = new String[] {""+0+""};

		Cursor cursor = database.query(ItemTable.TABLE_ITEM, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject item = new JSONObject();
				item.put("id", cursor.getLong(1));
				item.put("is_stared", cursor.getInt(6));
				item.put("is_unread", cursor.getInt(7));
				items.put(item);
			} catch (JSONException e) {
				Log.e("ItemRepository - getItemsToSync","Error", e);
			}
			cursor.moveToNext();
		}

		cursor.close();
		return items;
	}
	
	public void removeReadItems() {
		String where = ItemTable.COLUMN_IS_UNREAD+"=?";
		String[] args = new String[] {""+0+""};
		database.delete(ItemTable.TABLE_ITEM, where, args);
	}
	
	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setApiId(cursor.getLong(1));
		item.setFeedId(cursor.getLong(2));
		item.setTitle(cursor.getString(3));
		item.setLink(cursor.getString(4));
		item.setContent(cursor.getString(5));
		item.setIsStared(cursor.getInt(6)>0);
		item.setIsUnread(cursor.getInt(7)>0);
		item.setDateAdd(cursor.getInt(8));
		return item;
	}
	
	public void readItem(Long itemId, Boolean isUnread) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_UNREAD, isUnread);
		String where = ItemTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+itemId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public void readFeedItems(Integer feedId) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_UNREAD, false);
		String where = ItemTable.COLUMN_FEED_ID+"=?";
		String[] args = new String[] {""+feedId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public void startedChange(Long itemId, Boolean isStared) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_STARED, isStared);
		String where = ItemTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+itemId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public Item getItem(Long itemId) {
		Item item = null;
		String where = ItemTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+itemId+""};
		Cursor cursor = database.query(ItemTable.TABLE_ITEM, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			item = cursorToItem(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return item;
	}
}
