package com.dpcat237.nps.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.SharedTable;
import com.dpcat237.nps.model.Shared;

public class SharedRepository {

	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
				SharedTable.COLUMN_ID,
				SharedTable.COLUMN_TITLE,
				SharedTable.COLUMN_TEXT
			};

	public SharedRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

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
	
	public Boolean addShared(Shared shared){
		if (!checkSharedExists(shared.getText())) {
			ContentValues values = new ContentValues();
			values.put(SharedTable.COLUMN_TITLE, shared.getTitle());
			values.put(SharedTable.COLUMN_TEXT, shared.getText());
			database.insert(SharedTable.TABLE_NAME, null, values);
			
			return true;
		}
		
		return false;
	}
	
	public Boolean checkSharedExists(String text){
		Boolean result = false;
		String[] columns = new String[] {SharedTable.COLUMN_ID};
		String where = SharedTable.COLUMN_TEXT+"=?";
		String[] args = new String[] {""+text+""};
		
		Cursor cursor = database.query(SharedTable.TABLE_NAME, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}
	
	public JSONArray getSharedToSync() {
		JSONArray collectionJson = new JSONArray();

		Cursor cursor = database.query(SharedTable.TABLE_NAME, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject object = new JSONObject();
				object.put("id", cursor.getInt(0));
				object.put("title", cursor.getString(1));
				object.put("text", cursor.getString(2));
				collectionJson.put(object);
			} catch (JSONException e) {
				Log.e("SharedRepository - getSharedToSync","Error", e);
			}
			cursor.moveToNext();
		}

		cursor.close();
		return collectionJson;
	}
	
	public void removeSharedItems() {
		String where = SharedTable.COLUMN_ID+"!=?";
		String[] args = new String[] {""+0+""};
		database.delete(SharedTable.TABLE_NAME, where, args);
	}
}