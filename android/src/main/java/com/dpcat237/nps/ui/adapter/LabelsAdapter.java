package com.dpcat237.nps.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Label> dataSet;
    private Integer txtSize = 0;

    public static class ViewHolder {
        public TextView title;
        public TextView count;
    }

    public LabelsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        dataSet = new ArrayList<Label>();
    }

    public void addToDataset(List<Label> collection) {
        if (collection == null) {
            return;
        }

        dataSet.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Label getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.fragment_label_row, null);

            holder.title = (TextView)convertView.findViewById(R.id.feedRowText);
            holder.count = (TextView)convertView.findViewById(R.id.feedCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //set data
        Label label = getItem(position);

        holder.title.setText(label.getName());
        Integer unread = label.getUnreadCount();
        if (unread > 0) {
            holder.count.setText(unread.toString());
        } else {
            holder.count.setVisibility(View.GONE);
        }

        setDimensions(holder.title, holder.count);

        return convertView;
    }

    private void setDimensions(TextView text, TextView count) {
        getDimensions();
        text.setTextSize(txtSize);
        count.setTextSize(txtSize);
    }

    private void getDimensions() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        txtSize = Integer.parseInt(pref.getString("pref_list_size", "17"));
        if (txtSize < 14) {
            txtSize = 17;
        }
    }
}
