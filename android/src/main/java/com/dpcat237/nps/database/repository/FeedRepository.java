package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.FeedTable;
import com.dpcat237.nps.database.table.ItemTable;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedRepository extends BaseRepository {
    private static final String TAG = "NPS:FeedRepository";
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
    private String[] syncColumns = {
            FeedTable.COLUMN_ID,
            FeedTable.COLUMN_API_ID,
            FeedTable.COLUMN_TITLE,
            FeedTable.COLUMN_DATE_UP,
    };


	public FeedRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void addFeed(Feed feed){
		if (!checkFeedExists(feed.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(FeedTable.COLUMN_API_ID, feed.getApiId());
			values.put(FeedTable.COLUMN_TITLE, feed.getTitle());
			values.put(FeedTable.COLUMN_WEBSITE, feed.getWebsite());
            values.put(FeedTable.COLUMN_UNREAD_COUNT, feed.getUnreadCount());
			values.put(FeedTable.COLUMN_FAVICON, feed.getFavicon());
            values.put(FeedTable.COLUMN_DATE_UP, feed.getDateUpdated());
			database.insert(FeedTable.TABLE_NAME, null, values);
		}
	}
	
	public void updateFeed(Feed feed) {
		ContentValues values = new ContentValues();
        values.put(FeedTable.COLUMN_API_ID, feed.getApiId());
		values.put(FeedTable.COLUMN_TITLE, feed.getTitle());
		values.put(FeedTable.COLUMN_WEBSITE, feed.getWebsite());
		values.put(FeedTable.COLUMN_FAVICON, feed.getFavicon());
        values.put(FeedTable.COLUMN_DATE_UP, feed.getDateUpdated());
		String where = FeedTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+feed.getId()+""};
		database.update(FeedTable.TABLE_NAME, values, where, args);
	}
	
	public Boolean checkFeedExists(int feedApiId){
		String[] columns = new String[] {FeedTable.COLUMN_API_ID};
		String where = FeedTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+feedApiId+""};
		Cursor cursor = database.query(FeedTable.TABLE_NAME, columns, where, args, null, null, null);

		return (cursor.getCount() > 0);
	}

	public ArrayList<Feed> getAllFeedsUnread() {
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		String where = FeedTable.COLUMN_UNREAD_COUNT+">?";
		String[] args = new String[] {""+0+""};
		String orderBy = FeedTable.COLUMN_TITLE+" ASC";
		Cursor cursor = database.query(FeedTable.TABLE_NAME, allColumns, where, args, null, null, orderBy);

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
        Cursor cursor = database.query(FeedTable.TABLE_NAME, allColumns, where, args, null, null, orderBy);

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
        String orderBy = FeedTable.COLUMN_TITLE+" ASC";
        Cursor cursor = database.query(FeedTable.TABLE_NAME, listColumns, null, null, null, null, orderBy);

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
		
		Cursor cursor = database.query(FeedTable.TABLE_NAME, allColumns, where, args, null, null, null);
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

    public Integer getFeedUnreadCount(Integer feedApiId) {
        String sql = "SELECT (SELECT COUNT(tb2."+ItemTable.COLUMN_ID+") AS total FROM "+ItemTable.TABLE_ITEM+" AS tb2 " +
                    "WHERE tb2."+ItemTable.COLUMN_FEED_ID+"=tb1."+ItemTable.COLUMN_FEED_ID+" AND tb2."+ItemTable.COLUMN_IS_UNREAD+"=1) AS countUnread " +
                " FROM "+ItemTable.TABLE_ITEM+" AS tb1 WHERE tb1."+ItemTable.COLUMN_FEED_ID+"="+feedApiId+";";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        return cursor.getInt(0);
    }
	
	private ArrayList<Feed> getUnreadCount() {
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
	
	public void updateFeedCounts(Integer feedApiId, Integer countUnreadItems, Integer countItems) {
		ContentValues values = new ContentValues();
		values.put(FeedTable.COLUMN_UNREAD_COUNT, countUnreadItems);
        values.put(FeedTable.COLUMN_ITEMS_COUNT, countItems);
		String where = FeedTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+feedApiId+""};
		database.update(FeedTable.TABLE_NAME, values, where, args);
	}
	
	public void unreadCountUpdate () {
		ArrayList<Feed> feeds = getUnreadCount();
		if (feeds.size() < 1) {
			return;
		}

        for (Feed feed : feeds) {
            updateFeedCounts(feed.getApiId(), feed.getUnreadCount(), feed.getItemsCount());
        }
	}

    public JSONArray getFeedsToSync() {
        JSONArray feeds = new JSONArray();
        Cursor cursor = database.query(FeedTable.TABLE_NAME, syncColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            feeds.put(cursorToFeedSync(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return feeds ;
    }

    private JSONObject cursorToFeedSync(Cursor cursor) {
        JSONObject feed = new JSONObject();
        try {
            feed.put("id", cursor.getInt(0));
            feed.put("api_id", cursor.getInt(1));
            feed.put("title", cursor.getString(2));
            feed.put("date_up", cursor.getInt(3));
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return feed;
    }

    public void deleteFeed(Integer feedApiId) {
        String where = FeedTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+feedApiId+""};
        database.delete(FeedTable.TABLE_NAME, where, args);
    }
}
