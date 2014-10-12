package com.dpcat237.nps.behavior.factory.songManager;


import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.ListItem;
import com.dpcat237.nps.common.model.Song;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;

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
        Feed list;
        if (listItem.getListApiId() > 0) {
            list = feedRepo.getFeed(listItem.getListApiId());
        } else {
            list = new Feed();
            list.setApiId(999999999);
            list.setTitle("Shared");
        }

        Song song = createSong(list, listItem);
        songRepo.addSong(song);
    }
}