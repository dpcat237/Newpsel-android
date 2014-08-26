package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.database.Cursor;

import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.common.model.Song;

public class PlayerQueueManager {
    private static final String TAG = "NPS:PlayerQueueManager";
    private Cursor cursor = null;
    private SongRepository songRepo;
    private Integer currentPosition;
    private Integer lastPosition;
    private Boolean error = false;

    public PlayerQueueManager(Context context) {
        songRepo = new SongRepository(context);
        songRepo.open();
    }

    public void finish() {
        songRepo.close();
    }

    public Boolean areError() {
        return error;
    }

    public Song getCurrentSong() {
        cursor.moveToPosition(currentPosition);

        return songRepo.cursorToSong(cursor);
    }

    private void checkNewCursor() {
        error = (cursor.getCount() < 1);
    }

    public Boolean setCursorList(String playType, Integer listId) {
        cursor = songRepo.getSongsCursor(playType, listId);
        cursor.moveToNext();
        currentPosition = cursor.getPosition();
        checkNewCursor();

        return true;
    }

    public Boolean setCursorSong(String playType, Integer itemApiId) {
        cursor = songRepo.getSongCursor(playType, itemApiId);
        cursor.moveToNext();
        currentPosition = cursor.getPosition();
        checkNewCursor();

        return true;
    }

    public void setLastPosition(Integer position) {
        this.lastPosition = position;
    }

    public Boolean setNextSong()
    {
        if (cursor.isAfterLast()) {
            return false;
        }

        cursor.moveToPosition(currentPosition);
        cursor.moveToNext();
        currentPosition = cursor.getPosition();

        return true;
    }

    public Boolean setPreviousSong()
    {
        if (cursor.isBeforeFirst()) {
            return false;
        }

        cursor.moveToPosition(currentPosition);
        cursor.moveToPrevious();
        currentPosition = cursor.getPosition();

        return true;
    }

    public Boolean isFirst()
    {
        return cursor.isFirst();
    }

    public Boolean isLast()
    {
        return cursor.isLast();
    }

    public Integer getItemApiId() {
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);

        return song.getItemApiId();
    }
}
