package com.dpcat237.nps.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dpcat237.nps.database.FeedTable;
import com.dpcat237.nps.database.ItemTable;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.List;

import java.util.ArrayList;

public class FeedRepository {
    private static final String TAG = "NPS:FeedRepository";
	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
				FeedTable.COLUMN_ID,
				FeedTable.COLUMN_API_ID,
				FeedTable.COLUMN_TITLE,
				FeedTable.COLUMN_WEBSITE,
				FeedTable.COLUMN_FAVICON,
				FeedTable.COLUMN_UNREAD_COUNT
			};
    private String[] listColumns = {
            FeedTable.COLUMN_ID,
            FeedTable.COLUMN_API_ID,
            FeedTable.COLUMN_TITLE,
    };

	public FeedRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		dbHelper.onCreate(database);
	}

	public void close() {
		dbHelper.close();
	}
	
	public void create(){
		dbHelper.onCreate(database);
	}
	
	public void drop(){
		dbHelper.onDelete(database);
	}
	
	public void addFeed(Feed feed){
		if (!checkFeedExists(feed.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(FeedTable.COLUMN_API_ID, feed.getApiId());
			values.put(FeedTable.COLUMN_TITLE, feed.getTitle());
			values.put(FeedTable.COLUMN_WEBSITE, feed.getWebsite());
			values.put(FeedTable.COLUMN_FAVICON, feed.getFavicon());
			values.put(FeedTable.COLUMN_UNREAD_COUNT, 0 );
			database.insert(FeedTable.TABLE_FEED, null, values);
		}
	}
	
	public void updateFeed(Feed feed) {
		ContentValues values = new ContentValues();
		values.put(FeedTable.COLUMN_TITLE, feed.getTitle());
		values.put(FeedTable.COLUMN_WEBSITE, feed.getWebsite());
		values.put(FeedTable.COLUMN_FAVICON, feed.getFavicon());
		String where = FeedTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+feed.getApiId()+""};
		database.update(FeedTable.TABLE_FEED, values, where, args);
	}
	
	public Boolean checkFeedExists(long feedId){
		Boolean result = false;
		String[] columns = new String[] {"api_id"};
		String where = "api_id=?";
		String[] args = new String[] {""+feedId+""};
		
		Cursor cursor = database.query(FeedTable.TABLE_FEED, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}

	public Feed createFeed(String feed) {
		ContentValues values = new ContentValues();
		values.put(FeedTable.COLUMN_TITLE, feed);
		long insertId = database.insert(FeedTable.TABLE_FEED, null,
				values);
		Cursor cursor = database.query(FeedTable.TABLE_FEED,
				allColumns, FeedTable.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Feed newFeed = cursorToFeed(cursor);
		cursor.close();

		return newFeed;
	}

	public void deleteFeed(Feed feed) {
		long id = feed.getId();
		System.out.println("Feed deleted with id: " + id);
		database.delete(FeedTable.TABLE_FEED, FeedTable.COLUMN_ID + " = " + id, null);
	}

	public ArrayList<Feed> getAllFeeds() {
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		String orderBy = FeedTable.COLUMN_TITLE+" ASC";
		Cursor cursor = database.query(FeedTable.TABLE_FEED, allColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Feed feed = cursorToFeed(cursor);
			feeds.add(feed);
			cursor.moveToNext();
		}
		cursor.close();

		return feeds;
	}
	
	public ArrayList<Feed> getAllFeedsUnread() {
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		String where = FeedTable.COLUMN_UNREAD_COUNT+">?";
		String[] args = new String[] {""+0+""};
		String orderBy = FeedTable.COLUMN_TITLE+" ASC";
		Cursor cursor = database.query(FeedTable.TABLE_FEED, allColumns, where, args, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Feed feed = cursorToFeed(cursor);
			feeds.add(feed);
			cursor.moveToNext();
		}
		cursor.close();

		return feeds;
	}

    public ArrayList<Feed> getAllFeedsWithItems() {
        ArrayList<Feed> feeds = new ArrayList<Feed>();
        String where = FeedTable.COLUMN_ITEMS_COUNT+">?";
        String[] args = new String[] {""+0+""};
        String orderBy = FeedTable.COLUMN_TITLE+" ASC";
        Cursor cursor = database.query(FeedTable.TABLE_FEED, allColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Feed feed = cursorToFeed(cursor);
            feeds.add(feed);
            cursor.moveToNext();
        }
        cursor.close();

        return feeds;
    }

    public ArrayList<List> getLists() {
        ArrayList<List> feeds = new ArrayList<List>();
        String where = FeedTable.COLUMN_UNREAD_COUNT+">?";
        String[] args = new String[] {""+0+""};
        String orderBy = FeedTable.COLUMN_TITLE+" ASC";
        Cursor cursor = database.query(FeedTable.TABLE_FEED, listColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Feed feed = cursorToList(cursor);
            feeds.add(feed);
            cursor.moveToNext();
        }

        cursor.close();

        return feeds;
    }
	
	public Feed getFeed(Integer feedApiId) {
		Feed feed = null;
		String where = FeedTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+feedApiId+""};
		
		Cursor cursor = database.query(FeedTable.TABLE_FEED, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			feed = cursorToFeed(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return feed;
	}

	private Feed cursorToFeed(Cursor cursor) {
		Feed feed = new Feed();
		feed.setId(cursor.getInt(0));
		feed.setApiId(cursor.getInt(1));
		feed.setTitle(cursor.getString(2));
		feed.setWebsite(cursor.getString(3));
		feed.setFavicon(cursor.getString(4));
		feed.setUnreadCount(cursor.getInt(5));

		return feed;
	}

    private Feed cursorToList(Cursor cursor) {
        Feed feed = new Feed();
        feed.setId(cursor.getInt(0));
        feed.setApiId(cursor.getInt(1));
        feed.setTitle(cursor.getString(2));

        return feed;
    }
	
	private ArrayList<Feed> getUnreadCount () {
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		String sql = "SELECT tb1."+ItemTable.COLUMN_FEED_ID + ", " +
				"(SELECT COUNT(tb2."+ItemTable.COLUMN_ID+") AS total FROM "+ItemTable.TABLE_ITEM+" AS tb2 " +
						"WHERE tb2."+ItemTable.COLUMN_FEED_ID+"=tb1."+ItemTable.COLUMN_FEED_ID+" AND tb2."+ItemTable.COLUMN_IS_UNREAD+"=1) AS countUnread, " +
                "(SELECT COUNT(tb2."+ItemTable.COLUMN_ID+") AS total FROM "+ItemTable.TABLE_ITEM+" AS tb2 " +
                    "WHERE tb2."+ItemTable.COLUMN_FEED_ID+"=tb1."+ItemTable.COLUMN_FEED_ID+") AS countItems " +
				" FROM "+ItemTable.TABLE_ITEM+" AS tb1 GROUP BY tb1."+ItemTable.COLUMN_FEED_ID;
		Cursor cursor = database.rawQuery(sql, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Feed feed = new Feed();
			feed.setApiId(cursor.getInt(0));
			feed.setUnreadCount(cursor.getInt(1));
            feed.setItemsCount(cursor.getInt(2));
			feeds.add(feed);
			cursor.moveToNext();
		}
        cursor.close();
		
		return feeds;
	}
	
	public void updateFeedCountss(Integer feedApiId, Integer countUnreadItems, Integer countItems) {
		ContentValues values = new ContentValues();
		values.put(FeedTable.COLUMN_UNREAD_COUNT, countUnreadItems);
        values.put(FeedTable.COLUMN_ITEMS_COUNT, countItems);
		String where = FeedTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+feedApiId+""};
		database.update(FeedTable.TABLE_FEED, values, where, args);
	}
	
	public void unreadCountUpdate () {
		ArrayList<Feed> feeds = getUnreadCount();
		if (feeds.size() > 0) {
			for (Feed feed : feeds) {
                updateFeedCountss(feed.getApiId(), feed.getUnreadCount(), feed.getItemsCount());
	    	}
		}
	}
}
