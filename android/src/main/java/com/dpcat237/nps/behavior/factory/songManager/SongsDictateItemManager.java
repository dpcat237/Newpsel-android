package com.dpcat237.nps.behavior.factory.songManager;


import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.ListItem;
import com.dpcat237.nps.common.model.Song;

public class SongsDictateItemManager extends SongsManager {
    protected DictateItemRepository itemRepo;
    private static final String TAG = "NPS:SongsDictateItemManager";

    protected void getListItems() {
        listItems = itemRepo.getUnreadItems();
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

    protected void setCreatorType() {
        grabberType = SongConstants.GRABBER_TYPE_DICTATE_ITEM;
    }

    protected void setSongContent(Song song, ListItem listItem) {
        String content = song.getListTitle()+" "+song.getTitle()+" "+listItem.getText();
        song.setContent(content);
    }

    protected void getListItem(Integer itemApiId){
        songListItem =  itemRepo.getListItem(itemApiId);
        if (songListItem == null) {
            error = true;
        }
    }

    protected void markAsDictated(Integer itemApiId) {
        itemRepo.readItem(itemApiId, false);
    }

    protected void markTtsError(Integer itemApiId) {
        itemRepo.markItemTtsError(itemApiId);
    }

    protected void createSongsProcess() {
        getListItems();
        createListSongs();
    }

    protected void createListSong(ListItem listItem) {
        Feed list = feedRepo.getFeed(listItem.getListApiId());
        Song song = createSong(list, listItem);
        songRepo.addSong(song);
    }
}