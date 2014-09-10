package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.AddFeedTask;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;

public class AddFeedActivity extends Activity {
    private Context mContext;
	private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_feed_add);
	}
	
	public void addFeed(View view) {
		if (ConnectionHelper.hasConnection(mContext)) {
            DisplayHelper.hideKeyboard(mContext, mView);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);

			AddFeedTask task = new AddFeedTask(mContext, mView);
			task.execute();
		} else {
			Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
}