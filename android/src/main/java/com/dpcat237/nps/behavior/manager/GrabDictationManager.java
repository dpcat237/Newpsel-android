package com.dpcat237.nps.behavior.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.SongsFactory;
import com.dpcat237.nps.behavior.factory.songManager.SongsManager;
import com.dpcat237.nps.constant.NotificationConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.helper.FileHelper;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.WidgetHelper;
import com.dpcat237.nps.common.model.Song;

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
    private String voicesFolderPath = "";
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
        File voicesFolder = FileHelper.getVoicesFolder(mContext);
        if (voicesFolder.exists()) {
            voicesFolderPath = voicesFolder.getAbsolutePath()+"/";
        }

        Log.d(TAG, "tut: startProcess ");
    }

    private void process() {
        //Log.d(TAG, "tut: process ");
        if (voicesFolderPath.length() < 1) {
            finishProcess();
            //Log.d(TAG, "tut: process finishProcess ");
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
        //Log.d(TAG, "tut: setSongsCursor ");
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
        if (localeTTs != null && LanguageHelper.isLanguageAvailable(mContext, mTts, localeTTs)) {
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
        int listenerResult =
                mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onDone(String utteranceId) { callWithResult.onDone(); }

                    @Override
                    public void onError(String utteranceId) { callWithResult.onError(); }

                    @Override
                    public void onStart(String utteranceId) {
                        //Log.d(TAG, "tut: onStart "+utteranceId);
                    }
                });
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e(TAG, "failed to add utterance progress listener");
        }
    }

    private void endListProcess() {
        if (grabbedSongs) {
            NotificationHelper.showSimpleNotification(mContext, NotificationConstants.ID_DICTATIONS_DOWNLOAD, getNotificationMessage());
        }
        nextDictationType();
    }

    private Boolean areMoreTypes() {
       return (dictationTypesCount <= (dictationTypes.length-1));
   }

    private void grabNext() {
        if (songGrabManager.isLastSongPart()) {
            //Log.d(TAG, "tut: setAsGrabbedSong a");
            songGrabManager.setAsGrabbedSong(currentSong.getId(), currentSong.getFilename(), voicesFolderPath);
            grabbedSongs = true;
            WidgetHelper.updateWidgets(mContext);
            grabNextSong(false);

            return;
        }

        currentSong = songGrabManager.getNextSong(false);
        if (!songGrabManager.areError()) {
            createSongFile();
            grabSong();
        } else {
            grabNextSong(true);
        }
    }

    private void grabNextSong(Boolean error) {
        currentSong = songGrabManager.getNextSong(error);
        if (!songGrabManager.areError()) {
            setDictationLanguage();
            createSongFile();
            grabSong();
        } else {
            endListProcess();
        }
    }

    public void onDone() {
        //Log.d(TAG, "tut: onDone "+currentSong.getId()+" file length: "+soundFile.length()+" file: "+soundFile.getName());
        grabNext();
    }

    public void onError() {
        //Log.d(TAG, "tut: onError "+currentSong.getId()+" file: "+soundFile.length());
        if (soundFile.length() < 1) {
            songGrabManager.markTtsError(currentSong);
            deleteSongFile();
        }

        grabNextSong(true);
    }

    private void deleteSongFile() {
        if (soundFile != null && soundFile.exists()) {
            soundFile.delete();
        }
    }

    private void createSongFile() {
        songFilename = voicesFolderPath+currentSong.getPartFilename();
        soundFile = new File(songFilename);
        deleteSongFile();
    }

    private void grabSong() {
        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);

        //Log.d(TAG, "tut: length "+currentSong.getContent().length());
        //Log.d(TAG, "tut:  grabSong: "+currentSong.getTitle());
        //Log.d(TAG, "tut: getContent:  "+currentSong.getContent());
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
        if (songsType.equals(SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            message = mContext.getString(R.string.nt_download_dictation_items_finished);
        }

        return message;
    }

    private void finishProcess() {
        Log.d(TAG, "tut: finishProcess");
        running = false;
        dictationTypesCount=0;
        mTts.shutdown();
    }

    private void setSpeechSpeed() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Float speechRate = Float.parseFloat(pref.getString("pref_dictation_speed", "1.5f"));
        mTts.setSpeechRate(speechRate);
    }
}
