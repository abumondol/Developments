package edu.virginia.cs.mondol.phonevoiceapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.virginia.cs.mondol.phonevoiceapp.utils.DateTimeUtil;
import edu.virginia.cs.mondol.phonevoiceapp.utils.MC;

public class VocalWatchUtils {

    public static JSONObject checkJSONString(String str) {

        String pb = "Could not convert to JSON Object";
        JSONObject js, js2;
        int i = -1, len, x = -1;

        try {
            js = new JSONObject(str);
            pb = js.getString(MC.CONFIG_CODE);
            x = js.getInt(MC.TIME_TYPE);

            JSONArray ja = js.getJSONArray(MC.EVENTS);
            len = ja.length();

            for (i = 0; i < len; i++) {
                js2 = ja.getJSONObject(i);
                pb = js2.getString(MC.TIME);
                pb = js2.getString(MC.QUERY_CODE);
                pb = js2.getString(MC.DISPLAY_TEXT);
                pb = js2.getString(MC.QUERY);
                pb = js2.getString(MC.QUERY_TYPE);
                pb = js2.getString(MC.REQUERY_INTERVAL);
                pb = js2.getString(MC.DETAILS);
            }

        } catch (Exception ex) {
            Log.i(MC.TTS, ex.toString());
            Log.i(MC.TTS, "Check JSON String: " + i + ", " + x + ", " + pb);
            return null;
        }

        return js;
    }


    public static NextEvent getNextEvent(long delay) throws Exception {
        NextEvent nextEvent = new NextEvent(0, System.currentTimeMillis()+delay, delay);
        return nextEvent;
    }

    public static NextEvent getNextEvent(JSONObject jsonConfig) throws Exception {

        JSONArray eventsArray = jsonConfig.getJSONArray(MC.EVENTS);
        int len = eventsArray.length();
        if (len == 0)
            return null;

        int nextEventIndex = 0;
        long currentTime = System.currentTimeMillis();
        long dayStartTime = DateTimeUtil.dayStartTimeMillis(currentTime);
        long nextAbsoluteTimeSelected = dayStartTime + eventsArray.getJSONObject(nextEventIndex).getLong(MC.TIME_MILLIS);
        if (nextAbsoluteTimeSelected < currentTime)
            nextAbsoluteTimeSelected += MC.millisInOneday;

        long t;
        for (int i = 1; i < len; i++) {
            t = dayStartTime + eventsArray.getJSONObject(i).getLong(MC.TIME_MILLIS);
            if (t < currentTime)
                t += MC.millisInOneday;

            if (t < nextAbsoluteTimeSelected) {
                nextAbsoluteTimeSelected = t;
                nextEventIndex = i;
            }
        }

        NextEvent nextEvent = new NextEvent(nextEventIndex, nextAbsoluteTimeSelected, nextAbsoluteTimeSelected - currentTime);
        return nextEvent;
    }


    public static JSONObject processEventTimes(JSONObject jsonConfig) throws Exception {
        JSONArray eventsArray = jsonConfig.getJSONArray(MC.EVENTS);
        int len = eventsArray.length();
        if (len == 0)
            return null;

        long offsetFromDayStart = 0, t;
        int time_type = jsonConfig.getInt(MC.TIME_TYPE);// time_type,  0 : relative to start system, 1: absolute time in the day
        if (time_type == 0)
            offsetFromDayStart = DateTimeUtil.timeSinceStartOfDay(System.currentTimeMillis());

        for (int i = 0; i < len; i++) {
            t = DateTimeUtil.hmsStringToMillis(eventsArray.getJSONObject(i).getString(MC.TIME));
            eventsArray.getJSONObject(i).put(MC.TIME_MILLIS, t + offsetFromDayStart);
            eventsArray.getJSONObject(i).put(MC.REPEAT_COUNT, 0);
        }

        return jsonConfig;
    }


}
