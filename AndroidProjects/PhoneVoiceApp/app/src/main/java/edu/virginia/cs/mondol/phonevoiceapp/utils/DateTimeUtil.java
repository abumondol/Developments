package edu.virginia.cs.mondol.phonevoiceapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtil {

    public static long hmsToMillis(int hour, int min, int sec) {
        return (hour * 60 * 60 + min * 60 + sec) * 1000;
    }

    public static long hmsStringToMillis(String hms) throws Exception {
        String[] str = hms.split(":");
        int h = Integer.parseInt(str[0]);
        int m = 0;
        int s = 0;

        if (str.length >= 2)
            m = Integer.parseInt(str[1]);

        if (str.length >= 3)
            s = Integer.parseInt(str[2]);

        return (h * 60 * 60 + m * 60 + s) * 1000;
    }

    public static long dayStartTimeMillis(long time) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        Calendar cal2 = new GregorianCalendar(y, m, d);
        return cal2.getTimeInMillis();
    }

    public static long timeSinceStartOfDay(long time) {
        return time - dayStartTimeMillis(time);
    }


    public static String dateTimeString(long timeInMilis) {
        Date date = new Date(timeInMilis);
        String formatStr = "yyyy-MM-dd-HH-mm-ss";
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static String millisToHmsString(long millis) {
        long s = millis / 1000;
        long m = s / 60;
        s = s % 60;
        long h = m / 60;
        m = m % 60;
        return h + "h " + m + "m " + s + " s";
    }

    public static String millisToTimeString(long millis) {
        long s = millis / 1000;
        long m = s / 60;
        s = s % 60;
        long h = m / 60;
        m = m % 60;


        String suffix = " am";

        if (h >= 12) {
            suffix = " pm";
            if (h > 12) {
                h = h - 12;
            }
        }
        String str;
        if (h < 10)
            str = "0" + h + ":";
        else
            str = h + ":";

        if (m < 10)
            str += "0" + m;
        else
            str += m;

        str += suffix;


        return str;
    }


}
