package edu.virginia.cs.mondol.fed.utils;

import android.content.Context;
import android.os.Build;

/**
 * Created by Abu on 2/6/2017.
 */

public class FEDUtils {

    public static String getFileName(Context context, long startTime, String type, long serviceStartTime, int fileIndex) {

        return type + "-" + Build.SERIAL + "-" +
                SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_SERVER, context) + "-" +
                SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context) + "-" +
                serviceStartTime + "-" + startTime + "-" + fileIndex;
    }


    public static void saveSyncTimeInfo(Context context, String str) {
        String[] s = str.split(":");
        long server_time = Long.parseLong(s[1]);
        long call_time = Long.parseLong(s[2]);
        long response_time = Long.parseLong(s[3]);
        long watch_time = (call_time + response_time) / 2;
        long diff = server_time - watch_time;


        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_SERVER, server_time, context);
        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_SERVER_CALL, call_time, context);
        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_SERVER_RESPONSE, response_time, context);
        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_WATCH, watch_time, context);
        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_DIFF, diff, context);
        SharedPrefUtil.putSharedPrefLong(FedConstants.TIME_SYNC_RESPONSE_PERIOD, response_time - call_time, context);

    }

    public static String getTimeSyncInfo(Context context) {
        return "Watch: " + DateTimeUtils.getDateTimeString(SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_WATCH, context)) + "\n" +
                "Laptop: " + DateTimeUtils.getDateTimeString(SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_SERVER, context)) + "\n" +
                "Time Diff: " + SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_DIFF, context) / 1000 + "\n" +
                "Response Time: " + SharedPrefUtil.getSharedPrefLong(FedConstants.TIME_SYNC_RESPONSE_PERIOD, context) / 1000;
    }

}
