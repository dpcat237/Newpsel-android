package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.SongPartTable;
import com.dpcat237.nps.model.SongPart;

import java.util.ArrayList;

public class SongPartRepository extends  BaseRepository {
	private String[] columns = {
            SongPartTable.COLUMN_ID,
            SongPartTable.COLUMN_SONG_ID,
            SongPartTable.COLUMN_FILE
			};

    public SongPartRepository(Context context) {
        dbHelper = new NPSDatabase(context);
    }

	public void addSongPart(SongPart part) {
        ContentValues values = new ContentValues();
        values.put(SongPartTable.COLUMN_SONG_ID, part.getSongId());
        values.put(SongPartTable.COLUMN_FILE, part.getFilename());
        database.insert(SongPartTable.TABLE_NAME, null, values);
	}

    public ArrayList<SongPart> getSongParts(Integer songId) {
        ArrayList<SongPart> parts = new ArrayList<SongPart>();
        String where = SongPartTable.COLUMN_SONG_ID+"=?";
        String[] args = new String[] {""+songId+""};
        String orderBy = SongPartTable.COLUMN_ID+" ASC";

        Cursor cursor = database.query(SongPartTable.TABLE_NAME, columns, where, args, null, null, orderBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            parts.add(cursorToSongPart(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return parts;
    }

    private SongPart cursorToSongPart(Cursor cursor) {
        SongPart part = new SongPart();
        part.setId(cursor.getInt(0));
        part.setSongId(cursor.getInt(1));
        part.setFilename(cursor.getString(2));

        return part;
    }

    public void deleteSongPart(Integer partId) {
        database.delete(SongPartTable.TABLE_NAME, SongPartTable.COLUMN_ID + " = " + partId, null);
    }
}
