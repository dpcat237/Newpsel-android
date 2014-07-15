package com.dpcat237.nps.helper;

import android.content.Context;

import com.dpcat237.nps.constant.FileConstants;

import java.io.File;

public class FileHelper {
    //MAIN_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()+ FileConstants.FOLDER_APP;

    public static String getSongPath(Context context, String songName) {
        File voicesFolder = getVoicesFolder(context);

        return voicesFolder.getAbsolutePath()+"/"+songName;
    }

    public static File getVoicesFolder(Context context) {
        return context.getExternalFilesDir(FileConstants.FOLDER_VOICES);
    }

    public static void deleteFolders(File voiceFolder) {
        //delete voices folder
        File[] songs = voiceFolder.listFiles();
        deleteFiles(songs);
        if (voiceFolder.exists()) {
            voiceFolder.delete();
        }
    }

    private static void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
