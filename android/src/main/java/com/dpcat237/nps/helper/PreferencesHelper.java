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

    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public static void setBooleanPreference(Context context, String key, Boolean value) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBooleanPreference(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);

        return userPref.getBoolean(key, true);
    }

    public static void setIntPreference(Context context, String key, Integer value) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static Integer getIntPreference(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);

        return userPref.getInt(key, 0);
    }
}
