package com.dpcat237.nps.common.helper;

import com.dpcat237.nps.common.model.DictateItem;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.common.model.LaterItem;
import com.dpcat237.nps.common.model.Song;
import com.google.gson.Gson;

public class JsonHelper {
	public static Feed[] getFeeds(String content) {
		Feed[] feeds = null;
		Gson gson = new Gson();

		if (content.length() > 0) {
			feeds = gson.fromJson(content, Feed[].class);
		}

		return feeds;
	}
	
	public static Item[] getItems(String content) {
		Item[] items = null;
		Gson gson = new Gson();
		
		if (content.length() > 0) {
			items = gson.fromJson(content, Item[].class);
		}

		return items;
	}
	
	public static Label[] getLabels(String content) {
		Label[] labels = null;
		Gson gson = new Gson();
		
		if (content.length() > 0) {
			labels = gson.fromJson(content, Label[].class);
		}

		return labels;
	}

    public static LaterItem[] getLaterItems(String content) {
        LaterItem[] labels = null;
        Gson gson = new Gson();

        if (content.length() > 0) {
            labels = gson.fromJson(content, LaterItem[].class);
        }

        return labels;
    }

    public static DictateItem[] getDictateItems(String content) {
        DictateItem[] items = null;
        Gson gson = new Gson();

        if (content.length() > 0) {
            items = gson.fromJson(content, DictateItem[].class);
        }

        return items;
    }

    public static Song getSong(String content) {
        Song song = null;
        Gson gson = new Gson();

        if (content.length() > 0) {
            song = gson.fromJson(content, Song.class);
        }

        return song;
    }
}
