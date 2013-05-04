package com.dpcat237.nps.helper;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dpcat237.nps.model.Feed;
import com.google.gson.Gson;

public class ApiHelper {
	private static final String URL_FEEDS = "http://www.newpsel.com/app_dev.php/api/feeds_sync/";
	private static final String URL_LOGIN = "http://www.newpsel.com/app_dev.php/api/login/";
	
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
    	} catch(Exception e) {
    		Log.e("ApiHelper - getFeeds","Error", e);
    	}
    	
    	
		return feeds;
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
					if(respStr.equals("true")) {
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
