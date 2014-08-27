package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.ItemTable;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.common.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemRepository extends BaseRepository {
	private String[] allColumns = {
			ItemTable.COLUMN_ID,
			ItemTable.COLUMN_API_ID,
            ItemTable.COLUMN_UI_ID,
			ItemTable.COLUMN_FEED_ID,
			ItemTable.COLUMN_TITLE,
			ItemTable.COLUMN_LINK,
			ItemTable.COLUMN_CONTENT,
			ItemTable.COLUMN_IS_STARED,
			ItemTable.COLUMN_IS_UNREAD,
			ItemTable.COLUMN_DATE_ADD,
            ItemTable.COLUMN_LANGUAGE
			};
    private String[] listItemColumns = {
            ItemTable.COLUMN_ID,
            ItemTable.COLUMN_API_ID,
            ItemTable.COLUMN_FEED_ID,
            ItemTable.COLUMN_TITLE,
            ItemTable.COLUMN_LANGUAGE
    };
    private String[] listItemContentColumns = {
            ItemTable.COLUMN_ID,
            ItemTable.COLUMN_API_ID,
            ItemTable.COLUMN_TITLE,
            ItemTable.COLUMN_CONTENT,
            ItemTable.COLUMN_LANGUAGE
    };
    private String[] syncColumns = {
            ItemTable.COLUMN_API_ID,
            ItemTable.COLUMN_UI_ID,
            ItemTable.COLUMN_IS_STARED,
            ItemTable.COLUMN_IS_UNREAD,
    };


	public ItemRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void addItem(Item item) {
		if (!checkItemExists(item.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(ItemTable.COLUMN_API_ID, item.getApiId());
            values.put(ItemTable.COLUMN_UI_ID, item.getUiId());
			values.put(ItemTable.COLUMN_FEED_ID, item.getFeedId());
			values.put(ItemTable.COLUMN_TITLE, item.getTitle());
			values.put(ItemTable.COLUMN_LINK, item.getLink());
			values.put(ItemTable.COLUMN_CONTENT, item.getContent());
			values.put(ItemTable.COLUMN_IS_STARED, item.isStared());
			values.put(ItemTable.COLUMN_IS_UNREAD, item.isUnread());
			values.put(ItemTable.COLUMN_DATE_ADD, item.getDateAdd());
            values.put(ItemTable.COLUMN_LANGUAGE, item.getLanguage());
			database.insert(ItemTable.TABLE_ITEM, null, values);
		}
	}
	
	public Boolean checkItemExists(long apiId){
		Boolean result = false;
		String[] columns = new String[] {ItemTable.COLUMN_API_ID};
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
		if (isUnread) {
			Integer isUnr = 1;
			where = ItemTable.COLUMN_FEED_ID+"=? AND "+ItemTable.COLUMN_IS_UNREAD+"=?";
			args = new String[] {""+feedId+"", ""+isUnr+""};
		} else {
			where = ItemTable.COLUMN_FEED_ID+"=?";
			args = new String[] {""+feedId+""};
		}
		String orderBy = ItemTable.COLUMN_DATE_ADD+" DESC";

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
		Cursor cursor = database.query(ItemTable.TABLE_ITEM, syncColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject item = new JSONObject();
				item.put("id", cursor.getLong(0));
                item.put("ui_id", cursor.getLong(1));
				item.put("is_stared", cursor.getInt(2));
				item.put("is_unread", cursor.getInt(3));
				items.put(item);
			} catch (JSONException e) {
				Log.e("ItemRepository - getItemsToSync","Error", e);
			}
			cursor.moveToNext();
		}
		cursor.close();

		return items;
	}

    public ArrayList<ListItem> getUnreadItemsTitles(long feedId) {
        ArrayList<ListItem> items = new ArrayList<ListItem>();
        String where = "";
        String[] args = null;
        Integer isUnr = 1;
        where = ItemTable.COLUMN_FEED_ID+"=? AND "+ItemTable.COLUMN_IS_UNREAD+"=?";
        args = new String[] {""+feedId+"", ""+isUnr+""};
        String orderBy = ItemTable.COLUMN_DATE_ADD+" DESC";

        Cursor cursor = database.query(ItemTable.TABLE_ITEM, listItemColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItemTitle(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    public ListItem getListItem(Integer itemApiId) {
        ListItem listItem = null;
        String where = ItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        Cursor cursor = database.query(ItemTable.TABLE_ITEM, listItemContentColumns, where, args, null, null, null);

        cursor.moveToFirst();
        listItem = cursorToListenItem(cursor);
        cursor.close();

        return listItem;
    }
	
	public void removeReadItems() {
		String where = ItemTable.COLUMN_IS_UNREAD+"=?";
		String[] args = new String[] {""+0+""};
		database.delete(ItemTable.TABLE_ITEM, where, args);
	}
	
	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getInt(0));
		item.setApiId(cursor.getInt(1));
		item.setUiId(cursor.getLong(2));
        item.setFeedId(cursor.getInt(3));
		item.setTitle(cursor.getString(4));
		item.setLink(cursor.getString(5));
		item.setContent(cursor.getString(6));
		item.setIsStared(cursor.getInt(7)>0);
		item.setIsUnread(cursor.getInt(8)>0);
		item.setDateAdd(cursor.getInt(9));
        item.setLanguage(cursor.getString(10));

		return item;
	}

    private Item cursorToItemTitle(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getInt(0));
        item.setApiId(cursor.getInt(1));
        item.setItemApiId(cursor.getInt(1));
        item.setFeedId(cursor.getInt(2));
        item.setTitle(cursor.getString(3));
        item.setLanguage(cursor.getString(4));

        return item;
    }

    private ListItem cursorToListenItem(Cursor cursor) {
        ListItem listenItem = new ListItem();
        listenItem.setId(cursor.getInt(0));
        listenItem.setApiId(cursor.getInt(1));
        listenItem.setItemApiId(cursor.getInt(1));
        listenItem.setTitle(cursor.getString(2));
        listenItem.setContent(cursor.getString(3));
        listenItem.setLanguage(cursor.getString(4));

        return listenItem;
    }
	
	public void readItem(Integer itemApiId, Boolean isUnread) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_UNREAD, isUnread);
		String where = ItemTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+itemApiId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public void readFeedItems(Integer feedId) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_UNREAD, false);
		String where = ItemTable.COLUMN_FEED_ID+"=?";
		String[] args = new String[] {""+feedId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public void staredChange(Integer itemId, Boolean isStared) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_IS_STARED, isStared);
		String where = ItemTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+itemId+""};
		database.update(ItemTable.TABLE_ITEM, values, where, args);
	}
	
	public Item getItem(Integer itemApiId) {
		Item item = null;
		String where = ItemTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+itemApiId+""};
		Cursor cursor = database.query(ItemTable.TABLE_ITEM, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			item = cursorToItem(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return item;
	}

    public Integer countUnreadItems() {
        String sql = "SELECT COUNT(tb."+ItemTable.COLUMN_ID+") AS total " +
                " FROM "+ItemTable.TABLE_ITEM+" AS tb WHERE tb."+ItemTable.COLUMN_IS_UNREAD+"=1;";
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        Integer count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public void deleteItem(Integer itemApiId) {
        String where = ItemTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+itemApiId+""};
        database.delete(ItemTable.TABLE_ITEM, where, args);
    }
}
