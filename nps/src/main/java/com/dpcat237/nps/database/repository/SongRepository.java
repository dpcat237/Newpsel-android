package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dpcat237.nps.database.table.NPSDatabase;
import com.dpcat237.nps.database.table.SongTable;
import com.dpcat237.nps.model.Song;

import java.util.ArrayList;

public class SongRepository {

	// Database fields
	private SQLiteDatabase database;
	private NPSDatabase dbHelper;
	private String[] allColumns = {
			SongTable.COLUMN_ID,
            SongTable.COLUMN_LIST_ID,
            SongTable.COLUMN_ITEM_ID,
            SongTable.COLUMN_LIST_TITLE,
            SongTable.COLUMN_TITLE,
            SongTable.COLUMN_FILE,
            SongTable.COLUMN_IS_GRABBED,
            SongTable.COLUMN_IS_PLAYED,
            SongTable.COLUMN_TYPE
			};
    private String[] filesColumns = {
            SongTable.COLUMN_ID,
            SongTable.COLUMN_FILE,
    };

    public SongRepository(Context context) {
        dbHelper = new NPSDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

	public void close() {
		dbHelper.close();
	}

	public void addSong(Song song) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_LIST_ID, song.getListId());
        values.put(SongTable.COLUMN_ITEM_ID, song.getItemId());
        values.put(SongTable.COLUMN_LIST_TITLE, song.getListTitle());
        values.put(SongTable.COLUMN_TITLE, song.getTitle());
        values.put(SongTable.COLUMN_FILE, song.getFilename());
        values.put(SongTable.COLUMN_IS_GRABBED, song.isGrabbed());
        values.put(SongTable.COLUMN_IS_PLAYED, song.isPlayed());
        values.put(SongTable.COLUMN_TYPE, song.getType());
        database.insert(SongTable.TABLE_SONG, null, values);
	}

	public ArrayList<Song> getSongs(String type, long listId) {
		ArrayList<Song> songs = new ArrayList<Song>();
        Cursor cursor = getSongsCursor(type, listId);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Song song = cursorToSong(cursor);
            songs.add(song);
			cursor.moveToNext();
		}
		cursor.close();

		return songs;
	}

    public Cursor getSongsCursor(String type, long listId) {
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=? AND "+SongTable.COLUMN_IS_PLAYED+"=?";
        String[] args = new String[] {""+listId+"", ""+type+"", ""+1+"", ""+0+""};
        String orderBy = SongTable.COLUMN_ID+" ASC";
        Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, orderBy);

        return cursor;
    }

	public Song cursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setId(cursor.getInt(0));
        song.setListId(cursor.getInt(1));
        song.setItemId(cursor.getInt(2));
        song.setListTitle(cursor.getString(3));
        song.setTitle(cursor.getString(4));
        song.setFilename(cursor.getString(5));
        song.setGrabbed(cursor.getInt(6)>0);
        song.setPlayed(cursor.getInt(7)>0);
        song.setType(cursor.getString(8));

		return song;
	}

    public Song cursorToFile(Cursor cursor) {
        Song song = new Song();
        song.setId(cursor.getInt(0));
        song.setFilename(cursor.getString(1));

        return song;
    }

	public Song getSong(Long songId) {
        Song song = null;
		String where = SongTable.COLUMN_ID+"=?";
		String[] args = new String[] {""+songId+""};
		Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
            song = cursorToSong(cursor);
			cursor.moveToNext();
		}

		cursor.close();
		return song;
	}

    public Song getListSong(Integer listId, Integer itemId, String type) {
        Song song = null;
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+listId+"", ""+itemId+"", ""+type+""};
        Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            song = cursorToSong(cursor);
            cursor.moveToNext();
        }
        cursor.close();

        return song;
    }

    public Cursor getCursorNotGrabbedSongs(String type) {
        String where = SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=?";
        String[] args = new String[] {""+type+"", ""+0+""};
        Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, null);

        return cursor;
    }

    public Boolean checkListSongExists(Integer listId, Integer itemId, String type){
        Boolean result = false;
        String[] columns = new String[] {SongTable.COLUMN_ID};
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+listId+"", ""+itemId+"", ""+type+""};

        Cursor cursor = database.query(SongTable.TABLE_SONG, columns, where, args, null, null, null);

        if (cursor.getCount() > 0) {
            result = true;
        }

        return result;
    }

    public Boolean checkListHasGrabbedSongs(Integer listId, String type) {
        Boolean result = false;
        String[] columns = new String[] {SongTable.COLUMN_ID};
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=? AND "+SongTable.COLUMN_IS_PLAYED+"=?";
        String[] args = new String[] {""+listId+"", ""+type+"", ""+1+"", ""+0+""};

        Cursor cursor = database.query(SongTable.TABLE_SONG, columns, where, args, null, null, null);

        if (cursor.getCount() > 0) {
            result = true;
        }

        return result;
    }

    public void setGrabbedSong(Integer songId) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_IS_GRABBED, true);
        String where = SongTable.COLUMN_ID+"=?";
        String[] args = new String[] {""+songId+""};
        database.update(SongTable.TABLE_SONG, values, where, args);
    }

    public void markAsPlayed(Integer songId) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_IS_PLAYED, true);
        String where = SongTable.COLUMN_ID+"=?";
        String[] args = new String[] {""+songId+""};
        database.update(SongTable.TABLE_SONG, values, where, args);
    }

    public void markAsPlayed(Integer listId, Integer itemId, String type) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_IS_PLAYED, true);
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+itemId+"", ""+listId+"", ""+type+""};
        database.update(SongTable.TABLE_SONG, values, where, args);
    }

    public void markAsPlayedSongs(Integer listId, String type) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_IS_PLAYED, true);
        String where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+listId+"", ""+type+""};
        database.update(SongTable.TABLE_SONG, values, where, args);
    }

    public ArrayList<Song> getPlayedSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();
        String where = SongTable.COLUMN_IS_PLAYED+"=?";
        String[] args = new String[] {""+1+""};
        Cursor cursor = database.query(SongTable.TABLE_SONG, filesColumns, where, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song song = cursorToFile(cursor);
            songs.add(song);
            cursor.moveToNext();
        }
        cursor.close();

        return songs;
    }

    public void deleteSong(Integer songId) {
        database.delete(SongTable.TABLE_SONG, SongTable.COLUMN_ID + " = " + songId, null);
    }
}
