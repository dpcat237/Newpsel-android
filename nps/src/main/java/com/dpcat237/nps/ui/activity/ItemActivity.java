package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.ui.block.ItemBlock;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

import java.util.HashMap;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class ItemActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final String TAG = "NPS:ItemActivity";
    private Context mContext;
	private Feed feed;
    private Item item;
	private ShareActionProvider mShareActionProvider;
    private TextToSpeech mTts;
    private MenuItem dictateButton;
    private MenuItem startDictateButton;
    private MenuItem stopButton;
    private Boolean dictateActive = false;
    private SharedPreferences pref;
    private ItemRepository itemRepo;
    private FeedRepository feedRepo;

    private int CHECK_TTS_INSTALLED = 0;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);

	    setContentView(R.layout.activity_item_view);
        getNecessaryData();

        WebView mWebView = (WebView) findViewById(R.id.itemContent);
        ItemBlock.prepareWebView(mWebView, pref.getString("pref_text_size", "100"), item.getLink(), item.getTitle(), feed.getTitle(), item.getContent(), item.getDateAdd());
	}

    @Override
    @TargetApi(15)
    public void onInit(int initStatus) {
        if (dictateActive) {
            Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS(item.getLanguage(), mTts);
            if (localeTTs != null && mTts.isLanguageAvailable(localeTTs) == TextToSpeech.LANG_AVAILABLE) {
                startDictateButton.setVisible(false);
                dictateButton.setVisible(true);
                mTts.setLanguage(localeTTs);
            }

            setTtsListener();
        }
    }

    @SuppressLint("NewApi")
    private void setTtsListener()
    {
        final ItemActivity callWithResult = this;
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

    public void onDone()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopDictation();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS_INSTALLED) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTts = new TextToSpeech(mContext, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void openDB() {
        itemRepo = new ItemRepository(this);
        itemRepo.open();
        feedRepo = new FeedRepository(this);
        feedRepo.open();
    }

    private void closeDB() {
        itemRepo.close();
        feedRepo.close();
    }

    /**
     * Get item and feed data
     */
    private void getNecessaryData() {
        openDB();

        Integer itemApiId = getItemApiId();
        item = itemRepo.getItem(itemApiId);
        Integer feedId = PreferencesHelper.getSelectedFeed(mContext);
        feed = feedRepo.getFeed(feedId);

        closeDB();
    }

    private Integer getItemApiId() {
        Integer itemApiId = PreferencesHelper.getCurrentItemApiId(mContext);
        if (itemApiId > 1) {
            itemRepo.readItem(itemApiId, false);

            return itemApiId;
        }

        return getIntent().getIntExtra(ItemConstants.ITEM_API_ID, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        //get menu items
        MenuItem shareItem = menu.findItem(R.id.buttonShare);
        startDictateButton = menu.findItem(R.id.buttonStartDictate);
        dictateButton = menu.findItem(R.id.buttonDictate);
        stopButton = menu.findItem(R.id.buttonStop);

        if (item.getLanguage() != null) {
            startDictateButton.setVisible(true);
        }

        //prepare share button
        mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        mShareActionProvider.setShareIntent(createShareIntent());

        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getLink());
        shareIntent.setType("text/plain");

        return shareIntent;
    }


    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void setLabel()
    {
        FragmentManager fm = ((Activity) mContext).getFragmentManager();
        LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getApiId());
        editNameDialog.setRetainInstance(true);
        editNameDialog.show(fm, "fragment_select_label");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonDictate:
                dictate();
                return true;
            case R.id.buttonLabel:
                setLabel();
                return true;
            case R.id.buttonShare:
                setShareIntent(createShareIntent());
                return true;
            case R.id.buttonStartDictate:
                startDictateButton.setEnabled(false);
                startDictate();
                return true;
            case R.id.buttonStop:
                stopDictate();
                return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    /** Dictate methods **/
    private void dictate() {
        String speech = PreferencesHelper.stripHtml(item.getContent());

        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "item_dictate_"+item.getId();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        Float speechRate = Float.parseFloat(pref.getString("pref_dictation_speed", "1.0f"));
        mTts.setSpeechRate(speechRate);
        mTts.speak(speech, TextToSpeech.QUEUE_FLUSH, myHashRender);

        dictateButton.setVisible(false);
        stopButton.setVisible(true);
    }

    private void stopDictate() {
        mTts.stop();
        stopButton.setVisible(false);
        dictateButton.setVisible(true);
    }

    private void startDictate() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, CHECK_TTS_INSTALLED);
        dictateActive = true;
    }

    public void stopDictation() {
        stopButton.setVisible(false);
        dictateButton.setVisible(true);
    }
}