package com.dpcat237.nps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dpcat237.nps.database.table.DictateItemTable;
import com.dpcat237.nps.database.table.FeedTable;
import com.dpcat237.nps.database.table.ItemTable;
import com.dpcat237.nps.database.table.LabelItemTable;
import com.dpcat237.nps.database.table.LabelTable;
import com.dpcat237.nps.database.table.LaterItemTable;
import com.dpcat237.nps.database.table.SharedTable;
import com.dpcat237.nps.database.table.SongPartTable;
import com.dpcat237.nps.database.table.SongTable;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.PreferencesHelper;

public class NPSDatabase extends SQLiteOpenHelper {

    private Context mContext;
	private static final String DATABASE_NAME = "nps.db";
	private static final int DATABASE_VERSION = 9;
	
	public NPSDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        DictateItemTable.onCreate(db);
        FeedTable.onCreate(db);
		ItemTable.onCreate(db);
		LabelTable.onCreate(db);
		LabelItemTable.onCreate(db);
        LaterItemTable.onCreate(db);
		SharedTable.onCreate(db);
        SongTable.onCreate(db);
        SongPartTable.onCreate(db);
	}
	
	public void onDelete(SQLiteDatabase db) {
        DictateItemTable.onDelete(db);
        FeedTable.onDelete(db);
		ItemTable.onDelete(db);
		LabelTable.onDelete(db);
		LabelItemTable.onDelete(db);
        LaterItemTable.onDelete(db);
		SharedTable.onDelete(db);
        SongTable.onDelete(db);
        SongPartTable.onDelete(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Integer lastUpdate = 0;
        DictateItemTable.onUpgrade(db, oldVersion, newVersion);
		FeedTable.onUpgrade(db, oldVersion, newVersion);
		ItemTable.onUpgrade(db, oldVersion, newVersion);
		LabelTable.onUpgrade(db, oldVersion, newVersion);
		LabelItemTable.onUpgrade(db, oldVersion, newVersion);
        LaterItemTable.onUpgrade(db, oldVersion, newVersion);
		SharedTable.onUpgrade(db, oldVersion, newVersion);
        SongTable.onUpgrade(db, oldVersion, newVersion);
        SongPartTable.onUpgrade(db, oldVersion, newVersion);

        //delete folders with user files
        FileHelper.deleteFolders(FileHelper.getVoicesFolder(mContext));

        //TODO: remove when improve sync
        PreferencesHelper.setLastFeedsUpdate(mContext, lastUpdate);
        PreferencesHelper.setLastLabelsUpdate(mContext, lastUpdate);
	}
}
