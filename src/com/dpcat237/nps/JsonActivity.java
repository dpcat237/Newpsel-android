package com.dpcat237.nps;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;
import com.google.gson.Gson;

public class JsonActivity extends Activity {
	private static final String SAMPLE_JSON_FILE_NAME = "public_timeline2.json";
	private static final String API_FEEDS = "http://www.newpsel.com/app_dev.php/api/feeds_sync/";
	private static Integer TEST_TUT = 0;
	private static String TEST_TUTA = "";
	private FeedRepository db;

    private LinearLayout mLayout;
    private LinearLayout.LayoutParams mLayoutParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.try_json);
		db = new FeedRepository(this);
		db.open();
		
		mLayout = (LinearLayout) findViewById(R.id.layout);
        mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView textView = new TextView(JsonActivity.this);
        textView.setText("Running tests...");
        mLayout.addView(textView, mLayoutParams);

        new Thread(mTestTask).start();
	}

	private final Runnable mTestTask = new Runnable() {
        public void run() {

            //final Map<String, String> results = new HashMap<String, String>();
            TextView textView = new TextView(JsonActivity.this);
            //testImpl(new JacksonJson(), results);
            //testImpl(new GsonJson(), results);
            testTut(textView);

            

        }
    };
    
    private void testTut(TextView textView) {
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet del = new HttpGet(API_FEEDS);
    	del.setHeader("content-type", "application/json");
    	
    	try {
    		HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
    		Gson gson = new Gson();
    		Feed[] feeds = gson.fromJson(respStr, Feed[].class);
    		
    		//db.cr();
    		//@SuppressWarnings("unchecked")
    		//ArrayAdapter<Feed> adapter = (ArrayAdapter<Feed>) getListAdapter();
    		for (Feed feed : feeds) {
    			db.addFeed(feed);
    	    }
    		
    		
    		/*for (int i = 0; i < feeds.length; i++) {
    			Feed feed = feeds[i];
    			db.addFeed(feed);
    			
    			//db.createFeed(feed.id, feed.title);
				//adapter.add(feed);
    			//TEST_TUTA = TEST_TUTA +" "+ feed.favicon;
    		}*/
    		TEST_TUTA = "ok";
    	}
    	catch(Exception ex)
    	{
    		TEST_TUTA = "ab " + ex.getMessage();
    	        Log.e("ServicioRest","Error!", ex);
    	}
    	
    	runOnUiThread(new Runnable() {
            public void run() {

                /*mLayout.removeAllViews();

                for (int i = 0; i < results.size(); i++) {
                    textView.setText(results.get(i) + ": " + "ms");
                    mLayout.addView(textView, mLayoutParams);
                }*/
                
                TextView textView = new TextView(JsonActivity.this);
                textView.setText("tuta: " + TEST_TUTA );
                mLayout.addView(textView, mLayoutParams);

                
                
                Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }
}
