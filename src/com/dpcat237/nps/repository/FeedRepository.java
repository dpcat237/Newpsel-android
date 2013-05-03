package com.dpcat237.nps.repository;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dpcat237.nps.database.FeedTable;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.model.Feed;

public class FeedRepository {

	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
				FeedTable.COLUMN_ID,
				FeedTable.COLUMN_API_ID,
				FeedTable.COLUMN_TITLE,
				FeedTable.COLUMN_WEBSITE,
				FeedTable.COLUMN_FAVICON
			};

	public FeedRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void cr(){
		dbHelper.onCreate(database);
	}
	
	public void addFeed(Feed feed){
		if (!checkFeedExists(feed.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(FeedTable.COLUMN_API_ID, feed.getApiId());
			values.put(FeedTable.COLUMN_TITLE, feed.getTitle());
			values.put(FeedTable.COLUMN_WEBSITE, feed.getWebsite());
			values.put(FeedTable.COLUMN_FAVICON, feed.getFavicon());
			database.insert(FeedTable.TABLE_FEED, null, values);
		}
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

	public List<Feed> getAllFeeds() {
		List<Feed> feeds = new ArrayList<Feed>();

		Cursor cursor = database.query(FeedTable.TABLE_FEED, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Feed feed = cursorToFeed(cursor);
			feeds.add(feed);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return feeds;
	}

	private Feed cursorToFeed(Cursor cursor) {
		Feed feed = new Feed();
		feed.setId(cursor.getLong(0));
		feed.setApiId(cursor.getLong(1));
		feed.setTitle(cursor.getString(2));
		feed.setWebsite(cursor.getString(3));
		feed.setFavicon(cursor.getString(4));
		return feed;
	}
}
