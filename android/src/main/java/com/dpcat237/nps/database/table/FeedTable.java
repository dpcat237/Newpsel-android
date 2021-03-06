package com.dpcat237.nps.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FeedTable {
	// Database table
	public static final String TABLE_NAME = "feed";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_API_ID = "api_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_WEBSITE = "website";
	public static final String COLUMN_FAVICON = "favicon";
    public static final String COLUMN_ITEMS_COUNT = "items_count";
	public static final String COLUMN_UNREAD_COUNT = "unread_items_count";
    public static final String COLUMN_DATE_UP = "date_up";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_API_ID + " integer not null ,"
			+ COLUMN_TITLE + " varchar(255) not null ,"
			+ COLUMN_WEBSITE + " text not null,"
			+ COLUMN_FAVICON + " text,"
            + COLUMN_ITEMS_COUNT + " integer,"
			+ COLUMN_UNREAD_COUNT + " integer,"
            + COLUMN_DATE_UP + " integer not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(FeedTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
