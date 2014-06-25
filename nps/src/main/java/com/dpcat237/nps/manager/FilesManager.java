package com.dpcat237.nps.manager;

import android.content.Context;

import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.SongRepository;

import java.io.File;
import java.util.ArrayList;

public class FilesManager {
    private static final String TAG = "NPS:FilesManager";
    SongRepository songRepository;

    public void deletePlayedSongs(Context context) {
        if (!GenericHelper.isPlayerActive(context)) {
            songRepository = new SongRepository(context);
            songRepository.open();

            ArrayList<Song> songs = songRepository.getPlayedSongs();
            if (songs.size() > 0) {
                deleteSongs(songs);
            }
        }
    }

    private void deleteSongs(ArrayList<Song> songs) {
        for (Song song : songs) {
            File soundFile = new File(FileHelper.getSongPath(song.getFilename()));
            if (soundFile.exists()) {
                soundFile.delete();
            }
            songRepository.deleteSong(song.getId());
        }
    }
}
