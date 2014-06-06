package com.dpcat237.nps.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.service.FileService;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class SpeechActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {
    Context mContext;
    View mView;

    private int CHECK_TTS_INSTALLED = 0;
    private TextToSpeech mTTS;
    private String soundFilename = null;
    private File soundFile = null;
    private final String FILENAME = "wpta_tts.wav";
    private boolean mProcessed = false;
    private MediaPlayer mMediaPlayer;
    private static final String TAG = "NPS:SpeechActivity";
    private FileService fileService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mView = this.findViewById(android.R.id.content).getRootView();
        fileService = FileService.getInstance();

        setContentView(R.layout.speech);

        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        Button playButton = (Button)findViewById(R.id.play);
        playButton.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, CHECK_TTS_INSTALLED);
    }

    @Override
    public void onClick(View v) {
        //String words = enteredText.getText().toString();
        String words = "London based security company CertiVox, which sells information security infrastructure-as-a-service — providing enterprises with a multi-factor online authentication solution that does away with the need for passwords and usernames,  has closed an $8 million Series B round of investment from new investors NTT Docomo Ventures (making its first investment in Europe), along with existing investor Octopus Investments. " +
                "The new funding round brings CertiVox’s total raised to $17 million it said today — including a $1.5 million funding intake back in 2011, the year the company was founded. " +
                "CertiVox said it will use the new round of funding to accelerate development and reach of its M-Pin Strong Authentication Platform, which is a software-based multi-factor authentication platform that replaces password-based logins without the need for third party authentication devices. " +
                "Specifically, it said it plans to expand its sales, business development and marketing efforts, and expand partnerships with cloud service providers, OEM channels (including smart device makers and mobile service operators) and independent software vendors. " +
                "The mobile element explains NTT Docomo Ventures’ interest. The Japanese mobile operator announced an investment arm last year. Security and safety was one of the strategic fields NTT identified when it launched the fund. " +
                "CertiVox’s flagship M-Pin authentication system works in the browser or on mobile devices, with a focus on being user-friendly and avoiding the complexity that can dog some multi-factor authentication security systems. The technology can either be deployed by CertiVox’s customers on premise, or operated as a cloud-based service. " +
                "Their users then type a four-digit pin number into CertiVox’s software PIN pad to authenticate a session by generating a cryptographic key. The company says the system works in the same way that bank ATMs authenticate a user session.";

        //String words = "Hello, how are you?";

        switch (v.getId()) {
            case R.id.save:
                saveFile(words);
                break;
            case R.id.play:
                initializeMediaPlayer();
                break;
        }
    }

    private void saveFile(String words)
    {
        if (fileService.getError()) {
            return;
        }
        File voicesFolder = fileService.getVoicesFolder();
        String fileName = voicesFolder.getAbsolutePath()+"/test2.wav";
        File soundFile = new File(fileName);
        if (soundFile.exists()) {
            soundFile.delete();
        }

        Log.i(TAG, "tut: file2 "+fileName);

        /*HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "item_dictate_aa";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTTS.speak("Hello, how are you? what do you want?", TextToSpeech.QUEUE_FLUSH, myHashRender);
        Log.i(TAG, "dictate 2");*/


        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTTS.synthesizeToFile(words, myHashRender, fileName);

        Log.i(TAG, "tut: added "+soundFile.getPath());





        /*String appFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Newpsel";
        //soundFilename = "/sdcard/Newpsel/texttovoice/testvoice.wav";
        /*soundFilename = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Newpsel";
        soundFile = new File(soundFilename+"/voices/test.wav");
        if (soundFile.exists()) {
            soundFile.delete();
        }*/

        /*File folder = new File(appFolder);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            Toast.makeText(mContext, "tut: done",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "tut: ops",Toast.LENGTH_SHORT).show();
        }*/

        //File folder = new File(Environment.getExternalStorageDirectory() + "/TollCulator");
        /*boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }*/


        /*HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "wpta";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTTS.synthesizeToFile(words, myHashRender, soundFilename);

        Toast.makeText(mContext, "tut: added",Toast.LENGTH_SHORT).show();*/

        /*HashMap<String, String> myHashRender = new HashMap();
        String textToConvert = "this is a demo for saving a WAV file";
        String destinationFileName = "/sdcard/test.wav";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, textToConvert);
        //mTTS.synthesizeToFile(textToConvert, myHashRender, destinationFileName);

        if(mTTS.synthesizeToFile(words, myHashRender, destinationFileName) == TextToSpeech.SUCCESS) {
            Toast.makeText(mContext, "Sound file created",Toast.LENGTH_SHORT).show();

            // Initializes Media Player
            //initializeMediaPlayer();

            // Start Playing Speech
            //playMediaPlayer(0);
        } else {
            Toast.makeText(mContext, "Oops! Sound file not created",Toast.LENGTH_SHORT).show();
        }*/


        /*if(mTTS.synthesizeToFile(words, null, soundFilename)== TextToSpeech.SUCCESS) {
            Toast.makeText(mContext, "Sound file created",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Oops! Sound file not created",Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onInit(int initStatus) {
        Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS("en", mTTS);
        if (localeTTs != null && mTTS.isLanguageAvailable(localeTTs) == TextToSpeech.LANG_AVAILABLE) {
            mTTS.setLanguage(localeTTs);
        }

        setTtsListener();

        Button button = (Button)findViewById(R.id.save);
        button.setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS_INSTALLED) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        super.onPause();
    }

    private void initializeMediaPlayer(){
        File voicesFolder = fileService.getVoicesFolder();
        String fileName = voicesFolder.getAbsolutePath()+"/test.wav";
        Uri uri  = Uri.parse("file://" + fileName);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            Log.i(TAG, "tut: player ok");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playMediaPlayer(int status){

        // Start Playing
        if(status==0){
            mMediaPlayer.start();
        }

        // Pause Playing
        if(status==1){
            mMediaPlayer.pause();
        }
    }

    @SuppressLint("NewApi")
    private void setTtsListener()
    {
        final SpeechActivity callWithResult = this;
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult =
                    mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
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
                    mTTS.setOnUtteranceCompletedListener(
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

    public void onDone()
    {
        Log.i(TAG, "done");
    }
}