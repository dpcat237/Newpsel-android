package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.database.repository.LabelRepository;

import java.util.ArrayList;

abstract public class MainFragmentLabels extends MainFragmentManager {
    protected static final String TAG = "NPS:MainFragmentLabels";
    protected LabelRepository labelRepo;
    protected ArrayList<Label> labels;

    public void finish() {
        super.finish();
        labelRepo.close();
    }

    protected void openDB() {
        labelRepo = new LabelRepository(mActivity);
        labelRepo.open();
    }
}