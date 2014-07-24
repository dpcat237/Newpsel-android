package com.dpcat237.nps.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dpcat237.nps.database.repository.FeedRepository;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginHelper {
	public static Boolean checkLogged(Context context) {
		 @SuppressWarnings("static-access")
		 SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		 Boolean logged = userPref.getBoolean("logged", false);

		 return logged;
	}

	public static void doLogin(Context context) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPref.edit();
		editor.putBoolean("logged", true);
		editor.commit();

        ReceiverHelper.enableBootReceiver(context);
	}

	public static void doLogout(Context context) {
		@SuppressWarnings("static-access")
		SharedPreferences userPref = context.getSharedPreferences("UserPreference", context.MODE_PRIVATE);
		SharedPreferences.Editor userEditor = userPref.edit();
        //clear user preferences
        userEditor.clear();
        userEditor.commit();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = pref.edit();
        //clear generic preferences
        prefEditor.clear();
        prefEditor.commit();

        //drop data base
        FeedRepository feedRepository = new FeedRepository(context);
        feedRepository.open();
        feedRepository.drop();

        //delete folders with user files
        FileHelper.deleteFolders(FileHelper.getVoicesFolder(context));

        //disable services
        ReceiverHelper.disableBootReceiver(context);

        /**
         * from API 19 only with:
         * ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
         * Boolean result = activityManager.clearApplicationUserData();
         */
	}
	 
	public static String sha1LoginPassword(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String pwd = sha1("sc_"+text);
		String appPwd = sha1("checkPwd_"+pwd);
		return appPwd;
	}
	
	public static String sha1SignUpPassword(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String appPwd = sha1("sc_"+text);
		return appPwd;
	}

	public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

	private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
