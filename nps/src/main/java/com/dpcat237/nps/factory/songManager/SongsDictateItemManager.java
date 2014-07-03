package com.dpcat237.nps.factory.songManager;


import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.repository.DictateItemRepository;

public class SongsDictateItemManager extends SongsManager {
    protected DictateItemRepository itemRepo;
    private static final String TAG = "NPS:SongsDictateItemManager";

    public void getListItems(Integer feedId) {
        listItems = itemRepo.getUnreadItemsByFeed(feedId);
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
        itemRepo = new DictateItemRepository(mContext);
        itemRepo.open();
    }

    public void setCreatorType() {
        grabberType = SongConstants.GRABBER_TYPE_DICTATE_ITEM;
    }

    public void setSongContent(Song song, ListItem listItem) {
        song.setContent(listItem.getText());
    }

    public void getListItem(Integer itemId){
        songListItem =  itemRepo.getListItem(itemId);
    }

    public void markAsDictated(Integer itemId) {
        itemRepo.readItem(itemId, false);
    }
}