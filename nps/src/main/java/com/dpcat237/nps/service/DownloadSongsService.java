package com.dpcat237.nps.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.factory.SongsGrabber;
import com.dpcat237.nps.factory.SongsGrabberFactory;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.receiver.AlarmReceiver;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class DownloadSongsService extends IntentService {
    public DownloadSongsService() {
        super("SchedulingService");
    }
    
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds 
    // the string, it indicates the presence of a doodle.  
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private static final String TAG = "NPS:DownloadSongsService";
    private volatile static Boolean running = false;
    private static Integer count = 0;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "tut: onHandleIntent");
        if (!running) {
            synchronized ("running") {
                if (!running && GenericHelper.hasConnection(getApplicationContext())) {
                    running = true;

                    try {
                        startProcess();
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
        }

if(count >= 3) {
    stopSelf();
    running = false;
    count = 0;
} else {
    count++;
    Log.d(TAG, "tut: count "+count);
}



        AlarmReceiver.completeWakefulIntent(intent);
        // Try to connect to the Google homepage and download content.
        /*
        // If the app finds the string "doodle" in the Google home page content, it
        // indicates the presence of a doodle. Post a "Doodle Alert" notification.
        if (result.indexOf(SEARCH_STRING) != -1) {
            sendNotification(getString(R.string.doodle_found));
            Log.i(TAG, "Found doodle!!");
        } else {
            sendNotification(getString(R.string.no_doodle));
            Log.i(TAG, "No doodle found. :-(");
        }
        // Release the wake lock provided by the BroadcastReceiver.
        SampleAlarmReceiver.completeWakefulIntent(intent);*/
        // END_INCLUDE(service_onhandle)
    }

    private void startProcess() {
        Log.d(TAG, "tut: startProcess");

        SongsGrabber songsGrabber = new SongsGrabber();
        songsGrabber.grabSongs(SongConstants.GRABBER_TYPE_TITLE, getApplicationContext());

        return;
        /*FeedRepository feedRepo = new FeedRepository(getApplicationContext());
        feedRepo.open();
        ItemRepository itemRepo = new ItemRepository(this);
        itemRepo.open();
        ArrayList<Feed> feeds = feedRepo.getAllFeedsUnread();
        Log.d(TAG, "tut: feeds "+feeds.size());
        for (Feed feed : feeds) {
            Integer tt = (int) feed.getApiId();
            ArrayList<Item> items = itemRepo.getIsUnreadItems(tt, true);
            Log.d(TAG, "tut: items "+items.size()+" - "+feed.getTitle());

            return;
        }*/
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        /*mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(getString(R.string.doodle_alert))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
    }
}
