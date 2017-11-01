package edu.virginia.cs.mondol.fed.bites;

import android.util.Log;

import edu.virginia.cs.mondol.fed.utils.FedConstants;
import edu.virginia.cs.mondol.fed.utils.MathUtils;

/**
 * Created by Abu on 7/6/2017.
 */

public class PatternChecker {
    float[][][][] patterns;
    int patternCount, max_len_left, max_len_right, max_len;
    float[][] data_left, data_right;
    float dist_left, dist_right;

    float[][] d;
    float[][] D;

    public PatternChecker(float[][][][] pat) {
        patterns = pat;
        patternCount = patterns.length;
        max_len_left = (int) patterns[0][0][0][1];
        max_len_right = (int) patterns[0][0][0][2];
        max_len = max_len_left >= max_len_right ? max_len_left : max_len_right;

        data_left = new float[3][max_len_left];
        data_right = new float[3][max_len_right];

        d = new float[max_len][max_len];
        D = d;
        Log.i(FedConstants.MYTAG,"Pattern: "+patternCount+","+max_len_left+","+max_len_right);
    }

    boolean checkPattern(Bite b, float[][] data) {
        MathUtils.normalize_data_reverse(data, data_left, b.index - max_len_left + 1, b.index);
        MathUtils.normalize_data(data, data_right, b.index, b.index + max_len_right - 1);

        for (int i = 1; i < patternCount; i++) {
            dist_left = distance(patterns[i][0], data_left);
            dist_right = distance(patterns[i][1], data_right);
            if (dist_left + dist_right < patterns[i][2][0][1]) {
                b.patternIndex = i;
                return true;
            }
        }

        return false;
    }


    public float distance(float[][] t, float[][] r) {
        int N = t[0].length;
        int M = N;

        int n, m;
        for (n = 0; n < N; n++)
            for (m = 0; m < M; m++)
                d[n][m] = 1 - (t[0][n] * r[0][m] + t[1][n] * r[1][m] + t[2][n] * r[2][m]);


        D[0][0] = d[0][0];

        for (n = 1; n < N; n++) {
            D[n][0] = d[n][0] + D[n - 1][0];
        }

        for (m = 1; m < M; m++) {
            D[0][m] = d[0][m] + D[0][m - 1];
        }

        for (n = 1; n < N; n++) {
            for (m = 1; m < M; m++) {
                D[n][m] = d[n][m] + min(D[n - 1][m], D[n][m - 1], D[n - 1][m - 1]);
            }
        }

        return D[N - 1][M - 1];
    }

    public static float min(float a, float b, float c) {
        if (a <= b && a <= c) {
            return a;
        }
        if (b <= a && b <= c) {
            return b;
        }
        return c;
    }

}
