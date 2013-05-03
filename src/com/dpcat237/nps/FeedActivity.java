package com.dpcat237.nps;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class FeedActivity extends Activity {
	private EditText mTitleText;
	private Uri todoUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.feed_create);
		
		mTitleText = (EditText) findViewById(R.id.feed_create_title);
		Button confirmButton = (Button) findViewById(R.id.feed_create_button);
		
		Bundle extras = getIntent().getExtras();
		
		// Check from the saved Instance
				/*todoUri = (bundle == null) ? null : (Uri) bundle
						.getParcelable(NPSContentProvider.CONTENT_ITEM_TYPE);

				// Or passed from the other activity
				if (extras != null) {
					todoUri = extras
							.getParcelable(NPSContentProvider.CONTENT_ITEM_TYPE);

					fillData(todoUri);
				}*/
	}

}
