package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.manager.PlayerStateManager;
import com.dpcat237.nps.common.model.Label;

import java.util.ArrayList;

public class LabelsWearActivity extends Activity implements WearableListView.ClickListener {
    private static final String TAG = "NPSW:LabelsWearActivity";
    private PlayerStateManager stateManager;
    private WearableListView mListView;
    private ArrayList<Label> labels;
    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;
    private TextView textViewClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateManager = PlayerStateManager.getInstance(this);
        setContentView(R.layout.activity_label_list);
        mDefaultCircleRadius = getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = getResources().getDimension(R.dimen.selected_settings_circle_radius);
        labels = stateManager.getLabels();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                mListView.setAdapter(new LabelWearListViewAdapter());
                mListView.setClickListener(LabelsWearActivity.this);
            }
        });
    }

    @Override
    public void onTopEmptyRegionClick() { }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        stateManager.sendSetLabel(labels.get(viewHolder.getPosition()));

        LabelItemView itemView = (LabelItemView) viewHolder.itemView;
        textViewClick = (TextView) itemView.findViewById(R.id.textView);

        textViewClick.setTypeface(null, Typeface.BOLD);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                textViewClick.setTypeface(null, Typeface.NORMAL);
                finish();
            }
        }, 100);
    }

    private final class LabelWearListViewAdapter extends WearableListView.Adapter {
        private TextView textView;

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new LabelItemView(LabelsWearActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
            LabelItemView itemView = (LabelItemView) viewHolder.itemView;
            textView = (TextView) itemView.findViewById(R.id.textView);

            Label label = labels.get(position);
            textView.setText(label.getName());
            textView.setTypeface(null, Typeface.NORMAL);
            viewHolder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return labels.size();
        }
    }

    private final class LabelItemView extends FrameLayout {

        final TextView txtView;

        public LabelItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.fragment_label_row, this);
            txtView = (TextView) findViewById(R.id.textView);
        }
    }
}
