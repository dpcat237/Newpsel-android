package com.dpcat237.nps.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LabelTable {

	// Database table
	public static final String TABLE_LABEL = "label";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_API_ID = "api_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UNREAD_COUNT = "unread_count";
	public static final String COLUMN_IS_CHANGED = "is_changed";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_LABEL
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_API_ID + " integer,"
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_UNREAD_COUNT + " integer, "
			+ COLUMN_IS_CHANGED + " boolean not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(LabelTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL);
		onCreate(database);
	}
}
