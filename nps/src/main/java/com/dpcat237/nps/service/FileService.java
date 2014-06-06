package com.dpcat237.nps.service;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.dpcat237.nps.constant.FileConstants;

import java.io.File;

public class FileService {
    private String MAIN_FOLDER;
    private File appFolder;
    private File voicesFolder;
    private Boolean error = false;
    private static final String TAG = "NPS:FileService";
    private volatile static FileService uniqueInstance;

    private FileService() {
        MAIN_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()+ FileConstants.FOLDER_APP;
        setAppFolder();
    }

    public static FileService getInstance() {
        if (uniqueInstance == null) {
            synchronized (FileService.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new FileService();
                }
            }
        }

        return uniqueInstance;
    }

    public File getAppFolder() {
        return appFolder;
    }

    public Boolean getError() {
        return error;
    }

    public void setAppFolder() {
        appFolder = new File(MAIN_FOLDER);
        boolean success = true;
        if (!appFolder.exists()) {
            success = appFolder.mkdir();
        }

        if (!success) {
            error = true;
        }
    }

    public void testy() {
        Log.d(TAG, "tut: testy");
    }

    public File getVoicesFolder() {
        String folderLocation = MAIN_FOLDER+ FileConstants.FOLDER_VOICES;
        voicesFolder = new File(folderLocation);
        if (!voicesFolder.exists()) {
            voicesFolder.mkdir();
        }

        return voicesFolder;
    }

    public Uri getSongUri(String fileName) {
        if (voicesFolder == null) {
            getVoicesFolder();
        }
        String filePath = voicesFolder.getAbsolutePath()+"/"+fileName;

        return Uri.parse("file://" + filePath);
    }
}
