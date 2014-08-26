package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.LabelItemTable;
import com.dpcat237.nps.database.table.LabelTable;
import com.dpcat237.nps.database.table.LaterItemTable;
import com.dpcat237.nps.helper.NumbersHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.model.LabelItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LabelRepository extends BaseRepository {
    private static final String TAG = "NPS:LabelRepository";
	private String[] basicColumns = {
			LabelTable.COLUMN_ID,
			LabelTable.COLUMN_API_ID,
			LabelTable.COLUMN_NAME,
		};
	private String[] itemsSyncColumns = {
			LabelItemTable.COLUMN_LABEL_API_ID,
			LabelItemTable.COLUMN_ITEM_API_ID,
		};
    private String[] unreadListColumns = {
            LabelTable.COLUMN_ID,
            LabelTable.COLUMN_API_ID,
            LabelTable.COLUMN_NAME,
            LabelTable.COLUMN_UNREAD_COUNT
    };
    private String[] syncColumns = {
            LabelTable.COLUMN_ID,
            LabelTable.COLUMN_API_ID,
            LabelTable.COLUMN_NAME,
            LabelTable.COLUMN_DATE_UP,
    };


	public LabelRepository(Context context) {
		dbHelper = new NPSDatabase(context);
	}

	public void addLabel(Label label){
			ContentValues values = new ContentValues();
			values.put(LabelTable.COLUMN_API_ID, label.getApiId());
			values.put(LabelTable.COLUMN_NAME, label.getName());
			values.put(LabelTable.COLUMN_UNREAD_COUNT, label.getUnreadCount());
			values.put(LabelTable.COLUMN_DATE_UP, label.getDateUpdated());
			database.insert(LabelTable.TABLE_NAME, null, values);
	}

	public Boolean checkLabelSet(long labelApiId, long itemApiId){
		Boolean result = false;
		String[] columns = new String[] {"id"};
		String where = LabelItemTable.COLUMN_LABEL_API_ID+"=? AND "+LabelItemTable.COLUMN_ITEM_API_ID+"=?";
		String[] args = new String[] {""+labelApiId+"", ""+itemApiId+""};
		
		Cursor cursor = database.query(LabelItemTable.TABLE_LABEL_ITEM, columns, where, args, null, null, null);
		
		if (cursor.getCount() > 0) {
			result = true;
		}
		
		return result;
	}
	
	public Label createLabel(String label) {
		ContentValues values = new ContentValues();
		values.put(LabelTable.COLUMN_NAME, label);
		values.put(LabelTable.COLUMN_DATE_UP, NumbersHelper.getCurrentTimestamp());
		long insertId = database.insert(LabelTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(LabelTable.TABLE_NAME, basicColumns, LabelTable.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		Label newLabel = cursorToBasicLabel(cursor);
		cursor.close();

		return newLabel;
	}

	public ArrayList<Label> getAllLabels() {
		ArrayList<Label> labels = new ArrayList<Label>();
		String orderBy = LabelTable.COLUMN_NAME+" ASC";
		Cursor cursor = database.query(LabelTable.TABLE_NAME, basicColumns, null, null, null, null, orderBy);

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

	private Label cursorToBasicLabel(Cursor cursor) {
		Label label = new Label();
		label.setId(cursor.getInt(0));
		label.setApiId(cursor.getInt(1));
		label.setName(cursor.getString(2));
		return label;
	}

	public void setLabel(LabelItem labelItem){
		ContentValues values = new ContentValues();
		values.put(LabelItemTable.COLUMN_LABEL_API_ID, labelItem.getLabelApiId());
		values.put(LabelItemTable.COLUMN_ITEM_API_ID, labelItem.getItemApiId());
		values.put(LabelItemTable.COLUMN_IS_UNREAD, labelItem.isUnread());
		database.insert(LabelItemTable.TABLE_LABEL_ITEM, null, values);
	}

	public JSONArray getLabelsToSync() {
		JSONArray labels = new JSONArray();
        String orderBy = LabelTable.COLUMN_NAME+" ASC";
        Cursor cursor = database.query(LabelTable.TABLE_NAME, syncColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            labels.put(cursorToLabelSync(cursor));
            cursor.moveToNext();
        }
        cursor.close();

		return labels ;
	}

    private JSONObject cursorToLabelSync(Cursor cursor) {
        JSONObject item = new JSONObject();
        try {
            item.put("id", cursor.getInt(0));
            item.put("api_id", cursor.getInt(1));
            item.put("name", cursor.getString(2));
            item.put("date_up", cursor.getInt(3));
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return item;
    }
	
	public JSONArray getSelectedItemsToSync() {
		JSONArray items = new JSONArray();

		Cursor cursor = database.query(LabelItemTable.TABLE_LABEL_ITEM, itemsSyncColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				JSONObject item = new JSONObject();
				item.put("label_id", cursor.getInt(0));
				item.put("item_id", cursor.getInt(1));
				items.put(item);
			} catch (JSONException e) {
				Log.e("LabelRepository - getLabelsToSync","Error", e);
			}
			cursor.moveToNext();
		}

		cursor.close();
		return items;
	}

    public void updateLabel(Label label) {
        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_API_ID, label.getApiId());
        values.put(LabelTable.COLUMN_NAME, label.getName());
        values.put(LabelTable.COLUMN_DATE_UP, label.getDateUpdated());

        String where = LabelTable.COLUMN_ID+"=?";
        String[] args = new String[] {""+label.getId()+""};
        database.update(LabelTable.TABLE_NAME, values, where, args);
    }

    public void deleteLabel(Integer labelApiId) {
        String where = LabelTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+labelApiId+""};
        database.delete(LabelTable.TABLE_NAME, where, args);
    }

	public void removeLabelItem(long labelApiId, long itemApiId)
	{
		String where = LabelItemTable.COLUMN_LABEL_API_ID+"=? AND "+LabelItemTable.COLUMN_ITEM_API_ID+"=?";
		String[] args = new String[] {""+labelApiId+"", ""+itemApiId+""};
		database.delete(LabelItemTable.TABLE_LABEL_ITEM, where, args);
	}
	
	public void removeLabelItems() {
		String where = LabelItemTable.COLUMN_ID+"!=?";
		String[] args = new String[] {""+0+""};
		database.delete(LabelItemTable.TABLE_LABEL_ITEM, where, args);
	}

    private ArrayList<Label> getUnreadCount() {
        ArrayList<Label> labels = new ArrayList<Label>();
        String sql = "SELECT tb1."+LaterItemTable.COLUMN_LATER_ID+", " +
                "(SELECT COUNT(tb2."+LaterItemTable.COLUMN_ID+") AS total FROM "+LaterItemTable.TABLE_NAME+" AS tb2 " +
                "WHERE tb2."+LaterItemTable.COLUMN_LATER_ID+"=tb1."+LaterItemTable.COLUMN_LATER_ID+" AND tb2."+LaterItemTable.COLUMN_IS_UNREAD+"=1) AS countUnread " +
                " FROM "+LaterItemTable.TABLE_NAME+" AS tb1 GROUP BY tb1."+LaterItemTable.COLUMN_LATER_ID;
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Label label = new Label();
            label.setApiId(cursor.getInt(0));
            label.setUnreadCount(cursor.getInt(1));
            labels.add(label);
            cursor.moveToNext();
        }
        cursor.close();

        return labels;
    }

    private void updateLabelCounts(Integer feedApiId, Integer countUnreadItems) {
        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_UNREAD_COUNT, countUnreadItems);
        String where = LabelTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+feedApiId+""};
        database.update(LabelTable.TABLE_NAME, values, where, args);
    }

    public void unreadCountUpdate () {
        ArrayList<Label> labels = getUnreadCount();
        if (labels.size() < 1) {
            return;
        }

        for (Label label : labels) {
            updateLabelCounts(label.getApiId(), label.getUnreadCount());
        }
    }

    public ArrayList<Label> getForListUnread() {
        ArrayList<Label> labels = new ArrayList<Label>();
        String where = LabelTable.COLUMN_UNREAD_COUNT+">?";
        String[] args = new String[] {""+0+""};
        String orderBy = LabelTable.COLUMN_NAME+" ASC";
        Cursor cursor = database.query(LabelTable.TABLE_NAME, unreadListColumns, where, args, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Label label = cursorToListLabel(cursor);
            labels.add(label);
            cursor.moveToNext();
        }
        cursor.close();

        return labels;
    }

    private Label cursorToListLabel(Cursor cursor) {
        Label label = new Label();
        label.setId(cursor.getInt(0));
        label.setApiId(cursor.getInt(1));
        label.setName(cursor.getString(2));
        label.setUnreadCount(cursor.getInt(3));
        return label;
    }

    public Label getLabel(Integer labelApiId) {
        String where = LabelTable.COLUMN_API_ID+"=?";
        String[] args = new String[] {""+labelApiId+""};

        Cursor cursor = database.query(LabelTable.TABLE_NAME, basicColumns, where, args, null, null, null);
        cursor.moveToFirst();
        Label label = cursorToBasicLabel(cursor);
        cursor.close();

        return label;
    }
}