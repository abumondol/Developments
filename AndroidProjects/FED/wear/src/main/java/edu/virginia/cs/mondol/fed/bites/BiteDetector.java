package edu.virginia.cs.mondol.fed.bites;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.virginia.cs.mondol.fed.utils.DateTimeUtils;
import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.MathUtils;
import edu.virginia.cs.mondol.fed.utils.SharedPrefUtil;

/**
 * Created by Abu on 3/12/2017.
 */

public class BiteDetector {

    float[][] data;
    long[] time;
    Bite[] minPointBiteList;
    ArrayList<Bite> biteList;
    Bite b, bp, bn;

    int arrayLen, minWindowCount;
    float minXTh = (float) (-2.5), varTh = (float) 0.3;
    int window_size_for_minx = 16;


    int window_size_left = 48, window_size_right = 32;
    float smooth_factor = (float) 0.9;
    Context context;
    int episodeCount = 0, totalDiscardEpisodeCount = 12;
    boolean discardData = true;
    int i, j, k, ix, axis;
    long initDiscardDuration = 4 * 1000, initTime, episode_duration, lastBiteDetectionTime;

    StringBuilder sb;

    long lastSentTime = 0, minSendInterval = 60 * 1000, lastDecisionTime = 0, minDecisionIntevalTime = 30 * 1000, mealWindowLength = 120 * 1000;
    int minBiteCount = 4;

    public BiteDetector(int arrLen, Context ctx) {
        context = ctx;
        arrayLen = arrLen;
        initTime = System.currentTimeMillis();

        minWindowCount = 3 * arrayLen / window_size_for_minx;
        minPointBiteList = new Bite[minWindowCount];

        for (int i = 0; i < minWindowCount; i++) {
            minPointBiteList[i] = new Bite();
        }

        data = new float[3][3 * arrayLen];
        time = new long[3 * arrayLen];
        biteList = new ArrayList<>();
        sb = new StringBuilder();

    }


    public boolean addData(float[][] arr, long[] t) {
        episode_duration = t[arrayLen - 1] - t[0];
        SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_EPISODE_DURATION, episode_duration, context);

        /*if (discardData) {
            long dd = System.currentTimeMillis() - initTime;
            if (episode_duration > initDiscardDuration)
                discardData = false;
            Log.i(FedConstants.MYTAG, "Discarding data, duration:" + dd / 1000 + ", " + episode_duration);
            SharedPrefUtil.putSharedPrefLong(FedConstants.DISCARD_DURATION, dd, context);
            return false;
        }*/


        for (axis = 0; axis < 3; axis++) {
            for (i = 0, j = arrayLen; i < 2 * arrayLen; i++, j++) {
                data[axis][i] = data[axis][j];
                time[i] = time[j];
            }

            for (i = 2 * arrayLen, j = 0; j < arrayLen; i++, j++) {
                data[axis][i] = data[axis][i - 1] * smooth_factor + arr[axis][j] * (1 - smooth_factor);
                time[i] = t[j];
            }
        }

        for (i = 0, j = minWindowCount / 3; i < 2 * minWindowCount / 3; i++, j++) {
            b = minPointBiteList[j];
            minPointBiteList[i].index = b.index - arrayLen;
            minPointBiteList[i].time = b.time;
            minPointBiteList[i].status = b.status;
            minPointBiteList[i].minXVal = b.minXVal;
        }

        for (i = 0, k = 2 * minWindowCount / 3; i < arrayLen; i += window_size_for_minx, k++) {
            ix = i;
            for (j = i; j < i + window_size_for_minx; j++)
                if (arr[0][j] < arr[0][ix])
                    ix = j;

            minPointBiteList[k].index = ix + 2 * arrayLen;
            minPointBiteList[k].time = time[ix];
            minPointBiteList[k].status = 0;
            minPointBiteList[k].minXVal = arr[0][ix];
        }

