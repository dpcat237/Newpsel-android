package com.dpcat237.nps.behavior.factory.songManager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dpcat237.nps.constant.FileConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.common.model.List;
import com.dpcat237.nps.common.model.ListItem;
import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.common.model.SongPart;

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
    private Integer ttsStringLimit = 3900;
    private String filenamePrefix;
    private ArrayList<SongPart> parts = null;
    private Integer partsCount;


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
        Log.d(TAG, "tut: setCursorNotGrabbedSongs "+songsCursor.getCount()+" type: "+grabberType);
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

        if (parts == null) {
            setFilenamePrefix(song.getItemApiId(), song.getListId());
            createSongParts(song, song.getContent());
        }
        setSongPart(song);

        return song;
    }

    private void setFilenamePrefix(Integer itemApiId, Integer listApiId) {
        filenamePrefix = FileConstants.FILE_TEMP_SONG_PART+"_"+itemApiId+"_"+listApiId;

    }

    public Song getNextSong(Boolean previousError) {
        Song song;
        if (songsCursor.isLast()) {
            error = true;

            return null;
        }

        try {
            if (previousError || partsCount >= parts.size()) {
                songsCursor.moveToNext();
                parts = null;
            }
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

    public void setAsGrabbedSong(Integer songId, String songFileName, String voicesFolderPath) {
        Boolean merged = FileHelper.mergeSongParts(mContext, parts, voicesFolderPath+songFileName);
        //Log.d(TAG, "tut: setAsGrabbedSong a");
        if (merged) {
            //Log.d(TAG, "tut: setAsGrabbedSong b");
            songRepo.setGrabbedSong(songId);
        }

        FileHelper.removeSongParts(parts, voicesFolderPath);
    }

    public void markAsPlayed(Song song) {
        songRepo.markAsPlayed(song.getId());
        markAsDictated(song.getItemApiId());
    }

    public void markTtsError(Song song) {
        songRepo.deleteSong(song.getId());
        markTtsError(song.getItemApiId());
    }

    private void createSongParts(Song song, String completeText) {
        parts = null;
        partsCount = 0;

        Integer textLength = completeText.length();
        if (textLength <= ttsStringLimit) {
            createSongPart(song, completeText, 1);

            return;
        }

        cutDictation(song, completeText, textLength, 0, 0);
    }

    private void cutDictation(Song song, String completeText, Integer textLength, Integer subInt, Integer count) {
        count++;
        Integer subEnd = subInt + ttsStringLimit;
        if (subEnd >= textLength) {
            subEnd = textLength;
        }
        String subText = completeText.substring(subInt, subEnd);

        if (subEnd.equals(textLength)) {
            createSongPart(song, subText, count);

            return;
        }
        subText = subText.replaceAll(" [^ ]+$", "");
        createSongPart(song, subText, count);

        String subSearch = subText.substring((subText.length()-50), subText.length());
        Integer nextSubInt = completeText.lastIndexOf(subSearch) + 50;

        cutDictation(song, completeText, textLength, nextSubInt, count);
    }

    private void createSongPart(Song song, String content, Integer count) {
        SongPart songPart = new SongPart();
        songPart.setSongId(song.getId());
        songPart.setContent(content);
        songPart.setFilename(filenamePrefix + "_" + count + ".wav");
        song.addPart(songPart);
    }

    private void setSongPart(Song song) {
        if (parts == null) {
            parts = song.getParts();
        }

        SongPart songPart = parts.get(partsCount);
        partsCount++;

        song.setContent(songPart.getContent());
        song.setPartFilename(songPart.getFilename());
    }

    public Boolean isLastSongPart() {
        return (partsCount >= parts.size());
    }

    abstract protected void setCreatorType();
    abstract protected void setSongContent(Song song, ListItem listItem);
    abstract protected void getListItem(Integer itemId);
    abstract protected void markAsDictated(Integer itemApiId);
    abstract protected void markTtsError(Integer itemApiId);
    abstract protected void createSongsProcess();
    abstract protected void createListSong(ListItem listItem);
}