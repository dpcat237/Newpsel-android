package com.dpcat237.nps.behavior.factory.songManager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.model.List;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.SongRepository;

import java.util.ArrayList;

public abstract class SongsManager {
    private static final String TAG = "NPS:SongsManager";
    protected Context mContext;
    protected FeedRepository feedRepo;
    protected SongRepository songRepo;
    protected ArrayList<List> lists;
    protected ArrayList<ListItem> listItems;
    protected String grabberType;
    private Cursor songsCursor;
    protected ListItem songListItem;
    protected Boolean error = false;


    protected void createListSongs() {
        if (listItems.size() < 1) {
            return;
        }

        for (ListItem listItem : listItems) {
            if (!isSongExists(listItem.getListApiId(), listItem.getItemApiId())) {
                createListSong(listItem);
            }
        }
    }

    protected Song createSong(List list, ListItem listItem) {
        Song song = new Song();
        song.setListId(list.getApiId());
        song.setItemApiId(listItem.getItemApiId());
        song.setListTitle(list.getTitle());
        song.setTitle(listItem.getTitle());
        song.setType(grabberType);
        String filename = grabberType+"_"+list.getApiId()+"_"+listItem.getId();
        song.setFilename(filename + ".wav");

        return song;
    }

    public void finish() {
        feedRepo.close();
        songRepo.close();
    }

    public void createSongs() {
        createSongsProcess();
    }

    protected Boolean isSongExists(Integer listApiId, Integer itemApiId) {
        return songRepo.checkListSongExists(listApiId, itemApiId, grabberType);
    }

    protected void openDB() {
        feedRepo = new FeedRepository(mContext);
        songRepo = new SongRepository(mContext);

        feedRepo.open();
        songRepo.open();
    }

    public void setup(Context context) {
        this.mContext = context;
        setCreatorType();
        openDB();
    }

    public void setCursorNotGrabbedSongs() {
        songsCursor = songRepo.getCursorNotGrabbedSongs(grabberType);
        Log.d(TAG, "tut: setCursorNotGrabbedSongs  "+songsCursor.getCount());
        if (songsCursor.getCount() > 0) {
            songsCursor.moveToFirst();
        } else {
            error = true;
        }
    }

    public Song getCurrentSong() {
        Song song = songRepo.cursorToSong(songsCursor);
        song = setSongExtraData(song);

        return song;
    }

    private Song setSongExtraData(Song song) {
        getListItem(song.getItemApiId());
        if (error) {
            return null;
        }
        setSongContent(song, songListItem);
        song.setLanguage(songListItem.getLanguage());

        return song;
    }

    public Song getNextSong() {
        Song song;
        if (songsCursor.isLast()) {
            error = true;

            return null;
        }

        try {
            songsCursor.moveToNext();
            song = songRepo.cursorToSong(songsCursor);
            song = setSongExtraData(song);
        } catch (Exception e) {
            error = true;

            return null;
        }

        return song;
    }

    public Boolean areError() {
        return error;
    }

    public void setAsGrabbedSong(Integer songId) {
        songRepo.setGrabbedSong(songId);
    }

    public void markAsPlayed(Song song) {
        songRepo.markAsPlayed(song.getId());
        markAsDictated(song.getItemApiId());
    }

    public void markTtsError(Song song) {
        songRepo.deleteSong(song.getId());
        markTtsError(song.getItemApiId());
    }

    abstract protected void setCreatorType();
    abstract protected void setSongContent(Song song, ListItem listItem);
    abstract protected void getListItem(Integer itemId);
    abstract protected void markAsDictated(Integer itemApiId);
    abstract protected void markTtsError(Integer itemApiId);
    abstract protected void createSongsProcess();
    abstract protected void createListSong(ListItem listItem);
}