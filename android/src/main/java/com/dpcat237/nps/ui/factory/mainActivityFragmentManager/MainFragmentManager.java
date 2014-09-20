package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;

public abstract class MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentManager";
    protected Activity mActivity;
    protected ListView listView;
    protected int managerType;
    protected SharedPreferences preferences;


    public void finish() { }

    public void setup(Activity activity, ListView listView) {
        this.mActivity = activity;
        this.listView = listView;

        preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        setCreatorType();
        openDB();
        initializeAdapter();
    }

    public Integer prepareAdapter() {
        setTitle();
        setItems();
        setAdapter();
        setOnClickListener();

        return countItems();
    }

    //abstract methods
    abstract protected void setCreatorType();
    abstract protected void setTitle();
    abstract protected void openDB();
    abstract protected void initializeAdapter();
    abstract protected void setItems();
    abstract protected void setAdapter();
    abstract protected void setOnClickListener();
    abstract protected Integer countItems();
}