package com.dpcat237.nps.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpcat237.nps.LabelsDialog;
import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.task.StarItemTask;

public class ItemsAdapter extends ArrayAdapter<Item> {
	private ArrayList<Item> items;
	Activity mActivity;
	Context mContext;
	Integer imgSize = 0;
	Integer txtSize = 0;
	private Button label;
	
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
		Button stared = (Button) line.getChildAt(0);
		label = (Button) line.getChildAt(1);
		
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
		
		//set label to item
		label.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View view)
            {
				showLabels(item);	
            }
         });
		
		setDimensions(stared, text);
		
		line.post(new Runnable() {
			public void run() {
				// Post in the parent's message queue to make sure the parent
				// lays out its children before we call getHitRect()
				Rect delegateArea = new Rect();
				Button delegate = label;
				delegate.getHitRect(delegateArea);
				delegateArea.top -= 20;
				delegateArea.bottom += 20;
				delegateArea.left -= 10;
				delegateArea.right += 10;
				TouchDelegate expandedArea = new TouchDelegate(delegateArea,
						delegate);
				// give the delegate to an ancestor of the view we're
				// delegating the
				// area to
				if (View.class.isInstance(delegate.getParent())) {
					((View) delegate.getParent())
							.setTouchDelegate(expandedArea);
				}
			};
		});
		
		
		return v;
	}
	
	private void setDimensions(Button img, TextView text) {
		setSize();
		
		if (imgSize > 0) {
			img.getLayoutParams().height = imgSize;
			img.getLayoutParams().width = imgSize;			
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
			txtSize = 0;
		} else if (size == 2) {
			imgSize = 47;
			txtSize = 17;
		} else if (size == 3) {
			imgSize = 55;
			txtSize = 20;
		}
	}
	
	public void showLabels(Item item) {
		FragmentManager fm = ((Activity) mContext).getFragmentManager();
		LabelsDialog editNameDialog = new LabelsDialog(mContext, item);
		editNameDialog.setRetainInstance(true);
		editNameDialog.show(fm, "fragment_select_label");
	}
}
