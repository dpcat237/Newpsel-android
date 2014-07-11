package com.dpcat237.nps.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NumbersHelper {
    public static String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-MM kk:mm:ss");
            Date netDate = (new Date(timeStamp));

            return sdf.format(netDate);
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
}
