package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.database.repository.SongPartRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.model.SongPart;

import java.util.ArrayList;

public class PlayerQueueManager {
    private static final String TAG = "NPS:PlayerQueueManager";
    private Context mContext;
    private Cursor cursor = null;
    private SongRepository songRepo;
    private SongPartRepository partRepo;
    private Integer currentPosition;
    private Integer lastPosition;
    private Boolean error = false;
    private ArrayList<SongPart> parts = null;
    private Integer partsCount;

    public PlayerQueueManager(Context context) {
        mContext = context;
        songRepo = new SongRepository(mContext);
        songRepo.open();
        partRepo = new SongPartRepository(mContext);
        partRepo.open();
    }

    public void finish() {
        songRepo.close();
        partRepo.close();
    }

    public Boolean areError() {
        return error;
    }

    public Song getCurrentSong() {
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);
        setSongPart(song);
        //Log.d(TAG, "tut: getCurrentSong part "+partsCount);

        return song;
    }

    private void setSongPart(Song song) {
        if (parts == null) {
            parts = partRepo.getSongParts(song.getId());
            partsCount = 0;
        }

        //Log.d(TAG, "tut: setSongPart parts "+parts.size());
        SongPart songPart = parts.get(partsCount);
        song.setFilename(songPart.getFilename());
        partsCount++;
    }

    private void setCurrentPosition(Integer position) {
        currentPosition = position;
        parts = null;
    }

    public Boolean areMoreParts() {
        return (partsCount < parts.size());
    }

    private void checkNewCursor() {
        error = (cursor.getCount() < 1);
    }

    public Boolean setCursorList(String playType, Integer listId) {
        cursor = songRepo.getSongsCursor(playType, listId);
        cursor.moveToNext();
        setCurrentPosition(cursor.getPosition());
        checkNewCursor();

        return true;
    }

    public Boolean setCursorSong(String playType, Integer itemApiId) {
        cursor = songRepo.getSongCursor(playType, itemApiId);
        cursor.moveToNext();
        setCurrentPosition(cursor.getPosition());
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
        setCurrentPosition(cursor.getPosition());

        return true;
    }

    public Boolean setPreviousSong()
    {
        if (cursor.isBeforeFirst()) {
            return false;
        }

        cursor.moveToPosition(currentPosition);
        cursor.moveToPrevious();
        setCurrentPosition(cursor.getPosition());

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
