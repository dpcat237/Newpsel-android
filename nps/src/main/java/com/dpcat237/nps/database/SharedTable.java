package com.dpcat237.nps.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SharedTable {

	// Database table
	public static final String TABLE_NAME = "shared";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LABEL_API_ID = "label_api_id";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TITLE + " text not null,"
			+ COLUMN_TEXT + " text not null,"
            + COLUMN_LABEL_API_ID + " integer not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(SharedTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
