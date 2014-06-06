package com.dpcat237.nps.activity;

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
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.dialog.LabelsDialog;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.helper.LanguageHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class ItemActivity extends Activity implements TextToSpeech.OnInitListener {
    private Context mContext;
    private SharedPreferences pref;
	private Feed feed;
    private Item item;
	private ShareActionProvider mShareActionProvider;
    private TextToSpeech mTTS;
    private MenuItem dictateButton;
    private MenuItem startDictateButton;
    private MenuItem stopButton;
    private static final String TAG = "ItemActivity";
    private Boolean dictateActive = false;

    private int CACHE_MAX_SIZE = 5000;
    private int CHECK_TTS_INSTALLED = 0;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		pref = PreferenceManager.getDefaultSharedPreferences(mContext);

	    setContentView(R.layout.item_view);
        getNecessaryData();

        prepareWebView();
	}

    /**
     * Get item and feed data
     */
    private void getNecessaryData() {
        ItemRepository itemRepo = new ItemRepository(this);
        itemRepo.open();
        FeedRepository feedRepo = new FeedRepository(this);
        feedRepo.open();

        //Get passed item Id and them his and feed data
        Intent intent = getIntent();
        Long itemId = intent.getLongExtra(ItemConstants.ITEM_ID, 0);
        item = itemRepo.getItem(itemId);
        Integer feedId = GenericHelper.getSelectedFeed(mContext);
        feed = feedRepo.getFeed(feedId);
    }

    private String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-MM kk:mm:ss");
            Date netDate = (new Date(timeStamp));

            return sdf.format(netDate);
        } catch(Exception ex) {
            return null;
        }
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
        LabelsDialog editNameDialog = new LabelsDialog(mContext, item);
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
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }


    /** Dictate methods **/
    private void dictate() {
        String speech = GenericHelper.stripHtml(item.getContent());

        HashMap<String, String> myHashRender = new HashMap();
        String utteranceID = "item_dictate_"+item.getId();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
        mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, myHashRender);

        dictateButton.setVisible(false);
        stopButton.setVisible(true);
    }

    private void stopDictate() {
        mTTS.stop();
        stopButton.setVisible(false);
        dictateButton.setVisible(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_TTS_INSTALLED) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(mContext, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
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

    @Override
    @TargetApi(15)
    public void onInit(int initStatus) {
        if (dictateActive) {
            Locale localeTTs = LanguageHelper.getLocaleFromLanguageTTS(item.getLanguage(), mTTS);
            if (localeTTs != null && mTTS.isLanguageAvailable(localeTTs) == TextToSpeech.LANG_AVAILABLE) {
                startDictateButton.setVisible(false);
                dictateButton.setVisible(true);
                mTTS.setLanguage(localeTTs);
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


    /** Web view **/
    private void prepareWebView() {
        WebView mWebView = (WebView) findViewById(R.id.itemContent);
        mWebView.setFocusable(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings ws = mWebView.getSettings();
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(false);
        String textSize = pref.getString("pref_text_size", "100");
        ws.setTextZoom(Integer.parseInt(textSize));

        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setAppCacheMaxSize(CACHE_MAX_SIZE);
        ws.setAppCacheEnabled(true);

        long timestamp = item.getDateAdd() * 1000;
        String date = getDate(timestamp);
        String itemLink = item.getLink();

        String contentHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<div style='border-bottom:1px solid #d3d3d3; padding-bottom:4px; font-weight: bold; font-size:1em;'>" +
                "<a style='text-decoration: none; color:#12c;' href='"+itemLink+"'>"+item.getTitle()+"</a>" +
                "</div>" +
                "<p style='margin-top:1px; font-size:1em;'><font style='color:#12c;'>"+feed.getTitle()+"</font>" +
                " <font style='color:#d3d3d3;'>on "+date+"</font></p>";

        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        String content = "<div style='padding:0px 3px 0px 2px;'>"+contentHeader+item.getContent()+"</div>";
        mWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    }
}