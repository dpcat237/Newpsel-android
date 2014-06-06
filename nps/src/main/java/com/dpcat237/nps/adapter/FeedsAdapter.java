package com.dpcat237.nps.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Feed;

import java.util.ArrayList;
import java.util.List;

public class FeedsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Feed> dataSet;

    Integer imgSize = 0;
    Integer txtSize = 0;

    public FeedsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        dataSet = new ArrayList<Feed>();
    }

    public void addToDataset(List<Feed> collection) {

        if(collection == null)
            return;

        dataSet.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Feed getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.feed_row, null);

            holder.image = (ImageView)convertView.findViewById(R.id.feedFavicon);
            holder.title = (TextView)convertView.findViewById(R.id.feedRowText);
            holder.count = (TextView)convertView.findViewById(R.id.feedCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //set data
        Feed feed =getItem(position);

        holder.title.setText(feed.getTitle());
        Integer unread = feed.getUnreadCount();
        if (unread > 0) {
            holder.count.setText(unread.toString());
        } else {
            holder.count.setVisibility(View.GONE);
        }

        setDimensions(holder.image, holder.title, holder.count);

        return convertView;
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView count;
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
