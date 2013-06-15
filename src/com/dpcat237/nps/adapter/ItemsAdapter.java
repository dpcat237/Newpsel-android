package com.dpcat237.nps.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.task.StarItemTask;

public class ItemsAdapter extends ArrayAdapter<Item> {
	private ArrayList<Item> items;
	Activity mActivity;
	Context mContext;
	Integer imgSize = 0;
	Integer txtPadding = 0;
	Integer txtSize = 0;
	
	public ItemsAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.mContext = context;
		this.mActivity = (Activity) context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Item item = items.get(position);
		LayoutInflater vi = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.item_row, null);
		((ViewGroup)v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		TextView text = (TextView)v.findViewById(R.id.itemRowText);
		LinearLayout line = (LinearLayout) text.getParent();
		text.setText(Html.fromHtml(item.getTitle()));
		ImageView stared = (ImageView) line.getChildAt(0);
		
		if (!item.isUnread()) {
			String color = mContext.getString(R.string.color_read);
			line.setBackgroundColor(Color.parseColor(color));
		}
		
		if (item.isStared()) {
			stared.setBackgroundResource(R.drawable.is_stared);
		}
		
		//change status of item: is stared/isn't stared
		stared.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View view)
            {
				Boolean isStared = true;
				if (item.isStared()) {
					isStared = false;
					view.setBackgroundResource(R.drawable.isnt_stared);
				} else {
					view.setBackgroundResource(R.drawable.is_stared);
				}
				item.setIsStared(isStared);
				
				StarItemTask task = new StarItemTask(mContext, item.getId(), isStared);
				task.execute();
            }
         });
		
		setDimensions(stared, text);
		
		return v;
	}
	
	private void setDimensions(ImageView img, TextView text) {
		setSize();
		
		if (imgSize > 0) {
			img.getLayoutParams().height = imgSize;
			img.getLayoutParams().width = imgSize;			
		}
		
		if (txtPadding > 0) {
			text.setPadding(txtPadding, 0, 0, 0);
		}
		
		if (txtSize > 0) {
			text.setTextSize(txtSize);
		}
	}
	
	private void setSize() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String textSize = pref.getString("pref_list_size", "2");
		Integer size = Integer.parseInt(textSize);

		if (size == 1) {
			imgSize = 0;
			txtPadding = 0;
			txtSize = 0;
		} else if (size == 2) {
			imgSize = 47;
			txtPadding = 5;
			txtSize = 17;
		} else if (size == 3) {
			imgSize = 55;
			txtPadding = 10;
			txtSize = 20;
		}
	}
}
