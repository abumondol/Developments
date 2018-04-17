package m2fedutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//@author mm5gg

public class DateTimeUtils {
    
    public static String currentDateTimeInString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
}
