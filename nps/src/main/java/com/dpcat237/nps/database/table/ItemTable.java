package com.dpcat237.nps.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ItemTable {

	// Database table
	public static final String TABLE_ITEM = "item";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_API_ID = "api_id";
    public static final String COLUMN_UI_ID = "ui_id";
	public static final String COLUMN_FEED_ID = "feed_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_IS_STARED = "is_stared";
	public static final String COLUMN_IS_UNREAD = "is_unread";
	public static final String COLUMN_DATE_ADD = "date_add";
    public static final String COLUMN_LANGUAGE = "language";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_ITEM
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_API_ID + " integer not null,"
            + COLUMN_UI_ID + " integer not null,"
			+ COLUMN_FEED_ID + " integer not null,"
			+ COLUMN_TITLE + " text not null,"
			+ COLUMN_LINK + " text not null,"
			+ COLUMN_CONTENT + " text not null,"
			+ COLUMN_IS_STARED + " boolean not null,"
			+ COLUMN_IS_UNREAD + " boolean not null,"
			+ COLUMN_DATE_ADD + " integer not null,"
            + COLUMN_LANGUAGE + " character(2)"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ItemTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
		onCreate(database);
	}
}
