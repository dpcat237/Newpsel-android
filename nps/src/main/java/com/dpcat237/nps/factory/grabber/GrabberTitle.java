package com.dpcat237.nps.factory.grabber;


import android.util.Log;

import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.ItemRepository;

public class GrabberTitle extends Grabber {
    protected ItemRepository itemRepo;
    private static final String TAG = "NPS:GrabberTitle";


    public void getListItems(Integer listId) {
        listItems = itemRepo.getUnreadItemsTitles(listId);
    }

    public void getLists() {
        lists = feedRepo.getLists();
    }

    @Override
    public void finish() {
        super.finish();
        itemRepo.close();
    }

    @Override
    public void openDB() {
        super.openDB();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }

    public void setGrabberType() {
        grabberType = SongConstants.GRABBER_TYPE_TITLE;
    }

    public void setSongContent(Song song, ListItem listItem) {
        song.setContent(listItem.getTitle());
    }
}