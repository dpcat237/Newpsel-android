package com.dpcat237.nps.factory.songManager;


import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.ItemRepository;

public class SongsTitleManager extends SongsManager {
    protected ItemRepository itemRepo;
    private static final String TAG = "NPS:SongsManagerTitle";

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

    public void setCreatorType() {
        grabberType = SongConstants.GRABBER_TYPE_TITLE;
    }

    public void setSongContent(Song song, ListItem listItem) {
        song.setContent(listItem.getTitle());
    }

    public void getListItem(Integer itemId){
        songListItem =  itemRepo.getListItem(itemId);
    }

    public void markAsDictated(Integer itemId) {
        itemRepo.readItem(itemId, false);
    }
}