package com.dpcat237.nps.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LabelItemTable {

	// Database table
	public static final String TABLE_LABEL_ITEM = "label_item";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_API_ID = "api_id";
	public static final String COLUMN_LABEL_API_ID = "label_api_id";
	public static final String COLUMN_ITEM_API_ID = "item_api_id";
	public static final String COLUMN_IS_UNREAD = "is_unread";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_LABEL_ITEM
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_API_ID + " integer default 0,"
			+ COLUMN_LABEL_API_ID + " integer not null ,"
			+ COLUMN_ITEM_API_ID + " integer not null ,"
			+ COLUMN_IS_UNREAD + " boolean not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL_ITEM);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(LabelItemTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL_ITEM);
		onCreate(database);
	}
}
