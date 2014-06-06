package com.dpcat237.nps.factory.grabber;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.dpcat237.nps.model.List;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.SongRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Grabber implements GrabberInterface {
    private static final String TAG = "NPS:Grabber";
    protected Context mContext;
    protected FeedRepository feedRepo;
    protected SongRepository songRepo;
    protected ArrayList<List> lists;
    protected ArrayList<ListItem> listItems;
    protected ArrayList<Song> songs;
    protected String grabberType;


    protected void createListSongs(List list) {
        songs = null;
        songs = new ArrayList<Song>();
        for (ListItem listItem : listItems) {
            Song song = null;
            if (isSongExists(list.getApiId(), listItem.getId())) {
                song = getSong(list.getApiId(), listItem.getId());
            } else {
                song = createSong(list, listItem);
            }

            if (!song.isGrabbed()) {
                songs.add(song);
            }
        }
    }

    protected Song  createSong(List list, ListItem listItem) {
        Log.d(TAG, "tut: createSong");
        Song song = new Song();
        song.setListId(list.getApiId());
        song.setItemId(listItem.getId());
        song.setListTitle(list.getTitle());
        song.setTitle(listItem.getTitle());
        setSongContent(song, listItem);
        song.setType(grabberType);
        String filename = grabberType+"_"+list.getApiId()+"_"+listItem.getId();
        song.setFilename(filename + ".wav");

        return song;
    }

    protected void downloadSong(Song song) {
        //TODO: pass to external service
        /*if (fileService.getError()) {
            return;
        }
        File voicesFolder = fileService.getVoicesFolder();
        String fileName = voicesFolder.getAbsolutePath()+"/"+song.getFilename();
        File soundFile = new File(fileName);
        if (soundFile.exists()) {
            soundFile.delete();
        }

        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTTS.synthesizeToFile(song.getContent(), myHashRender, fileName);*/
    }

    protected void downloadSongs() {
        for (Song song : songs) {
            downloadSong(song);

            Log.d(TAG, "tut: song grab "+song.getFilename()); return;
        }
    }

    public void finish() {
        feedRepo.close();
        songRepo.close();
    }

    protected Song getSong(Integer listId, Integer itemId) {
        Log.d(TAG, "tut: getSong");
        return songRepo.getListSong(listId, itemId, grabberType);
    }

    public void grabSongs() {
        getLists();

        for (List list : lists) {
            getListItems(list.getApiId());
            createListSongs(list);
            downloadSongs();

            Log.d(TAG, "tut: songsss "+songs.size()+" - "+list.getTitle()); return;
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
        setGrabberType();
        openDB();
    }


}