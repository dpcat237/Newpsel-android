package com.dpcat237.nps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NPSDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "nps.db";
	private static final int DATABASE_VERSION = 1;
	
	public NPSDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		FeedTable.onCreate(db);
	}
	
	public void onDelete(SQLiteDatabase db) {
		FeedTable.onDelete(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		FeedTable.onUpgrade(db, oldVersion, newVersion);
	}

}
