package edu.virginia.cs.mondol.fed.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Abu on 2/4/2017.
 */

    public class DateTimeUtils {

    public static String getDateTimeString(long timeInMilis) {
        Date date = new Date(timeInMilis);
        String formatStr = "yyyy-MM-dd HH-mm-ss";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }


}
