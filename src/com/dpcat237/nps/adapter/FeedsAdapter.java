package com.dpcat237.nps.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Feed;

public class FeedsAdapter extends ArrayAdapter<Feed> {
	private ArrayList<Feed> feeds;
	Activity mActivity;
	Context mContext;
	
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
		TextView text = (TextView)v.findViewById(R.id.feedRowText);
		text.setText(Html.fromHtml(feed.getTitle()));
		
		TextView count = (TextView)v.findViewById(R.id.feedCount);
		Long unread = feed.getUnreadCount();
		if (unread > 0) {
			count.setText(unread.toString());
		} else {
			count.setVisibility(View.GONE);
		}
		
		return v;
	}
	
}
