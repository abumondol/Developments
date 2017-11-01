package edu.virginia.cs.mondol.fed.utils;

/**
 * Created by Abu on 5/19/2017.
 */

public class MathUtils {

    public static float var(float[][] data, int start_index, int end_index) {
        float v = 0;
        for (int i = 0; i < data.length; i++)
            v += var(data[i], start_index, end_index);

        return v;
    }

    public static float var(float[] data, int start_index, int end_index) {
        float sumX = 0, v = 0, sumX2 = 0;
        int i, N = end_index - start_index + 1;
        for (i = start_index; i <= end_index; i++) {
            sumX += data[i];
            sumX2 += data[i] * data[i];
        }

        float mean = sumX / N;
        v = sumX2 / N - mean * mean;

        return v;
    }

    public static void smooth_data(float[][] data, float lastX, float lastY, float lastZ, float smooth_factor) {
        smooth_data(data[0], lastX, smooth_factor);
        smooth_data(data[1], lastY, smooth_factor);
        smooth_data(data[2], lastZ, smooth_factor);
    }

    public static void smooth_data(float[] data, float lastVal, float smooth_factor) {
        data[0] = lastVal * smooth_factor + data[0] * (1 - smooth_factor);
        for (int i = 1; i < data.length; i++) {
            data[i] = data[i - 1] * smooth_factor + data[i] * (1 - smooth_factor);
        }
    }

    public static void normalize_data(float[][] src, float[][] dest, int si, int ei) {
        float mag;
        for (int i = 0, j=si; j <=ei; i++, j++) {
            mag = (float) Math.sqrt(src[0][j] * src[0][j] + src[1][i] * src[1][j] + src[2][j] * src[2][j]);
            dest[0][i] = src[0][j] / mag;
            dest[1][i] = src[1][j] / mag;
            dest[2][i] = src[2][j] / mag;
        }
    }

    public static void normalize_data_reverse(float[][] src, float[][] dest, int si, int ei) {
        float mag;
        for (int i = 0, j=ei; j >=si; i++, j--) {
            mag = (float) Math.sqrt(src[0][j] * src[0][j] + src[1][i] * src[1][j] + src[2][j] * src[2][j]);
            dest[0][i] = src[0][j] / mag;
            dest[1][i] = src[1][j] / mag;
            dest[2][i] = src[2][j] / mag;
        }
    }

}
