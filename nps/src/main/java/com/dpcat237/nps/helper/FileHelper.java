package com.dpcat237.nps.helper;

import android.os.Environment;

import com.dpcat237.nps.constant.FileConstants;

public class FileHelper {
    public static String getSongPath(String songName)
    {
        String mainFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+ FileConstants.FOLDER_APP;
        String songPath = mainFolder+ FileConstants.FOLDER_VOICES+"/"+songName;

        return songPath;
    }
}
