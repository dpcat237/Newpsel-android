package com.dpcat237.nps.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Feed;

public class FeedsAdapter extends ArrayAdapter<Feed> {
	private ArrayList<Feed> feeds;
	Activity mActivity;
	Context mContext;
	Integer imgSize = 0;
	Integer txtSize = 0;
	
	public FeedsAdapter(Context context, int textViewResourceId, ArrayList<Feed> feeds) {
		super(context, textViewResourceId, feeds);
		this.feeds = feeds;
		this.mContext = context;
		this.mActivity = (Activity) context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Feed feed = feeds.get(position);
		LayoutInflater vi = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.feed_row, null);
		((ViewGroup)v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		ImageView img = (ImageView)v.findViewById(R.id.feedFavicon);
		TextView text = (TextView)v.findViewById(R.id.feedRowText);
		text.setText(Html.fromHtml(feed.getTitle()));
		
		TextView count = (TextView)v.findViewById(R.id.feedCount);
		Long unread = feed.getUnreadCount();
		if (unread > 0) {
			count.setText(unread.toString());
		} else {
			count.setVisibility(View.GONE);
		}
		
		setDimensions(img, text, count);
		
		return v;
	}
	
	private void setDimensions(ImageView img, TextView text, TextView count) {
		setSize();
		
		if (imgSize > 0) {
			img.getLayoutParams().height = imgSize;
			img.getLayoutParams().width = imgSize;			
		}
		
		if (txtSize > 0) {
			text.setTextSize(txtSize);
			count.setTextSize(txtSize);
		}
	}
	
	private void setSize() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String textSize = pref.getString("pref_list_size", "2");
		Integer size = Integer.parseInt(textSize);

		if (size == 1) {
			imgSize = 0;
			txtSize = 0;
		} else if (size == 2) {
			imgSize = 47;
			txtSize = 17;
		} else if (size == 3) {
			imgSize = 55;
			txtSize = 20;
		}
	}
	
}
