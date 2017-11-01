package m2fedutils;

import constants_config.EatConfig;

public class DataUtils {

    public static void process_sensor_time(long[] time, long diff) {
        long t = time[0];
        int total = time.length;

        for (int i = 0; i < total; i++) {
            time[i] = time[i] + diff;
        }
    }

    public static float[][] smooth_data(float[][] data) {
        data[0] = smooth_data(data[0]);
        data[1] = smooth_data(data[1]);
        data[2] = smooth_data(data[2]);
        return data;
    }

    public static float[] smooth_data(float[] data) {
        for (int i = 1; i < data.length; i++) {
            data[i] = data[i - 1] * EatConfig.smooth_factor + data[i] * (1 - EatConfig.smooth_factor);
        }
        return data;
    }

    public static float[][] normalize_data(float[][] data) {
        float[][] d = new float[data.length][data[0].length];
        float mag;
        for (int i = 0; i < data[0].length; i++) {
            mag = (float) Math.sqrt(data[0][i] * data[0][i] + data[1][i] * data[1][i] + data[2][i] * data[2][i]);
            d[0][i] = data[0][i] / mag;
            d[1][i] = data[1][i] / mag;
            d[2][i] = data[2][i] / mag;
            mag = d[0][i] * d[0][i] + d[1][i] * d[1][i] + d[2][i] * d[2][i];
            if (Math.abs(mag - 1) > 0.01) {
                System.out.println("Error normalization: " + mag);
                System.exit(0);
            }
        }

        return d;
    }

}
