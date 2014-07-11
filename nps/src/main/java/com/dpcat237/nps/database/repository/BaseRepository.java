package com.dpcat237.nps.database.repository;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dpcat237.nps.database.NPSDatabase;

public abstract class BaseRepository {
	protected SQLiteDatabase database;
    protected NPSDatabase dbHelper;


	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		dbHelper.onCreate(database);
	}

	public void close() {
		dbHelper.close();
	}
	
	public void create(){
		dbHelper.onCreate(database);
	}
	
	public void drop(){
		dbHelper.onDelete(database);
	}
}