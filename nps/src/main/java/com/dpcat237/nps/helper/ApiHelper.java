package com.dpcat237.nps.helper;

import android.util.Log;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ApiHelper {
	private static final String API_URL = "http://www.newpsel.com/api/";
	private static final String URL_SYNC_FEEDS = API_URL+"sync_feeds/";
	private static final String URL_SYNC_ITEMS_UNREAD = API_URL+"sync_unread/";
	private static final String URL_LOGIN = API_URL+"login/";
	private static final String URL_SIGN_UP = API_URL+"sign_up/";
	private static final String URL_ADD_FEED = API_URL+"add_feed/";
	private static final String URL_SYNC_LABELS = API_URL+"sync_labels/";
	private static final String URL_SYNC_LATER_ITEMS = API_URL+"sync_later/";
	private static final String URL_SYNC_SHARED_ITEMS = API_URL+"sync_shared/";
	
	public Map<String, Object> getFeeds (String appKey, Integer lastUpdate) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
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
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - getFeeds","Error", e);
				error = true;
			}
			
			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					
					if (!GenericHelper.isNumeric(respStr)) {
						feeds = JsonHelper.getFeeds(respStr);
					} else {
						error = true;
						errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - getFeeds", "Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("feeds", feeds);
		result.put("error", error);
		result.put("errorMessage", errorMessage);
    	
		return result;
	}
	
	public Map<String, Object>  getItems(String appKey, JSONArray viewedItems, Boolean isDownload) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
		Item[] items = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_ITEMS_UNREAD);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();

		try {
			jsonData.put("appKey", appKey);
			jsonData.put("viewedItems", viewedItems);
			jsonData.put("isDownload", isDownload);
		} catch (JSONException e) {
			Log.e("ApiHelper - getItems","Error", e);
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - getItems","Error", e);
				error = true;
			}
			
			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());

					if (resp.getStatusLine().getStatusCode() == 200 && !GenericHelper.isNumeric(respStr)) {
						items = JsonHelper.getItems(respStr);
						error = false;
					} else {
						error = true;
                        errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - getItems","Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("items", items);
		result.put("error", error);
		result.put("errorMessage", errorMessage);
    	
		return result;
	}
	
	public static Map<String, Object> doLogin(String username, String password, String appKey) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
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
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - doLogin","Error", e);
				error = true;
			}
			
			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());

					if(!respStr.equals("100")) {
						error = true;
						errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - doLogin","Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("error", error);
		result.put("errorMessage", errorMessage);
		
		return result;
	}
	
	public static String doSignUp(String username, String email, String password, String appKey) {
		String check = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SIGN_UP);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("username", username);
			jsonData.put("email", email);
			jsonData.put("password", password);
			jsonData.put("appKey", appKey);
		} catch (JSONException e) {
			Log.e("ApiHelper - doSignUp","Error", e);
			check = "99";
		}
		
		if (check != "99") {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - doSignUp","Error", e);
				check = "99";
			}
			
			if (check != "99") {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					check = respStr;
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - doSignUp","Error", e);
		    		check = "99";
		    	}
			}
		}
		
		return check;
	}
	
	public static Item[] addFeed(String appKey, String feedUrl) {
		Item[] items = null;
		String check = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_ADD_FEED);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();
		
		try {
			jsonData.put("feed_url", feedUrl);
			jsonData.put("appKey", appKey);
		} catch (JSONException e) {
			Log.e("ApiHelper - addFeed","Error", e);
			check = "99";
		}
		
		if (check != "99") {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - addFeed","Error", e);
				check = "99";
			}
			
			if (check != "99") {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					if (!GenericHelper.isNumeric(respStr)) {
						items = JsonHelper.getItems(respStr);
					} else {
						check = "99";
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - addFeed","Error", e);
		    		check = "99";
		    	}
			}
		}
		
		return items;
	}
	
	public Map<String, Object> syncLabels(String appKey, JSONArray changedLabels, Integer lastUpdate) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
		Label[] labels = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_LABELS);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();

		try {
			jsonData.put("appKey", appKey);
			jsonData.put("changedLabels", changedLabels);
			jsonData.put("lastUpdate", lastUpdate);
		} catch (JSONException e) {
			Log.e("ApiHelper - syncLabels","Error", e);
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - syncLabels","Error", e);
				error = true;
			}
			
			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					
					if (resp.getStatusLine().getStatusCode() == 200 && !GenericHelper.isNumeric(respStr)) {
						labels = JsonHelper.getLabels(respStr);
						error = false;
					} else {
						error = true;
						errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - syncLabels","Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("labels", labels);
		result.put("error", error);
		result.put("errorMessage", errorMessage);
    	
		return result;
	}
	
	public Map<String, Object> syncLaterItems(String appKey, JSONArray laterItems) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_LATER_ITEMS);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();

		try {
			jsonData.put("appKey", appKey);
			jsonData.put("laterItems", laterItems);
		} catch (JSONException e) {
			Log.e("ApiHelper - syncLaterItems","Error", e);
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - syncLaterItems","Error", e);
				error = true;
			}
			
			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());

					if (resp.getStatusLine().getStatusCode() == 200 && respStr.equals("100")) {
						error = false;
					} else {
						error = true;
						errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - syncLaterItems","Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("error", error);
		result.put("errorMessage", errorMessage);
    	
		return result;
	}
	
	public Map<String, Object> syncSharedItems(String appKey, JSONArray sharedItems) {
		Map<String, Object> result = new HashMap<String, Object>();
		Boolean error = false;
		String errorMessage = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SYNC_SHARED_ITEMS);
		StringEntity jsonEntity;
		String jsonString;
		JSONObject jsonData = new JSONObject();

		try {
			jsonData.put("appKey", appKey);
			jsonData.put("sharedItems", sharedItems);
		} catch (JSONException e) {
			Log.e("ApiHelper - syncSharedItems","Error", e);
			error = true;
		}
		
		if (!error) {
			try {
				jsonString = jsonData.toString();
				jsonEntity = new StringEntity(jsonString);
				post.setEntity(jsonEntity);
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				Log.e("ApiHelper - syncLaterItems","Error", e);
				error = true;
			}

			if (!error) {
				try {
					HttpResponse resp = httpClient.execute(post);
					String respStr = EntityUtils.toString(resp.getEntity());
					
					if (resp.getStatusLine().getStatusCode() == 200 && respStr.equals("100")) {
						error = false;
					} else {
						error = true;
						errorMessage = respStr;
					}
		    	} catch(Exception e) {
		    		Log.e("ApiHelper - syncSharedItems","Error", e);
		    		error = true;
		    	}
			}
		}
		result.put("error", error);
		result.put("errorMessage", errorMessage);
    	
		return result;
	}
}