package com.dpcat237.nps.factory;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.factory.grabber.Grabber;

public class SongsGrabber {
    private SongsGrabberFactory grabberFactory;
    private static final String TAG = "NPS:SongsGrabber";

    public SongsGrabber() {
        this.grabberFactory = new SongsGrabberFactory();
    }

    public Boolean grabSongs(String type, Context context) {
        Boolean error = false;
        Grabber grabber;

        grabber = grabberFactory.createGrabber(type);
        try {
            grabber.setup(context);
            grabber.grabSongs();
            grabber.finish();
        } catch (Exception e) {
            error = true;
            Log.d(TAG, "tut: Exception "+e.getMessage());
        }

        return error;
    }
}
