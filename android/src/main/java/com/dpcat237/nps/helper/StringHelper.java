package com.dpcat237.nps.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    /**
     * Convert string from UTF8
     *
     * @param s String
     *
     * @return String
     */
    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    /**
     * Convert string to UTF8
     *
     * @param s String
     *
     * @return String
     */
    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    /**
     * Checking valid email id format.
     *
     * @param email String
     *
     * @return boolean
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Valid email and password from form
     *
     * @param context Context
     * @param view    View
     *
     * @return boolean
     */
    public static Boolean isUserDataValid(Context context, View view) {
        Boolean check = isEmailValid(context, view);
        EditText password = (EditText) view.findViewById(R.id.txtPassword);
        if (password.getText().toString().matches("")) {
            password.setError(context.getString(R.string.error_password_empty));
            check = false;
        }

        return check;
    }

    /**
     * Valid email from form
     *
     * @param context Context
     * @param view    View
     *
     * @return boolean
     */
    public static Boolean isEmailValid(Context context, View view) {
        Boolean check = true;
        EditText email = (EditText) view.findViewById(R.id.txtEmail);

        if (!StringHelper.isEmailValid(email.getText().toString())) {
            email.setError(context.getString(R.string.error_email));
            check = false;
        }

        return check;
    }

    /**
     * Encode password
     *
     * @param string String
     *
     * @return String
     */
    public static String getPassword(String string) {
        String password = "";
        try {
            password = LoginHelper.sha1LoginPassword(string);
        } catch (NoSuchAlgorithmException e) {
            Log.d("LoginTask - getData", "Error", e);
        } catch (UnsupportedEncodingException e) {
            Log.d("LoginTask - getData","Error", e);
        }

        return password;
    }
}
