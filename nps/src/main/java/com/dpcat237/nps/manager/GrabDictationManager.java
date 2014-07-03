package com.dpcat237.nps.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.factory.SongsFactory;
import com.dpcat237.nps.factory.songManager.SongsManager;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.service.FileService;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class GrabDictationManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "NPS:GrabDictationService";
    private TextToSpeech mTts;
    private FileService fileService;
    private volatile static GrabDictationManager uniqueInstance;
    protected Context mContext;
    private Song currentSong;
    private String songsType = "";
    private SongsManager songGrabManager;
    private File voicesFolder;
    private String songFilename;
    private Boolean running = false;
    private Boolean grabbedSongs = false;

    private GrabDictationManager(Context context) {
        mContext = context;
    }

    public static GrabDictationManager getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (GrabDictationManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new GrabDictationManager(context);
                }
            }
        }

        return uniqueInstance;
    }

    public void startProcess() {
        running = true;
        mTts = new TextToSpeech(mContext, this);
        fileService = FileService.getInstance();
        setSongsType();
    }

    private void process() {
        songGrabManager = SongsFactory.createManager(songsType);
        songGrabManager.setup(mContext);
        songGrabManager.setCursorNotGrabbedSongs();
        if (songGrabManager.areError() || fileService.getError()) {
            endListProcess();
            return;
        }

        grabbedSongs = false;
        voicesFolder = fileService.getVoicesFolder();
        currentSong = songGrabManager.getCurrentSong();
        setDictationLanguage();
        createSongFile();
        grabSong();
    }

    private void setDictationLanguage() {
        Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS(currentSong.getLanguage(), mTts);
        if (localeTTs != null && mTts.isLanguageAvailable(localeTTs) == TextToSpeech.LANG_AVAILABLE) {
            mTts.setLanguage(localeTTs);
        }
    }

    private void setSongsType() {
        if (songsType.equals("")) {
            songsType = SongConstants.GRABBER_TYPE_TITLE;
        }
    }

    @Override
    public void onInit(int status) {
        setTtsListener();
        process();
    }

    @SuppressLint("NewApi")
    private void setTtsListener()
    {
        final GrabDictationManager callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult =
                    mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) { callWithResult.onDone(); }

                        @Override
                        public void onError(String utteranceId) { }

                        @Override
                        public void onStart(String utteranceId) { }
                    });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        } else {
            int listenerResult =
                    mTts.setOnUtteranceCompletedListener(
                            new TextToSpeech.OnUtteranceCompletedListener() {
                                @Override
                                public void onUtteranceCompleted(String utteranceId) {
                                    callWithResult.onDone();
                                }
                            });
            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }

    private void endListProcess() {
        songGrabManager.finish();
        running = false;

        if (grabbedSongs) {
            NotificationHelper.showSimpleNotification(mContext, getNotificationMessage());
        }
    }

    private void grabNextSong() {
        currentSong = songGrabManager.getNextSong();
        if (!songGrabManager.areError()) {
            setDictationLanguage();
            createSongFile();
            grabSong();
        } else {
            endListProcess();
        }
    }

    public void onDone()
    {
        grabbedSongs = true;
        songGrabManager.setAsGrabbedSong(currentSong.getId());
        grabNextSong();
    }

    private void createSongFile() {
        songFilename = voicesFolder.getAbsolutePath()+"/"+currentSong.getFilename();
        File soundFile = new File(songFilename);
        if (soundFile.exists()) {
            soundFile.delete();
        }
    }

    private void grabSong() {
        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTts.synthesizeToFile(currentSong.getContent(), myHashRender, songFilename);
    }

    public Boolean isRunning() {
        return running;
    }

    private String getNotificationMessage() {
        String message = "";
        if (songsType.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            message = mContext.getString(R.string.nt_download_items_title_finished);
        }

        return message;
    }
}
