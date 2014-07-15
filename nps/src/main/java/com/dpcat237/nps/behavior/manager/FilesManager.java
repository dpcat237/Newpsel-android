package com.dpcat237.nps.behavior.manager;

import android.content.Context;

import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Song;

import java.io.File;
import java.util.ArrayList;

public class FilesManager {
    private static final String TAG = "NPS:FilesManager";
    SongRepository songRepository;

    public void deletePlayedSongs(Context context) {
        if (!PreferencesHelper.isPlayerActive(context)) {
            songRepository = new SongRepository(context);
            songRepository.open();

            ArrayList<Song> songs = songRepository.getPlayedSongs();
            if (songs.size() > 0) {
                deleteSongs(context, songs);
            }
        }
    }

    private void deleteSongs(Context context, ArrayList<Song> songs) {
        for (Song song : songs) {
            File songFile = new File(FileHelper.getSongPath(context, song.getFilename()));
            if (songFile.exists()) {
                songFile.delete();
            }
            songRepository.deleteSong(song.getId());
        }
    }
}
