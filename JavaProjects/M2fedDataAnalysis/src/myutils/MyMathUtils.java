/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myutils;

/**
 *
 * @author mm5gg
 */
public class MyMathUtils {
    
    public static float min(float a, float b, float c) {
        if (a <= b && a <= c) {
            return a;
        }
        if (b <= a && b <= c) {
            return b;
        }
        return c;
    }
    
    public static float var(float[] data, int start_index, int end_index) {
        float sumX = 0, var = 0, sumX2 = 0;
        int i, N = end_index - start_index + 1;
        for (i = start_index; i <= end_index; i++) {
            sumX += data[i];
            sumX2 += data[i] * data[i];
        }

        float mean = sumX / N;
        var = sumX2 / N - mean * mean;

        return var;
    }

    
}
