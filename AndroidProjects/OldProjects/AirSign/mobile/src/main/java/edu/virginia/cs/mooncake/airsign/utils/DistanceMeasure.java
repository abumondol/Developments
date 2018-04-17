package edu.virginia.cs.mooncake.airsign.utils;

import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsign.myclasses.SensorSample;

public class DistanceMeasure {

    public static float[][] DTW(float[] a, float[] b) {
        int n = a.length, m = b.length;

        float[][] d = new float[n + 1][m + 1];
        int i, j;
        float dist;

        for (i = 0; i <= n; i++)
            d[i][0] = 0;
        for (j = 1; j <= m; j++)
            d[0][j] = 0;

        for (i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {
                dist = Math.abs(a[i - 1] - b[j - 1]);
                d[i][j] = dist
                        + Math.min(d[i - 1][j - 1],
                        Math.min(d[i][j - 1], d[i - 1][j]));
                // System.out.println(i + "," + j + "  => " + d[i][j]);
            }
        }

        return d;
    }// end of DTW function

    public static float DTW_distance(float[] a, float[] b) {
        float[][] dtw = DTW(a, b);
        return dtw[a.length][b.length];
    }

    public static float DTW_dps(ArrayList<SensorSample> s1, ArrayList<SensorSample> s2) {
        float[][] d1, d2;
        float[] m1, m2;

        d1 = listToArray(s1);
        d2 = listToArray(s2);

        m1 = magnitude(d1);
        m2 = magnitude(d2);
        float dist= DTW_distance(m1, m2);
        int l = (m1.length+m2.length)/50;
        return 2*dist/l;

    }

    public static float[][] listToArray(ArrayList<SensorSample> list) {
        int i, s = 0;
        for (i = 0; i < list.size(); i++) {
            if (list.get(i).sensorType == 1) s++;
        }

        float[][] data= new float[3][s];

        for(i=0;i<s;i++){
            data[0][i] = list.get(i).values[0];
            data[1][i] = list.get(i).values[1];
            data[2][i] = list.get(i).values[2];
        }

        return data;
    }

    public  static float[] magnitude(float[][] d){
        float[] mag = new float[d[0].length];
        for(int i=0;i<mag.length;i++){
            mag[i]= (float)Math.sqrt(d[0][i]*d[0][i]+d[1][i]*d[1][i]+d[2][i]*d[2][i]);
        }

        return mag;
    }

}
