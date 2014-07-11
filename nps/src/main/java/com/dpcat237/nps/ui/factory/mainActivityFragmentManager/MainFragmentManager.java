package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.app.Activity;
import android.widget.ListView;

public abstract class MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentManager";
    protected Activity mActivity;
    protected ListView listView;
    protected int managerType;


    public void finish() { }

    public void setup(Activity activity, ListView listView) {
        this.mActivity = activity;
        this.listView = listView;

        setCreatorType();
        openDB();
        initializeAdapter();
    }

    public void prepareAdapter() {
        setTitle();
        setItems();
        setAdapter();
        setOnClickListener();
        showToast();
    }

    //abstract methods
    abstract protected void setCreatorType();
    abstract protected void setTitle();
    abstract protected void openDB();
    abstract protected void initializeAdapter();
    abstract protected void setItems();
    abstract protected void setAdapter();
    abstract protected void setOnClickListener();
    abstract protected void showToast();
}