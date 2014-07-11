package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.SharedTable;
import com.dpcat237.nps.model.Shared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedRepository extends BaseRepository {
	private String[] allColumns = {
				SharedTable.COLUMN_ID,
				SharedTable.COLUMN_TITLE,
				SharedTable.COLUMN_TEXT,
                SharedTable.COLUMN_LABEL_API_ID
			};


	public SharedRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}


	public Boolean addShared(Shared shared){
		if (!checkSharedExists(shared.getText())) {
			ContentValues values = new ContentValues();
			values.put(SharedTable.COLUMN_TITLE, shared.getTitle());
			values.put(SharedTable.COLUMN_TEXT, shared.getText());
            values.put(SharedTable.COLUMN_LABEL_API_ID, shared.getLabelApiId());
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
                object.put("label_api_id", cursor.getInt(3));
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