package com.dpcat237.nps.manager;

import android.content.Context;
import android.database.Cursor;

import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.ItemRepository;
import com.dpcat237.nps.repository.SongRepository;

public class PlayerQueueManager {
    private Context mContext;
    private Cursor cursor = null;
    private SongRepository songRepo;
    private Integer currentPosition;
    private Integer currentStatus;
    private Integer lastPosition;
    private static final String TAG = "NPS:PlayerQueueManager";


    public PlayerQueueManager(Context context) {
        mContext = context;
        currentStatus = PlayerConstants.PLAYER_STATUS_QUEUEEMPTY;
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }

    public Song getCurrentSong()
    {
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);

        return song;
    }

    public Boolean setCurrentList(String playType, long listId)
    {
        if (cursor == null) {
            cursor = songRepo.getSongsCursor(playType, listId);
            cursor.moveToNext();
            currentPosition = cursor.getPosition();

            return true;
        }

        return false;
    }

    public void setCurrentStatus(Integer status) {
        this.currentStatus = status;
    }

    public Boolean isPaused()
    {
        if (currentStatus == PlayerConstants.PLAYER_STATUS_PAUSED) {
            return true;
        }

        return false;
    }

    public int getLastPosition() {
        return this.lastPosition;
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

    public Item getItem() {
        ItemRepository itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);
        //song.getItemId()
        Item item = itemRepo.getItem(new Long(10));
        itemRepo.close();

        return item;
    }

    public Long getItemId() {
        ItemRepository itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        cursor.moveToPosition(currentPosition);
        Song song = songRepo.cursorToSong(cursor);
        //song.getItemId()

        return new Long(10);
    }
}
