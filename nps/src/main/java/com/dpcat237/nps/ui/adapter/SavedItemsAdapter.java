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
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsAdapter extends BaseAdapter {
    private static final String TAG = "NPS:SavedItemsAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ListItem> dataSet;

    public static class ViewHolder {
        public LinearLayout line;
        public ImageView label;
        public TextView text;
    }

    public SavedItemsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        dataSet = new ArrayList<ListItem>();
    }

    public void addToDataset(List<ListItem> collection) {
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
    public ListItem getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_saveditem_row, null);

            holder.label = (ImageView)convertView.findViewById(R.id.itemLabel);
            holder.text = (TextView)convertView.findViewById(R.id.itemRowText);
            holder.line = (LinearLayout) holder.text.getParent();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //set data
        ListItem item = getItem(position);
        holder.text.setText(item.getTitle());

        if (item.isUnread()) {
            String color = mContext.getString(R.string.color_unread);
            holder.line.setBackgroundColor(Color.parseColor(color));
        } else {
            String color = mContext.getString(R.string.color_read);
            holder.line.setBackgroundColor(Color.parseColor(color));
        }

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
            ListItem item = getItem(mPosition);
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getItemApiId());
            editNameDialog.setRetainInstance(true);
            editNameDialog.show(fm, "fragment_select_label");
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
}
