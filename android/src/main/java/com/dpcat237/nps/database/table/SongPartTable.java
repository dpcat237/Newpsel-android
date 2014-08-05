package com.dpcat237.nps.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SongPartTable {

	// Database table
	public static final String TABLE_NAME = "song_type";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SONG_ID = "song_id";
	public static final String COLUMN_FILE = "file";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_SONG_ID + " integer not null ,"
			+ COLUMN_FILE + " varchar(255) not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(SongPartTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
