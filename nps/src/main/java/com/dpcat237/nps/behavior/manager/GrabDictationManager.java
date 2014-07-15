package com.dpcat237.nps.behavior.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.SongsFactory;
import com.dpcat237.nps.behavior.factory.songManager.SongsManager;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.model.Song;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class GrabDictationManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "NPS:GrabDictationManager";
    private TextToSpeech mTts;
    private volatile static GrabDictationManager uniqueInstance;
    protected Context mContext;
    private Song currentSong;
    private String songsType = "";
    private SongsManager songGrabManager;
    private File voicesFolder;
    private String songFilename;
    private Boolean running = false;
    private Boolean grabbedSongs = false;
    private String dictationTypes[] = new String[] {SongConstants.GRABBER_TYPE_TITLE, SongConstants.GRABBER_TYPE_DICTATE_ITEM};
    private Integer dictationTypesCount;
    private File soundFile;

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
        dictationTypesCount = 0;
        mTts = new TextToSpeech(mContext, this);
        voicesFolder = FileHelper.getVoicesFolder(mContext);
        Log.d(TAG, "tut: startProcess ");
    }

    private void process() {
        Log.d(TAG, "tut: process ");
        if (!voicesFolder.exists()) {
            finishProcess();
            Log.d(TAG, "tut: process finishProcess ");
            return;
        }
        setSpeechSpeed();
        setSongsCursor();
        startGrabDictationTypeSongs();
    }

    private void setSongsType() {
        songsType = dictationTypes[dictationTypesCount];
    }

    private void setSongsCursor() {
        setSongsType();
        songGrabManager = SongsFactory.createManager(songsType);
        songGrabManager.setup(mContext);
        songGrabManager.setCursorNotGrabbedSongs();
        Log.d(TAG, "tut: setSongsCursor ");
    }

    private void startGrabDictationTypeSongs()
    {
        if (songGrabManager.areError()) {
            nextDictationType();
        } else {
            grabbedSongs = false;
            currentSong = songGrabManager.getCurrentSong();
            setDictationLanguage();
            createSongFile();
            grabSong();
        }
    }

    private void nextDictationType() {
        dictationTypesCount++;
        if (areMoreTypes()) {
            songGrabManager.finish();
            setSongsCursor();
            startGrabDictationTypeSongs();
        } else {
            songGrabManager.finish();
            finishProcess();
        }
    }

    private void setDictationLanguage() {
        Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS(currentSong.getLanguage(), mTts);
        if (localeTTs != null && mTts.isLanguageAvailable(localeTTs) == TextToSpeech.LANG_AVAILABLE) {
            mTts.setLanguage(localeTTs);
        }
    }

    @Override
    public void onInit(int status) {
        setTtsListener();
        process();
    }

    @SuppressLint("NewApi")
    private void setTtsListener() {
        final GrabDictationManager callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult =
                    mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) { callWithResult.onDone(); }

                        @Override
                        public void onError(String utteranceId) { callWithResult.onError(); }

                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG, "tut: onStart "+utteranceId);
                        }
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
        if (grabbedSongs) {
            NotificationHelper.showSimpleNotification(mContext, getNotificationMessage());
        }
        nextDictationType();
    }

   private Boolean areMoreTypes() {
       return (dictationTypesCount <= (dictationTypes.length-1));
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

    public void onDone() {
        grabbedSongs = true;
        Log.d(TAG, "tut: onDone "+currentSong.getId()+" file: "+soundFile.length());
        songGrabManager.setAsGrabbedSong(currentSong.getId());
        grabNextSong();
    }

    public void onError() {
        Log.d(TAG, "tut: onError "+currentSong.getId()+" file: "+soundFile.length());
        if (soundFile.length() < 1) {
            songGrabManager.markTtsError(currentSong);
            deleteSongFile();
        }

        grabNextSong();
    }

    private void deleteSongFile() {
        if (soundFile != null && soundFile.exists()) {
            soundFile.delete();
        }
    }

    private void createSongFile() {
        songFilename = voicesFolder.getAbsolutePath()+"/"+currentSong.getFilename();
        soundFile = new File(songFilename);
        deleteSongFile();
    }

    private void grabSong() {
        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTts.synthesizeToFile(currentSong.getContent(), myHashRender, songFilename);

        Log.d(TAG, "tut: grabSong "+currentSong.getId()+" title: "+currentSong.getTitle());
        //Log.d(TAG, "tut: grabSong getContent:  "+currentSong.getContent());
    }

    public Boolean isRunning() {
        return running;
    }

    private String getNotificationMessage() {
        String message = "";
        if (songsType.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            message = mContext.getString(R.string.nt_download_items_title_finished);
        }
        if (songsType.equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            message = mContext.getString(R.string.nt_download_dictation_items_finished);
        }

        return message;
    }

    private void finishProcess() {
        Log.d(TAG, "tut: finishProcess");
        running = false;
        dictationTypesCount=0;
    }

    private void setSpeechSpeed() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Float speechRate = Float.parseFloat(pref.getString("pref_dictation_speed", "1.0f"));
        mTts.setSpeechRate(speechRate);
    }
}
