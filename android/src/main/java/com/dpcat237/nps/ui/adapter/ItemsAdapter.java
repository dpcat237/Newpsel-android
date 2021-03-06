package com.dpcat237.nps.ui.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.StarItemTask;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Item> dataSet;
    private ViewHolder vHolder;

    public static class ViewHolder {
        public LinearLayout line;
        public ImageView stared;
        public ImageView label;
        public TextView text;
    }

    public ItemsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        dataSet = new ArrayList<Item>();
    }

    public void addToDataset(List<Item> collection) {
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
    public Item getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        vHolder = holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_row, null);

            holder.stared = (ImageView)convertView.findViewById(R.id.itemStared);
            holder.label = (ImageView)convertView.findViewById(R.id.itemLabel);
            holder.text = (TextView)convertView.findViewById(R.id.itemRowText);
            holder.line = (LinearLayout) holder.text.getParent();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //set data
        Item item = getItem(position);
        holder.text.setText(item.getTitle());

        if (item.isUnread()) {
            String color = mContext.getString(R.string.color_unread);
            holder.line.setBackgroundColor(Color.parseColor(color));
        } else {
            String color = mContext.getString(R.string.color_read);
            holder.line.setBackgroundColor(Color.parseColor(color));
        }


        if (item.isStared()) {
            holder.stared.setImageResource(R.drawable.is_stared);
        } else {
            holder.stared.setImageResource(R.drawable.isnt_stared);
        }

        //change to stared or remove star
        holder.stared.setOnClickListener(new OnStaredClick(position));

        //set label to item
        holder.label.setOnClickListener(new OnLabelClick(position));

        setDimensions(holder.text);

        return convertView;
    }

    public class OnLabelClick implements View.OnClickListener {

        private int mPosition;
        public OnLabelClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Item item = getItem(mPosition);
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getApiId());
            editNameDialog.setRetainInstance(true);
            editNameDialog.show(fm, "fragment_select_label");
        }
    }

    public class OnStaredClick implements View.OnClickListener {

        private int mPosition;
        public OnStaredClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Boolean isStared;
            Item item = getItem(mPosition);
            if (item.isStared()) {
                isStared = false;
            } else {
                isStared = true;
            }

            item.setIsStared(isStared);
            StarItemTask task = new StarItemTask(mContext, item.getId(), isStared);
            task.execute();
            notifyDataSetChanged();
        }
    }

    private void setDimensions(TextView text) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Integer textSize = Integer.parseInt(pref.getString("pref_list_size", "17"));
        if (textSize < 14) {
            textSize = 17;
        }
        text.setTextSize(textSize);
    }

    public void updateList(List<Item> collection) {
        dataSet.clear();
        dataSet.addAll(collection);
        notifyDataSetChanged();
    }
}
