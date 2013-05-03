package com.dpcat237.nps.helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.dpcat237.nps.model.Feed;
import com.google.gson.Gson;

public class ApiHelper {
	private static final String URL_FEEDS = "http://www.newpsel.com/app_dev.php/api/feeds_sync/";
	
	public Feed[] getFeeds ()
	{
		Feed[] feeds = null;
		HttpClient httpClient = new DefaultHttpClient();
    	HttpGet get = new HttpGet(URL_FEEDS);
    	get.setHeader("content-type", "application/json");

    	try {
    		HttpResponse resp = httpClient.execute(get);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        
    		Gson gson = new Gson();
    		feeds = gson.fromJson(respStr, Feed[].class);
    	} catch(Exception ex) {
    		Log.e("ApiHelper - getFeeds","Error", ex);
    	}
    	
    	
		return feeds;
	}
}
