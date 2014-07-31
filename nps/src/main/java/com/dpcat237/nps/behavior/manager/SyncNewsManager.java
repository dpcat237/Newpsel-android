package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.SharedRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.ApiRequestHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public class SyncNewsManager {
    private static final String TAG = "NPS:SyncNewsManager";
    private Context mContext;
    private SharedPreferences pref;
    private ApiFactoryManager apiFactoryManager;
    private FeedRepository feedRepo;
    private Integer itemsSyncLimit = 300;

    public SyncNewsManager(Context context)
    {
        mContext = context;
    }

    public void startSync() {
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        apiFactoryManager = new ApiFactoryManager();
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();

        syncFeeds();
        if (pref.getBoolean("pref_items_download_enable", true)) {
            syncItems();
            feedRepo.unreadCountUpdate();
        }
        feedRepo.close();

        syncLabels();
        syncLaterItems();
        syncSharedItems();
    }

    private void syncFeeds() {
        Map<String, Object> result = ApiRequestHelper.feedsSyncRequest(mContext, apiFactoryManager, feedRepo);
        Feed[] feeds = (Feed[]) result.get("feeds");
        Boolean error = (Boolean) result.get("error");

        if (feeds == null || error) {
            return;
        }

        Integer lastUpdate = 0;
        Integer total = feeds.length;
        for (Feed feed : feeds) {
            if (feedRepo.checkFeedExists(feed.getApiId())) {
                feedRepo.updateFeed(feed);
            } else {
                feedRepo.addFeed(feed);
            }

            if (feed.getLastUpdate() > lastUpdate) {
                lastUpdate = feed.getLastUpdate();
            }
        }
        if (lastUpdate != 0 && total > 0) {
            PreferencesHelper.setLastFeedsUpdate(mContext, lastUpdate);
        }
    }

    private void syncItems() {
        ItemRepository itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        if (!checkSyncItems(itemRepo)) {
            itemRepo.close();

            return;
        }

        JSONArray viewedItems = itemRepo.getItemsToSync();
        Map<String, Object> result = ApiRequestHelper.itemsSyncRequest(mContext, apiFactoryManager, viewedItems, itemsSyncLimit);
        Item[] items = (Item[]) result.get("items");
        Boolean error = (Boolean) result.get("error");
        if (error || items.length < 1) {
            itemRepo.close();

            return;
        }

        SongRepository songRepo = new SongRepository(mContext);
        songRepo.open();
        for (Item item : items) {
            if (item.isUnread()) {
                itemRepo.addItem(item);
            } else {
                itemRepo.deleteItem(item.getApiId());
                songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
            }
        }

        if (viewedItems.length() > 0) {
            itemRepo.removeReadItems();
        }

        songRepo.close();
        itemRepo.close();
    }

    private Boolean checkSyncItems(ItemRepository itemRepo) {
        Boolean sync = false;
        Integer unreadCount = itemRepo.countUnreadItems();
        if (unreadCount < itemsSyncLimit) {
            sync = true;
            itemsSyncLimit = ((itemsSyncLimit - unreadCount) < 10)? 10 : (itemsSyncLimit - unreadCount);
        }

        return sync;
    }

    private void syncLabels() {
        LabelRepository labelRepo = new LabelRepository(mContext);
        labelRepo.open();

        Map<String, Object> resultChanged = labelRepo.getLabelsToSync();
        ArrayList<Label> changedLabelsArray = (ArrayList<Label>) resultChanged.get("labelsArray");
        Map<String, Object> result = ApiRequestHelper.labelsSyncRequest(mContext, apiFactoryManager, resultChanged);
        Label[] labels = (Label[]) result.get("labels");
        Boolean error = (Boolean) result.get("error");

        if (error) {
            labelRepo.close();

            return;
        }

        Integer lastUpdate = 0;
        if (labels != null) {
            for (Label label : labels) {
                if (label.getId() > 0) {
                    labelRepo.setApiId(label.getId(), label.getApiId());
                    labelRepo.setApiId(label.getId(), label.getApiId());
                } else {
                    labelRepo.addLabel(label, false);
                }
                lastUpdate = label.getLastUpdate();
            }
        }

        if (changedLabelsArray.size() > 0) {
            for (Label changedLabel : changedLabelsArray) {
                labelRepo.setChanged(changedLabel.getId(), false);
            }
        }

        if (lastUpdate != 0) {
            PreferencesHelper.setLastLabelsUpdate(mContext, lastUpdate);
        }

        labelRepo.close();
    }

    private void syncLaterItems() {
        LabelRepository labelRepo = new LabelRepository(mContext);
        labelRepo.open();

        JSONArray selectedItems = labelRepo.getSelectedItemsToSync();
        if (selectedItems.length() < 1) {
            labelRepo.close();

            return;
        }

        Map<String, Object> result = ApiRequestHelper.labelsSyncRequest(mContext, apiFactoryManager, selectedItems);
        Boolean error = (Boolean) result.get("error");
        if (!error) {
            labelRepo.removeLaterItems();
        }
        labelRepo.close();
    }

    private void syncSharedItems() {
        SharedRepository sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();

        JSONArray sharedItems = sharedRepo.getSharedToSync();
        if (sharedItems.length() < 1) {
            sharedRepo.close();

            return;
        }

        Map<String, Object> result = ApiRequestHelper.sharedItemsSyncRequest(mContext, apiFactoryManager, sharedItems);
        Boolean error = (Boolean) result.get("error");
        if (!error) {
            sharedRepo.removeSharedItems();
        }
        sharedRepo.close();
    }
}
