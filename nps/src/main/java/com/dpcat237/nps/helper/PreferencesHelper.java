package com.dpcat237.nps.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;

import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class PreferencesHelper {
	public static String generateKey(Context context) {
	    final int outputKeyLength = 256;
	    String result = ""; 
	    
	    @SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
	    Boolean registered = userPref.getBoolean("registered", false);
	    
	    if (registered) {
	    	result = userPref.getString("appKey", "");
	    } else {
	    	try {
	    		SecureRandom secureRandom = new SecureRandom();
			    // Do *not* seed secureRandom! Automatically seeded from system entropy.
			    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			    keyGenerator.init(outputKeyLength, secureRandom);
			    SecretKey key = keyGenerator.generateKey();

			    SharedPreferences.Editor editor = userPref.edit();
			    editor.putString("appKey", key.getEncoded().toString());
				editor.putBoolean("registered", true);
				editor.commit();
				
				result = userPref.getString("appKey", "");
	    	} catch(Exception e) {
	    		Log.e("GenericHelper - generateKey","Error", e);
	    	}
	    }

		return result;
	}
	
	public static Integer getLastFeedsUpdate(Context context) {
		Integer result = 0; 
		
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
	    Integer feedsUpdate = userPref.getInt("feedsUpdate", 0);
	    
	    if (feedsUpdate != null) {
	    	result = feedsUpdate;
	    }
		
		return result;
	}
	
	public static void setLastFeedsUpdate(Context context, Integer feedsUpdate) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPref.edit();
	    editor.putInt("feedsUpdate", feedsUpdate);
		editor.commit();
	}
	
	public static Integer getLastLabelsUpdate(Context context) {
		Integer result = 0; 
		
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
	    Integer labelsUpdate = userPref.getInt("labelsUpdate", 0);
	    
	    if (labelsUpdate != null) {
	    	result = labelsUpdate;
	    }
		
		return result;
	}
	
	public static void setLastLabelsUpdate(Context context, Integer labelsUpdate) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPref.edit();
	    editor.putInt("labelsUpdate", labelsUpdate);
		editor.commit();
	}
	
	public static boolean isNumeric(String str)
	{  
	  try  
	  {  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public static Integer getFeedsList(Context context) {
		 @SuppressWarnings("static-access")
		 SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		 Integer list = userPref.getInt("feeds_list", 0);
		 
		 return list;
	}
	
	public static void setFeedsList(Context context, Integer list) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPref.edit();
	    editor.putInt("feeds_list", list);
		editor.commit();
	}
	
	public static Integer getSelectedFeed(Context context) {
		 @SuppressWarnings("static-access")
		 SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		 Integer feedId = userPref.getInt("selected_feed", 0);
		 
		 return feedId;
	}
	
	public static void setSelectedFeed(Context context, Integer feedId) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPref.edit();
	    editor.putInt("selected_feed", feedId);
		editor.commit();
	}
	
	public static Boolean checkLastClearCache(Context context) {
		 Boolean check = false;
		 @SuppressWarnings("static-access")
		 SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		 Long lastClearDate = userPref.getLong("last_clear_cache", 0);
		 Date current = new Date();
		 
		 if (lastClearDate != 0) {
			 Long week = (long) (7 * 24 * 60 * 60 * 1000);
			 
			 if (current.getTime() > (lastClearDate + week)) {
				 check = true;
			 }
		 } else {
			 SharedPreferences.Editor editor = userPref.edit();
			 editor.putLong("last_clear_cache", current.getTime());
			 editor.commit();
		 }
		 
		 return check;
	}

    public static Boolean isPlayerActive(Context context) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        Boolean active = userPref.getBoolean("player_active", false);

        return active;
    }

    public static void setPlayerActive(Context context, Boolean active) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean("player_active", active);
        editor.commit();
    }

    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public static void setNewDictationItems(Context context, Boolean areNew) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean("sync_are_new_dictate_items", areNew);
        editor.commit();
    }

    public static Boolean areNewDictationItems(Context context) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        Boolean areNew = userPref.getBoolean("sync_are_new_dictate_items", false);

        return areNew;
    }

    public static void setNewItems(Context context, Boolean areNew) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean("sync_are_new_items", areNew);
        editor.commit();
    }

    public static Boolean areNewItems(Context context) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        Boolean areNew = userPref.getBoolean("sync_are_new_items", false);

        return areNew;
    }
}