        sb.setLength(0);
        sb.append(DateTimeUtils.getDateTimeString(System.currentTimeMillis()));
        sb.append("\n");
        for (i = 0; i < minWindowCount; i++) {
            sb.append("(");
            sb.append(i);
            sb.append(",");
            sb.append(minPointBiteList[i].index);
            sb.append(",");
            sb.append(minPointBiteList[i].minXVal);
            sb.append("), ");
            if (i == 4 || i == 9)
                sb.append("\n");
        }

        Log.i(FedConstants.MYTAG, sb.toString());


        addToBiteList();
        return checkUpload();
    }

    void addToBiteList() {
        sb.setLength(0);
        sb.append("MinX Selected: ***** ");

        for (i = minWindowCount / 3; i < 2 * minWindowCount / 3; i++) {
            bp = minPointBiteList[i - 1];
            b = minPointBiteList[i];
            bn = minPointBiteList[i + 1];

            if (b.minXVal <= minXTh && b.minXVal < bp.minXVal && b.minXVal <= bn.minXVal) {
                sb.append("(");
                sb.append(i);
                sb.append(", ");
                sb.append(minPointBiteList[i].index);
                sb.append(", ");
                sb.append(minPointBiteList[i].minXVal);

                if (checkClassifier(b)) {
                    biteList.add(b.getClone());
                    Log.i(FedConstants.MYTAG, "Bite Detected, Index:"+i);
                }

                sb.append("), ");

            }
        }

        sb.append(" *****");
        Log.i(FedConstants.MYTAG, sb.toString());

    }

    boolean checkClassifier(Bite b) {
        float var = MathUtils.var(data, b.index - window_size_left, b.index + window_size_right);
        sb.append("," + var);
        if (var > varTh)
            return true;

        return false;
    }


    boolean checkUpload() {
        Long t = System.currentTimeMillis();

        int count = biteList.size();
        SharedPrefUtil.putSharedPrefInt(FedConstants.BITE_COUNT, count, context);


        String s = "Last Upload: " + DateTimeUtils.getDateTimeString(SharedPrefUtil.getSharedPrefLong(FedConstants.LAST_UPLOAD_ATTEMPT_TIME, context)) + ", Result:" +
                SharedPrefUtil.getSharedPref(FedConstants.LAST_UPLOAD_ATTEMPT_RESULT, context);
        Log.i(FedConstants.MYTAG, s);

        if (count == 0) {
            s = "Bite count: " + count + ", Last bite time: " +
                    DateTimeUtils.getDateTimeString(lastBiteDetectionTime).substring(11) + ", Current time: " +
                    DateTimeUtils.getDateTimeString(t).substring(11) + ", Last Decision Time:" +
                    DateTimeUtils.getDateTimeString(lastDecisionTime).substring(11);
            Log.i(FedConstants.MYTAG, s);
            return false;
        }

        lastBiteDetectionTime = biteList.get(count - 1).time;

        s = "Bite count: " + count + ", Bite times: " +
                DateTimeUtils.getDateTimeString(biteList.get(0).time).substring(11) + ", " + DateTimeUtils.getDateTimeString(lastBiteDetectionTime).substring(11) + ", Current Time:" +
                DateTimeUtils.getDateTimeString(t).substring(11)+ ", Last Decision Time:" +
                DateTimeUtils.getDateTimeString(lastDecisionTime).substring(11);

        Log.i(FedConstants.MYTAG, s);
        SharedPrefUtil.putSharedPrefLong(FedConstants.LAST_BITE_TIME, lastBiteDetectionTime, context);

        if (count >= minBiteCount) {
            biteList.clear();
            return true;
        }

        /*if (t - lastDecisionTime < minDecisionIntevalTime)
            return false;*/
        if (t - lastSentTime < minSendInterval) {
            return false;
        }

        lastDecisionTime = t;

        for (int i = count - 1; i >= 0; i--) {
            if (t - biteList.get(i).time > mealWindowLength) {
                biteList.remove(i);
            }
        }

        if (count >= minBiteCount) {
            biteList.clear();
            lastSentTime = t;
            return true;
        }

        return false;
    }

}
