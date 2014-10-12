package com.dpcat237.nps.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.database.NPSDatabase;
import com.dpcat237.nps.database.table.SongTable;

import java.util.ArrayList;

public class SongRepository extends  BaseRepository {
    private static final String TAG = "NPS:SongRepository";
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

	public void addSong(Song song) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_LIST_ID, song.getListId());
        values.put(SongTable.COLUMN_ITEM_ID, song.getItemApiId());
        values.put(SongTable.COLUMN_LIST_TITLE, song.getListTitle());
        values.put(SongTable.COLUMN_TITLE, song.getTitle());
        values.put(SongTable.COLUMN_FILE, song.getFilename());
        values.put(SongTable.COLUMN_IS_GRABBED, song.isGrabbed());
        values.put(SongTable.COLUMN_IS_PLAYED, song.isPlayed());
        values.put(SongTable.COLUMN_TYPE, song.getType());
        values.put(SongTable.COLUMN_DATE_ADD, song.getDateAdd());
        database.insert(SongTable.TABLE_SONG, null, values);
	}

    public Cursor getSongsCursor(String type, Integer listId) {
        Log.d(TAG, "tut: "+type+" - "+listId);
        String where = "";
        String[] args = null;
        if (listId == 0) {
            where = SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=? AND "+SongTable.COLUMN_IS_PLAYED+"=?";
            args = new String[] {""+type+"", ""+1+"", ""+0+""};
        } else {
            where = SongTable.COLUMN_LIST_ID+"=? AND "+SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=? AND "+SongTable.COLUMN_IS_PLAYED+"=?";
            args = new String[] {""+listId+"", ""+type+"", ""+1+"", ""+0+""};
        }
        String orderBy = SongTable.COLUMN_DATE_ADD+" DESC";

        return database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, orderBy);
    }

	public Song cursorToSong(Cursor cursor) {
        Song song = new Song();
        song.setId(cursor.getInt(0));
        song.setListId(cursor.getInt(1));
        song.setItemApiId(cursor.getInt(2));
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

    public Cursor getSongCursor(String type, Integer itemApiId) {
        String where = SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_ITEM_ID+"=?";
        String[] args = new String[] {""+type+"", ""+itemApiId+""};
        Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, null);

        return cursor;
    }

    public Cursor getCursorNotGrabbedSongs(String type) {
        String where = SongTable.COLUMN_TYPE+"=? AND "+SongTable.COLUMN_IS_GRABBED+"=?";
        String[] args = new String[] {""+type+"", ""+0+""};
        Cursor cursor = database.query(SongTable.TABLE_SONG, allColumns, where, args, null, null, null);

        return cursor;
    }

    public Boolean checkListSongExists(Integer itemApiId, String type){
        Boolean result = false;
        String[] columns = new String[] {SongTable.COLUMN_ID};
        String where = SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+itemApiId+"", ""+type+""};

        Cursor cursor = database.query(SongTable.TABLE_SONG, columns, where, args, null, null, null);
        if (cursor.getCount() > 0) {
            result = true;
        }

        return result;
    }

    public Boolean checkSongGrabbed(Integer itemApiId, String type){
        Boolean result = false;
        String[] columns = new String[] {SongTable.COLUMN_ID};
        String where = SongTable.COLUMN_IS_GRABBED+"=? AND "+SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+1+"", ""+itemApiId+"", ""+type+""};

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

    public void markAsPlayed(Integer itemApiId, String type, Boolean isPlayed) {
        ContentValues values = new ContentValues();
        values.put(SongTable.COLUMN_IS_PLAYED, isPlayed);
        String where = SongTable.COLUMN_ITEM_ID+"=? AND "+SongTable.COLUMN_TYPE+"=?";
        String[] args = new String[] {""+itemApiId+"", ""+type+""};
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
