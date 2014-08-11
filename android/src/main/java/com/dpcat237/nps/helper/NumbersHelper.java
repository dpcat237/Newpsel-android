package com.dpcat237.nps.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NumbersHelper {
    public static String getDate(Integer timeStamp) {
        try {
            Date date = new Date(timeStamp*1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            return dateFormat.format(date);
        } catch(Exception ex) {
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static Integer getCurrentTimestamp() {
        Date date= new java.util.Date();
        Long tmp = date.getTime();

        return Integer.parseInt(tmp.toString().substring(0, 10));
    }
}
