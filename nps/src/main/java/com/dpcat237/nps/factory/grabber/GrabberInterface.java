package com.dpcat237.nps.factory.grabber;

import com.dpcat237.nps.model.ListItem;
import com.dpcat237.nps.model.Song;

public interface GrabberInterface {
    void getLists();
    void getListItems(Integer listId);
    void setGrabberType();
    void setSongContent(Song song, ListItem listItem);
}