package com.dpcat237.nps.helper;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
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
}
