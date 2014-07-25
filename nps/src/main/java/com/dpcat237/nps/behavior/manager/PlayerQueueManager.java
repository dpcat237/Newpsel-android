package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.database.Cursor;

import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.model.Song;

public class PlayerQueueManager {
    private Context mContext;
    private Cursor cursor = null;
    private SongRepository songRepo;
    private Integer currentPosition;
    private Integer lastPosition;
    private static final String TAG = "NPS:PlayerQueueManager";
    private Boolean error = false;


    public PlayerQueueManager(Context context) {
        mContext = context;
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }

    public Boolean areError() {
        return error;
    }

    public Song getCurrentSong() {
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);

        return song;
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

    public void setLastPosition(Integer postition) {
        this.lastPosition = postition;
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
