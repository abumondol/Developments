package edu.virginia.cs.mooncake.airsignapp;

/**
 * Created by TOSHIBA on 3/20/2016.
 */
public class Distance {

    public static double[] distances(double[][] sign1, double[][] sign2) {
        int axis_count = sign1.length;
        double[] distances = new double[axis_count];

        for (int i = 0; i < axis_count; i++) {
            distances[i] = DTW(sign1[i], sign2[i]);
        }
        return distances;
    }

    public static double DTW(double[] s1, double[] s2) {
        int len1 = s1.length, len2 = s2.length;
        //System.out.println("Length: " + s1.length + "  " + s2.length);
        int i, j, k;

        double[][] d = new double[len1][len2];
        double[][] D = new double[len1][len2];

        for (i = 0; i < len1; i++) {
            for (j = 0; j < len2; j++) {
                d[i][j] = Math.abs(s1[i] - s2[j]);
            }
        }

        D[0][0] = d[0][0];

        for (i = 1; i < len1; i++) {
            D[i][0] = d[i][0] + D[i - 1][0];
        }

        for (j = 1; j < len2; j++) {
            D[0][j] = d[0][j] + D[0][j - 1];
        }

        for (i = 1; i < len1; i++) {
            for (j = 1; j < len2; j++) {
                D[i][j] = d[i][j] + min(D[i - 1][j], D[i][j - 1], D[i - 1][j - 1]);
            }
        }

        return D[len1 - 1][len2 - 1];

    }

    public static double min(double a, double b, double c) {
        double m = a;
        if (b < m)
            m = b;
        if (c < m)
            m = c;
        return m;
    }
}