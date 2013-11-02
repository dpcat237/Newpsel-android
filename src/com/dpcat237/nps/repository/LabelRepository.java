package com.dpcat237.nps.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dpcat237.nps.database.LabelItemTable;
import com.dpcat237.nps.database.LabelTable;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.model.LabelItem;

public class LabelRepository {

	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
				LabelTable.COLUMN_ID,
				LabelTable.COLUMN_API_ID,
				LabelTable.COLUMN_NAME,
				LabelTable.COLUMN_UNREAD_COUNT
			};
	
	private String[] basicColumns = {
			LabelTable.COLUMN_ID,
			LabelTable.COLUMN_API_ID,
			LabelTable.COLUMN_NAME,
		};
	
	private String[] selectedItemsColumns = {
			LabelItemTable.COLUMN_ID,
			LabelItemTable.COLUMN_API_ID,
			LabelItemTable.COLUMN_LABEL_ID,
			LabelItemTable.COLUMN_LABEL_API_ID,
			LabelItemTable.COLUMN_ITEM_API_ID,
		};

	public LabelRepository(Context context) {
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
	
	public void addLabel(Label label, Boolean defaultChanged){
		if (!checkLabelExists(label.getApiId())) {
			ContentValues values = new ContentValues();
			values.put(LabelTable.COLUMN_API_ID, label.getApiId());
			values.put(LabelTable.COLUMN_NAME, label.getName());
			values.put(LabelTable.COLUMN_UNREAD_COUNT, 0);
			values.put(LabelTable.COLUMN_IS_CHANGED, true);
			database.insert(LabelTable.TABLE_LABEL, null, values);
		} else {
			updateLabelName(label.getApiId(), label.getName(), defaultChanged);
		}
	}
	
	public Boolean checkLabelExists(long labelId){
		Boolean result = false;
		String[] columns = new String[] {"api_id"};
		String where = "api_id=?";
		String[] args = new String[] {""+labelId+""};
		
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}
	
	public Boolean checkLabelSet(long labelId, long itemApiId){
		Boolean result = false;
		String[] columns = new String[] {"id"};
		String where = LabelItemTable.COLUMN_LABEL_ID+"=? AND "+LabelItemTable.COLUMN_ITEM_API_ID+"=?";
		String[] args = new String[] {""+labelId+"", ""+itemApiId+""};
		
		Cursor cursor = database.query(LabelItemTable.TABLE_LABEL_ITEM, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}
	
	public Label createLabel(String label) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_NAME, label);
		values.put(LabelTable.COLUMN_IS_CHANGED, true);
		long insertId = database.insert(LabelTable.TABLE_LABEL, null,
				values);
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, basicColumns, LabelTable.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		Label newLabel = cursorToBasicLabel(cursor);
		cursor.close();
		return newLabel;
	}

	public ArrayList<Label> getAllLabels() {
		ArrayList<Label> labels = new ArrayList<Label>();
		String orderBy = LabelTable.COLUMN_NAME+" ASC";
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, basicColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Label label = cursorToBasicLabel(cursor);
			labels.add(label);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return labels;
	}
	
	//TODO: review!
	public ArrayList<Label> getAllLabelsUnread() {
		ArrayList<Label> labels = new ArrayList<Label>();
		String where = LabelTable.COLUMN_UNREAD_COUNT+">?";
		String[] args = new String[] {""+0+""};
		String orderBy = LabelTable.COLUMN_NAME+" ASC";
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, allColumns, where, args, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Label label = cursorToLabel(cursor);
			labels.add(label);
			cursor.moveToNext();
		}

		cursor.close();
		return labels;
	}
	
	public Label getLabel(Integer labelApiId) {
		Label label = null;
		String where = LabelTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+labelApiId+""};
		
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			label = cursorToLabel(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return label;
	}

	private Label cursorToBasicLabel(Cursor cursor) {
		Label label = new Label();
		label.setId(cursor.getInt(0));
		label.setApiId(cursor.getInt(1));
		label.setName(cursor.getString(2));
		return label;
	}
	
	private Label cursorToLabel(Cursor cursor) {
		Label label = new Label();
		label.setId(cursor.getInt(0));
		label.setApiId(cursor.getInt(1));
		label.setName(cursor.getString(2));
		label.setUnreadCount(cursor.getInt(3));
		return label;
	}
	
	private ArrayList<Label> getUnreadCount () {
		ArrayList<Label> labels = new ArrayList<Label>();
		String sql = "SELECT tb1."+LabelItemTable.COLUMN_LABEL_ID + ", " +
				"(SELECT COUNT(tb2."+LabelItemTable.COLUMN_ID+") AS total FROM "+LabelItemTable.TABLE_LABEL_ITEM+" AS tb2 " +
						"WHERE tb2."+LabelItemTable.COLUMN_LABEL_ID+"=tb1."+LabelItemTable.COLUMN_LABEL_ID+" AND tb2."+LabelItemTable.COLUMN_IS_UNREAD+"=1) AS count " +
				" FROM "+LabelItemTable.TABLE_LABEL_ITEM+" AS tb1 GROUP BY tb1."+LabelItemTable.COLUMN_LABEL_ID;
		Cursor cursor = database.rawQuery(sql, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Label label = new Label();
			label.setApiId(cursor.getInt(0));
			label.setUnreadCount(cursor.getInt(1));
			labels.add(label);
			cursor.moveToNext();
		}
		
		return labels;
	}
	
	public void updateLabelUnreads(Long labelApiId, Long count) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_UNREAD_COUNT, count);
		String where = LabelTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+labelApiId+""};
		database.update(LabelTable.TABLE_LABEL, values, where, args);
	}
	
	public void unreadCountUpdate () {
		ArrayList<Label> labels = getUnreadCount();
		if (labels.size() > 0) {
			for (Label label : labels) {
				updateLabelUnreads(label.getApiId(), label.getUnreadCount());
	    	}
		}
	}
	
	public void setLabel(LabelItem labelItem){
		ContentValues values = new ContentValues();
		values.put(LabelItemTable.COLUMN_LABEL_ID, labelItem.getLabelId());
		values.put(LabelItemTable.COLUMN_LABEL_API_ID, labelItem.getLabelApiId());
		values.put(LabelItemTable.COLUMN_ITEM_API_ID, labelItem.getItemApiId());
		values.put(LabelItemTable.COLUMN_IS_UNREAD, labelItem.isUnread());
		database.insert(LabelItemTable.TABLE_LABEL_ITEM, null, values);
	}
	
	public ArrayList<Label> getChangedLabels() {
		ArrayList<Label> labels = new ArrayList<Label>();
		String where = LabelTable.COLUMN_IS_CHANGED+"=?";
		String[] args = new String[] {""+1+""};
		Cursor cursor = database.query(LabelTable.TABLE_LABEL, allColumns, where, args, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Label label = cursorToLabel(cursor);
			labels.add(label);
			cursor.moveToNext();
		}

		cursor.close();
		return labels;
	}
	
	public Map<String, Object> getLabelsToSync() {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONArray labelsJson = new JSONArray();
		ArrayList<Label> labelsArray = new ArrayList<Label>();
		String where = LabelTable.COLUMN_IS_CHANGED+"=?";
		String[] args = new String[] {""+1+""};

		Cursor cursor = database.query(LabelTable.TABLE_LABEL, basicColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject label = new JSONObject();
				label.put("api_id", cursor.getInt(0));
				label.put("id", cursor.getInt(1));
				label.put("name", cursor.getString(2));
				labelsJson.put(label);
				
				Label labelArray = cursorToBasicLabel(cursor);
				labelsArray.add(labelArray);
			} catch (JSONException e) {
				Log.e("LabelRepository - getLabelsToSync","Error", e);
			}
			cursor.moveToNext();
		}
		cursor.close();
		result.put("labelsJson", labelsJson);
		result.put("labelsArray", labelsArray);
		
		return result;
	}
	
	public JSONArray getSelectedItemsToSync() {
		JSONArray items = new JSONArray();

		Cursor cursor = database.query(LabelItemTable.TABLE_LABEL_ITEM, selectedItemsColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject item = new JSONObject();
				item.put("label_id", cursor.getInt(3));
				item.put("item_id", cursor.getInt(4));
				items.put(item);
			} catch (JSONException e) {
				Log.e("LabelRepository -  getLabelsToSync","Error", e);
			}
			cursor.moveToNext();
		}

		cursor.close();
		return items;
	}
	
	public void setApiId(Long labelId, Long labelApiId) {
		setFeedApiId(labelId, labelApiId);
		setItemsFeedApiId(labelId, labelApiId);
	}
	
	public void setFeedApiId(Long labelId, Long labelApiId) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_API_ID, labelApiId);
		values.put(LabelTable.COLUMN_IS_CHANGED, false);
		String where = LabelTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+labelId+""};
		database.update(LabelTable.TABLE_LABEL, values, where, args);
	}
	
	public void setItemsFeedApiId(Long labelId, Long labelApiId) {
		ContentValues values = new ContentValues();
		values.put(LabelItemTable.COLUMN_LABEL_API_ID, labelApiId);
		String where = LabelItemTable.COLUMN_LABEL_ID+"=?";
		String[] args = new String[] {""+labelId+""};
		database.update(LabelItemTable.TABLE_LABEL_ITEM, values, where, args);
	}
	
	public void updateLabelName(Long labelApiId, String name, Boolean defaultChanged) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_NAME, name);
		values.put(LabelTable.COLUMN_IS_CHANGED, defaultChanged);
		String where = LabelTable.COLUMN_API_ID+"=?";
		String[] args = new String[] {""+labelApiId+""};
		database.update(LabelTable.TABLE_LABEL, values, where, args);
	}
	
	public void setChanged(Long labelId, Boolean changed) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_IS_CHANGED, changed);
		String where = LabelTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+labelId+""};
		database.update(LabelTable.TABLE_LABEL, values, where, args);
	}

	public void removeLaterItem(long labelId, long itemApiId)
	{
		String where = LabelItemTable.COLUMN_LABEL_ID+"=? AND "+LabelItemTable.COLUMN_ITEM_API_ID+"=?";
		String[] args = new String[] {""+labelId+"", ""+itemApiId+""};
		database.delete(LabelItemTable.TABLE_LABEL_ITEM, where, args);
	}
	
	public void removeLaterItems() {
		String where = LabelItemTable.COLUMN_ID+"!=?";
		String[] args = new String[] {""+0+""};
		database.delete(LabelItemTable.TABLE_LABEL_ITEM, where, args);
	}
}