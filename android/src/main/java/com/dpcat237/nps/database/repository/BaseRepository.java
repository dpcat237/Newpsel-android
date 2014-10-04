package com.dpcat237.nps.database.repository;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dpcat237.nps.database.NPSDatabase;

public abstract class BaseRepository {
	protected SQLiteDatabase database;
    protected NPSDatabase dbHelper;
    protected Boolean isOpen = false;


	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		dbHelper.onCreate(database);
        setOpen(true);
	}

	public void close() {
		dbHelper.close();
        setOpen(false);
	}
	
	public void create(){
		dbHelper.onCreate(database);
	}
	
	public void drop(){
		dbHelper.onDelete(database);
	}

    protected String columnsToString(String table, String[] columns) {
        String all = "";
        Integer total = columns.length;
        Integer count = 1;
        for (String column : columns) {
            if (count < total) {
                all+= " "+table+"."+column+",";
            } else {
                all+= " "+table+"."+column+" ";
            }
            count++;
        }

        return all;
    }

    public Boolean isOpen() {
        return isOpen;
    }

    private void setOpen(Boolean status) {
        isOpen = status;
    }
}