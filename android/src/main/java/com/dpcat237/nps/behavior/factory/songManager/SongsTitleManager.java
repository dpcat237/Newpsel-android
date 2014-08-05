package com.dpcat237.nps.behavior.factory.songManager;


import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.model.List;
import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;
import com.dpcat237.nps.database.repository.ItemRepository;

public class SongsTitleManager extends SongsManager {
    protected ItemRepository itemRepo;
    private static final String TAG = "NPS:SongsManagerTitle";
    private List currentList;

    protected void getListItems(Integer listId) {
        listItems = itemRepo.getUnreadItemsTitles(listId);
    }

    protected void getLists() {
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

    protected void setCreatorType() {
        grabberType = SongConstants.GRABBER_TYPE_TITLE;
    }

    protected void setSongContent(Song song, ListItem listItem) {
        song.setContent(listItem.getTitle());
    }

    protected void getListItem(Integer itemId){
        songListItem =  itemRepo.getListItem(itemId);
    }

    protected void markAsDictated(Integer itemApiId) {
        itemRepo.readItem(itemApiId, false);
    }

    protected void markTtsError(Integer itemApiId) {}

    protected void createSongsProcess() {
        getLists();

        for (List list : lists) {
            currentList = list;
            getListItems(list.getApiId());
            createListSongs();
        }
    }

    protected void createListSong(ListItem listItem) {
        Song song = createSong(currentList, listItem);
        songRepo.addSong(song);
    }
}