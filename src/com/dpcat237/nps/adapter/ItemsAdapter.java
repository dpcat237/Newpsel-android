package com.dpcat237.nps.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
	
	public ItemsAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.mContext = context;
		this.mActivity = (Activity) context;
	}
	
	/*@Override
	public int getItemViewType(int position) {
		Item item = items.get(position);
		
		/*if (item.is_unread == true) {
			return 1;
		} else {
			return 2;				
		}*/
		/*return 1;
	}*/

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
		
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		/*List<Item> items = items[position];
		textView.setText(items[position]);
		
		/*View v = convertView;
		final Item article = items.get(position);
		if (v == null) {
			int layoutId = R.layout.item_row;
			/*switch (getItemViewType(position)) {
			case VIEW_LOADMORE:
				layoutId = R.layout.headlines_row_loadmore;
				break;
			case VIEW_UNREAD:
				layoutId = R.layout.headlines_row_unread;
				break;
			case VIEW_SELECTED:
				layoutId = R.layout.headlines_row_selected;
				break;
			case VIEW_SELECTED_UNREAD:
				layoutId = R.layout.headlines_row_selected_unread;
				break;
			}*/
			
			/*LayoutInflater vi = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutId, null);
			((ViewGroup)v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		}

		TextView tt = (TextView)v.findViewById(R.id.itemRowText);
		if (tt != null) {
			tt.setText(Html.fromHtml(article.title));
		}*/

		
		return v;
	}
}
