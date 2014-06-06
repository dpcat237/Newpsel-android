package com.dpcat237.nps.database;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class SongTable {

	// Database table
	public static final String TABLE_SONG = "song";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_LIST_ID = "list_id";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_LIST_TITLE = "list_title";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_FILE = "file";
    public static final String COLUMN_IS_GRABBED = "is_grabbed";
	public static final String COLUMN_TYPE = "type";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_SONG
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_LIST_ID + " integer not null ,"
            + COLUMN_ITEM_ID + " integer not null ,"
            + COLUMN_LIST_TITLE + " varchar(255) not null ,"
			+ COLUMN_TITLE + " varchar(255) not null ,"
			+ COLUMN_FILE + " varchar(255) not null ,"
            + COLUMN_IS_GRABBED + " boolean not null,"
			+ COLUMN_TYPE + " varchar(50) not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onDelete(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(SongTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
		onCreate(database);
	}
}
