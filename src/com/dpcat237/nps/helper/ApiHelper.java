package com.dpcat237.nps.helper;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.google.gson.Gson;

public class ApiHelper {
	private static final String URL_SYNC_FEEDS = "http://www.newpsel.com/app_dev.php/api/sync_feeds/";
	private static final String URL_SYNC_ITEMS_UNREAD = "http://www.newpsel.com/app_dev.php/api/sync_unread/";
	private static final String URL_LOGIN = "http://www.newpsel.com/app_dev.php/api/login/";
	
	public Feed[] getFeeds (String appKey, Integer lastUpdate) {
		Boolean checkProcess = true;
		Feed[] feeds = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_FEEDS);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("appKey", appKey);
			jsonData.put("lastUpdate", lastUpdate);
		} catch (JSONException e) {
			Log.e("ApiHelper - getFeeds","Error", e);
			checkProcess = false;
		}
		
		if (checkProcess) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - getFeeds","Error", e);
				checkProcess = false;
			}
			
			if (checkProcess) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					
					if (!GenericHelper.isNumeric(respStr)) {
						feeds = JsonHelper.getFeeds(respStr);
					} else {
						checkProcess = false;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - getFeeds","Error", e);
		    		checkProcess = false;
		    	}
			}
		}
    	
		return feeds;
	}
	
	public Item[] getItems (String appKey, Integer viewedFeeds, Boolean isDownload) {
		Boolean checkProcess = true;
		Item[] items = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_ITEMS_UNREAD);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();

		try {
			jsonData.put("appKey", appKey);
			jsonData.put("viewedFeeds", viewedFeeds);
			jsonData.put("isDownload", isDownload);
		} catch (JSONException e) {
			Log.e("ApiHelper - getItems","Error", e);
			checkProcess = false;
		}
		
		if (checkProcess) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - getItems","Error", e);
				checkProcess = false;
			}
			
			if (checkProcess) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					
					if (!GenericHelper.isNumeric(respStr)) {
						items = JsonHelper.getItems(respStr);
						checkProcess = true;
					} else {
						checkProcess = false;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - getItems","Error", e);
		    		checkProcess = false;
		    	}
			}
		}
    	
		return items;
	}
	
	public static Boolean doLogin(String username, String password, String appKey) {
		Boolean checkLogin = true;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_LOGIN);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("username", username);
			jsonData.put("password", password);
			jsonData.put("appKey", appKey);
		} catch (JSONException e) {
			Log.e("ApiHelper - doLogin","Error", e);
			checkLogin = false;
		}
		
		if (checkLogin) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - doLogin","Error", e);
				checkLogin = false;
			}
			
			if (checkLogin) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					//GenericHelper.isNumeric(respStr);
					if(respStr.equals("100")) {
						checkLogin = true;
					} else {
						checkLogin = false;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - doLogin","Error", e);
		    		checkLogin = false;
		    	}
			}
		}
		
		return checkLogin;
	}
}
