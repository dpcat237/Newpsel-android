package com.dpcat237.nps.factory;

import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.factory.grabber.Grabber;
import com.dpcat237.nps.factory.grabber.GrabberTitle;

public class SongsGrabberFactory {
    public Grabber createGrabber(String type) {
        Grabber grabber = null;

        if (type.equals(SongConstants.GRABBER_TYPE_TITLE)) {
            grabber = new GrabberTitle();
        }

        return grabber;
    }
}