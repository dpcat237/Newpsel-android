package com.dpcat237.nps.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dpcat237.nps.behavior.service.SyncDictationItemsService;
import com.dpcat237.nps.behavior.service.SyncLaterService;
import com.dpcat237.nps.behavior.service.SyncNewsService;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.table.DictateItemTable;
import com.dpcat237.nps.database.table.FeedTable;
import com.dpcat237.nps.database.table.ItemTable;
import com.dpcat237.nps.database.table.LabelItemTable;
import com.dpcat237.nps.database.table.LabelTable;
import com.dpcat237.nps.database.table.LaterItemTable;
import com.dpcat237.nps.database.table.SharedTable;
import com.dpcat237.nps.database.table.SongTable;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.helper.ReceiverHelper;

public class NPSDatabase extends SQLiteOpenHelper {
    private static final String TAG = "NPS:NPSDatabase";
    private Context mContext;
	private static final String DATABASE_NAME = "nps.db";
	private static final int DATABASE_VERSION = 14;
	
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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DictateItemTable.onUpgrade(db, oldVersion, newVersion);
		FeedTable.onUpgrade(db, oldVersion, newVersion);
		ItemTable.onUpgrade(db, oldVersion, newVersion);
		LabelTable.onUpgrade(db, oldVersion, newVersion);
		LabelItemTable.onUpgrade(db, oldVersion, newVersion);
        LaterItemTable.onUpgrade(db, oldVersion, newVersion);
		SharedTable.onUpgrade(db, oldVersion, newVersion);
        SongTable.onUpgrade(db, oldVersion, newVersion);

        //delete folders with user files
        FileHelper.deleteFolders(FileHelper.getVoicesFolder(mContext));

        //launch sync services after upgrade
        if (LoginHelper.checkLogged(mContext)) {
            launchServices();
        }
	}

    private void launchServices() {
        //activate sync of feeds and labels
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.FEEDS_SYNC_REQUIRED, true);
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.LABELS_SYNC_REQUIRED, true);

        Intent syncNews = new Intent(mContext, SyncNewsService.class);
        mContext.startService(syncNews);

        Intent syncDictations = new Intent(mContext, SyncDictationItemsService.class);
        mContext.startService(syncDictations);

        Intent syncLater = new Intent(mContext, SyncLaterService.class);
        mContext.startService(syncLater);

        ReceiverHelper.enableBootReceiver(mContext);
    }
}
