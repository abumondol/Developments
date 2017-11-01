package m2fedutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//@author mm5gg
public class DateTimeUtils {

    public static String currentDateInString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String currentDateTimeInString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTimeString(long timeInMilis, int flag) {
        Date date = new Date(timeInMilis);
        String formatStr = "yyyy-MM-dd";

        if (flag == 1) {
            formatStr += " hh:mm aa";
        } else if (flag == 2) {
            formatStr += " hh:mm:ss aa";
        } else if (flag == 3) {
            formatStr += " HH-mm-ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static String getDateString(long timeInMilis) {
        Date date = new Date(timeInMilis);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

}
