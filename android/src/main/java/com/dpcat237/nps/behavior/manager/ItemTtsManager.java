package com.dpcat237.nps.behavior.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.dpcat237.nps.common.constant.BroadcastConstants;
import com.dpcat237.nps.helper.BroadcastHelper;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.helper.PreferencesHelper;

import java.util.HashMap;
import java.util.Locale;

public class ItemTtsManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "NPS:ItemTtsManager";
    private Context mContext;
    private TextToSpeech mTts;
    //private int CHECK_TTS_INSTALLED = 0;
    private Boolean ttsActive = false;
    private String currentLanguage;
    private Boolean error = false;

    public void prepareTts(Context context, String language) {
        error = false;
        mContext = context;
        currentLanguage = language;
        if (!ttsActive) {
            mTts = new TextToSpeech(mContext, this);
            /*Intent checkTTSIntent = new Intent(); TODO: check in case need install tts
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            mActivity.startActivityForResult(checkTTSIntent, CHECK_TTS_INSTALLED);*/
            ttsActive = true;
        } else {
            setDictationLanguage();

            if (!error) {
                notifyActive();
            }
        }
    }

    @Override
    public void onInit(int initStatus) {
        setTtsListener();
        setDictationLanguage();

        if (!error) {
            notifyActive();
        }
    }

    private void setDictationLanguage() {
        Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS(currentLanguage, mTts);
        if (localeTTs != null && LanguageHelper.isLanguageAvailable(mContext, mTts, localeTTs)) {
            mTts.setLanguage(localeTTs);
        } else {
            error = true;
        }
    }

    @SuppressLint("NewApi")
    private void setTtsListener() {
        final ItemTtsManager callWithResult = this;
        int listenerResult =
                mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onDone(String utteranceId) { callWithResult.notifyFinished(); }

                    @Override
                    public void onError(String utteranceId) { }

                    @Override
                    public void onStart(String utteranceId) { }
                });
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e(TAG, "failed to add utterance progress listener");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "tut: onActivityResult");
        /*if (requestCode == CHECK_TTS_INSTALLED) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTts = new TextToSpeech(mContext, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }*/
    }

    private void notifyActive() {
        BroadcastHelper.launchBroadcast(mContext, BroadcastConstants.ITEM_ACTIVITY, BroadcastConstants.ITEM_ACTIVITY_MESSAGE, BroadcastConstants.COMMAND_A_ITEM_TTS_ACTIVE);
    }

    private void notifyFinished() {
        BroadcastHelper.launchBroadcast(mContext, BroadcastConstants.ITEM_ACTIVITY, BroadcastConstants.ITEM_ACTIVITY_MESSAGE, BroadcastConstants.COMMAND_A_ITEM_TTS_ACTIVE);
    }

    public void dictate(String text, Float speechRate) {
        String speech = PreferencesHelper.stripHtml(text);

        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "item_dictate";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTts.setSpeechRate(speechRate);
        mTts.speak(speech, TextToSpeech.QUEUE_FLUSH, myHashRender);
    }

    public void stopDictation() {
        mTts.stop();
    }

    public void stop() {
        mTts.shutdown();
        mTts = null;
        ttsActive = false;
    }
}