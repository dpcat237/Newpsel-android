package com.dpcat237.nps.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.io.File;

public class GrabDictationService implements TextToSpeech.OnInitListener {
    private static final String TAG = "NPS:GrabDictationService";
    private int CHECK_TTS_INSTALLED = 0;
    private TextToSpeech mTTS;
    private String soundFilename = null;
    private File soundFile = null;
    private final String FILENAME = "wpta_tts.wav";
    private boolean mProcessed = false;
    private MediaPlayer mMediaPlayer;
    private FileService fileService;

    /**
     * http://stackoverflow.com/questions/3860711/start-android-tts-from-broadcast-receiver-or-service
     * http://stackoverflow.com/questions/8274010/texttospeech-in-a-service  ??
     */


    public void GrabDictationService(Context context) {
        mTTS = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        /*if (status == TextToSpeech.SUCCESS) {
            int result = mTTS.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTTS.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }*/
    }






    /*@Override
    public void onUtteranceCompleted(String uttId) {
        //stopSelf();
    }*/

    /*@Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }*/

    /*@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }*/
}
