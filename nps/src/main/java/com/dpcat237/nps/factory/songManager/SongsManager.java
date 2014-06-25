package com.dpcat237.nps.factory.songManager;

import android.content.Context;
import android.database.Cursor;

import com.dpcat237.nps.model.List;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.SongRepository;

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

    protected void createListSongs(List list) {
        for (ListItem listItem : listItems) {
            if (!isSongExists(list.getApiId(), listItem.getId())) {
                Song song = createSong(list, listItem);
                songRepo.addSong(song);
            }
        }
    }

    protected Song  createSong(List list, ListItem listItem) {
        Song song = new Song();
        song.setListId(list.getApiId());
        song.setItemId(listItem.getId());
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
        getLists();

        for (List list : lists) {
            getListItems(list.getApiId());
            createListSongs(list);
        }
    }

    protected Boolean isSongExists(Integer listId, Integer itemId) {
        return songRepo.checkListSongExists(listId, itemId, grabberType);
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
        getListItem(song.getItemId());
        setSongContent(song, songListItem);
        song.setLanguage(songListItem.getLanguage());

        return song;
    }

    public Song getNextSong() {
        Song song = null;
        if (songsCursor.isLast()) {
            error = true;

            return song;
        }

        songsCursor.moveToNext();
        song = songRepo.cursorToSong(songsCursor);
        song = setSongExtraData(song);

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
        markAsDictated(song.getItemId());
    }

    abstract void getLists();
    abstract void getListItems(Integer listId);
    abstract void setCreatorType();
    abstract void setSongContent(Song song, ListItem listItem);
    abstract void getListItem(Integer itemId);
    abstract void markAsDictated(Integer itemId);
}