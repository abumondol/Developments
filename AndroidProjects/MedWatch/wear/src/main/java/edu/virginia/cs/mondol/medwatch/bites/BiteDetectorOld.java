package edu.virginia.cs.mondol.medwatch.bites;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.virginia.cs.mondol.medwatch.utils.FedConstants;
import edu.virginia.cs.mondol.medwatch.utils.MathUtils;
import edu.virginia.cs.mondol.medwatch.utils.SharedPrefUtil;

/**
 * Created by Abu on 3/12/2017.
 */

public class BiteDetectorOld {

    float[][][][] patterns;
    float[][][] data;
    float[][] comboData;
    long[][] time;
    Bite[][] minPointBiteList;
    Bite[] tempMinPointBiteList;
    ArrayList<Bite> biteList;

    int arrayLen, minWindowCount;
    float minXTh = (float) (-3.5), varTh = (float) 0.3;
    int window_size_for_minx = 16;
    long currentTime, lastSentTime = 0;

    long meal_window_size = 1 * 60 * 1000; //minute*second*millisecond
    int bite_count_for_meal_start = 1, meal_status = 0;
    int window_size_left = 48, window_size_right = 32;
    float smooth_factor = (float)0.9;
    Context context;
    int episodeCount = 0;


    public BiteDetectorOld(int arrLen, Context ctx, float[][][][] pats) {
        context = ctx;
        arrayLen = arrLen;
        patterns = pats;

        minWindowCount = arrayLen / window_size_for_minx;
        minPointBiteList = new Bite[3][minWindowCount];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < minWindowCount; j++) {
                minPointBiteList[i][j] = new Bite();
            }
        }

        data = new float[3][][];
        comboData = new float[3][3 * arrayLen];
        time = new long[3][];
        biteList = new ArrayList<>();
    }

    public boolean addData(float[][] arr, long[] t) {
        if(episodeCount<12){
            episodeCount++;
            return false;
        }

        boolean sendDataFlag = false;
        if (data[1] == null) {
            if (data[0] == null) {
                data[0] = arr;
                time[0] = t;
                MathUtils.smooth_data(data[0], 0, 0, 0, smooth_factor);
                findMinPointBitesAllWindows(0);
            } else {
                data[1] = arr;
                time[1] = t;
                MathUtils.smooth_data(data[1], data[0][0][arrayLen-1], data[0][1][arrayLen-1], data[0][2][arrayLen-1], smooth_factor);
                findMinPointBitesAllWindows(1);
            }

            return false;
        }

        data[2] = arr;
        time[2] = t;
        MathUtils.smooth_data(data[2], data[1][0][arrayLen-1], data[1][1][arrayLen-1], data[1][2][arrayLen-1], smooth_factor);
        currentTime = t[arrayLen - 1];

        // Flattening three data array to one combo data
        int i, j, k = 0;
        for (i = 0; i < 3; i++) {
            System.arraycopy(data[i][0], 0, comboData[0], arrayLen * i, arrayLen);
            System.arraycopy(data[i][1], 0, comboData[1], arrayLen * i, arrayLen);
            System.arraycopy(data[i][2], 0, comboData[2], arrayLen * i, arrayLen);

            /*for(j=0;j<arrayLen;j++){
                comboData[0][k] = data[i][0][j];
                comboData[1][k] = data[i][1][j];
                comboData[2][k] = data[i][2][j];
                k++;
            }*/
        }


        findMinPointBitesAllWindows(2);
        addToBiteList();
        sendDataFlag = mealStatus();

        tempMinPointBiteList = minPointBiteList[0];
        data[0] = data[1];
        time[0] = time[1];
        minPointBiteList[0] = minPointBiteList[1];

        data[1] = data[2];
        time[1] = time[2];
        minPointBiteList[1] = minPointBiteList[2];

        minPointBiteList[2] = tempMinPointBiteList;

        return sendDataFlag;
    }


    void findMinPointBitesAllWindows(int array_index) {
        float[] x = data[array_index][0];
        int i, j, k, ix;
        String minValsStr = "MinXVals: ";

        for (i = 0, k = 0; i < x.length; i += window_size_for_minx, k++) {
            ix = i;
            for (j = i; j < i + window_size_for_minx; j++) {
                if (x[j] < x[ix]) {
                    ix = j;
                }
            }

            minPointBiteList[array_index][k].index = ix;
            minPointBiteList[array_index][k].time = time[array_index][ix];
            minPointBiteList[array_index][k].status = 0;
            minPointBiteList[array_index][k].minXVal = x[ix];
            minValsStr += "("+ix+", " + x[ix]+"), ";

        }

        Log.i(FedConstants.MYTAG, minValsStr);

    }

    void addToBiteList() {
        Bite b;
        float var;
        //String minValsStr = "MinXVals: ";

        b = minPointBiteList[1][0];
        if (b.minXVal <= minXTh && b.minXVal < minPointBiteList[0][minWindowCount - 1].minXVal && b.minXVal <= minPointBiteList[1][1].minXVal) {
            var = MathUtils.var(comboData, arrayLen + b.index - window_size_left, arrayLen + b.index + window_size_right);
            Log.i(FedConstants.MYTAG, String.format("Index: %d, MinX:%.4f, %.4f, Variance:%.4f", b.index, b.minXVal, comboData[0][arrayLen + b.index], var));
            if (var > varTh)
                biteList.add(minPointBiteList[1][0].getClone());
        }
        //minValsStr += "("+b.index+", " + b.minXVal+"), ";


        int i;
        for (i = 1; i < minWindowCount - 1; i++) {
            b = minPointBiteList[1][i];
            if (b.minXVal <= minXTh && b.minXVal < minPointBiteList[1][i - 1].minXVal && b.minXVal <= minPointBiteList[1][i + 1].minXVal) {
                var = MathUtils.var(comboData, arrayLen + b.index - window_size_left, arrayLen + b.index + window_size_right);
                Log.i(FedConstants.MYTAG, String.format("Index: %d, MinX:%.4f, %.4f, Variance:%.4f", b.index, b.minXVal, comboData[0][arrayLen + b.index], var));
                if (var > varTh)
                    biteList.add(minPointBiteList[1][i].getClone());
            }
            //minValsStr += "("+b.index+", " + b.minXVal+"), ";
        }

        i = minWindowCount - 1;
        b = minPointBiteList[1][i];
        if (b.minXVal <= minXTh && b.minXVal < minPointBiteList[1][i - 1].minXVal && b.minXVal <= minPointBiteList[2][0].minXVal) {
            var = MathUtils.var(comboData, arrayLen + b.index - window_size_left, arrayLen + b.index + window_size_right);
            Log.i(FedConstants.MYTAG, String.format("Index: %d, MinX:%.4f, %4f, Variance:%.4f", b.index, b.minXVal, comboData[0][arrayLen + b.index], var));
            if (var > varTh)
                biteList.add(minPointBiteList[1][minWindowCount - 1].getClone());
        }
        //minValsStr += "("+b.index+", " + b.minXVal+"), ";

        //Log.i(FedConstants.MYTAG, minValsStr);

    }

    boolean mealStatus() {
        SharedPrefUtil.putSharedPrefInt(FedConstants.BITE_COUNT, biteList.size(), context);
        Log.i(FedConstants.MYTAG, "BiteList Size:" + biteList.size() + ", " + meal_window_size / 1000 + ", Times: " + lastSentTime / 1000 + ", " + currentTime / 1000);
        if (currentTime - lastSentTime < meal_window_size)
            return false;

        /*while (biteList.size() > 0 && currentTime - biteList.get(0).time >= meal_window_size) {
            Log.i(FedConstants.MYTAG, "Removing bite, new size:" + biteList.size() + ", " + biteList.get(0).time / 1000);
            biteList.remove(0);
        }*/

        if (biteList.size() >= bite_count_for_meal_start) {
            Log.i(FedConstants.MYTAG, "Meal Detected");
            lastSentTime = currentTime;
            biteList.clear();
            return true;
        } else
            return false;
    }


}
