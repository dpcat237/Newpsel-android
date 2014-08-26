package com.dpcat237.nps.behavior.manager;

import android.content.Context;

import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Song;

import java.io.File;
import java.util.ArrayList;

public class FilesManager {
    private static final String TAG = "NPS:FilesManager";
    private Context mContext;
    private SongRepository songRepository;


    public void deletePlayedSongs(Context context) {
        if (!PreferencesHelper.isPlayerActive(context)) {
            mContext = context;
            songRepository = new SongRepository(mContext);
            songRepository.open();

            ArrayList<Song> songs = songRepository.getPlayedSongs();
            if (songs.size() > 0) {
                deleteSongs(songs);
            }

            songRepository.close();
        }
    }

    private void deleteSongs(ArrayList<Song> songs) {
        for (Song song : songs) {
            removeFile(song.getFilename());
            songRepository.deleteSong(song.getId());
        }
    }

    private void removeFile(String filePath) {
        File file = new File(FileHelper.getSongPath(mContext, filePath));
        if (file.exists()) {
            file.delete();
        }
    }
}
