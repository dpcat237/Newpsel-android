package com.dpcat237.nps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.task.AddFeedTask;

public class AddFeedActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.feed_add);
	}
	
	public void addFeed(View view) {
		if (GenericHelper.hasConnection(this)) {
			AddFeedTask task = new AddFeedTask(this, mView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
}