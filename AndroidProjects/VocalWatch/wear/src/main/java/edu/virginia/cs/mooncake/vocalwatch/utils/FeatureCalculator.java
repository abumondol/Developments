package edu.virginia.cs.mooncake.vocalwatch.utils;

public class FeatureCalculator {

    public static double mean(float[][] data, int axis, int len) {
        double sum = 0;
        //int len = data.length;
        int i;
        for (i = 0; i < len; i++) {
            sum += data[i][axis];
        }

        return sum / len;
    }

    public static double std(float[][] data, int axis, int len) {
        double m = mean(data, axis, len);
        double sum = 0, temp;
        //int len = data.length;
        int i;
        for (i = 0; i < len; i++) {
            temp = data[i][axis] - m;
            sum += temp * temp;
        }

        if (len <= 1) {
            return Math.sqrt(sum);
        }

        return Math.sqrt(sum / (len - 1));
    }

    public static double rms(float[][] data, int axis, int len) {
        double sum = 0;
        //int len = data.length;
        int i;
        for (i = 0; i < len; i++) {
            sum += data[i][axis] * data[i][axis];
        }

        return Math.sqrt(sum / len);
    }

    public static double zcr(float[][] data, int axis, int len) {
        int sum = 0;
        //int len = data.length;
        int[] x = new int[len];

        int i;
        for (i = 0; i < len; i++) {
            if (data[i][axis] < 0) {
                x[i] = -1;
            } else if (data[i][axis] > 0) {
                x[i] = 1;
            } else {
                x[i] = 0;
            }
        }

        for (i = 1; i < len; i++) {
            sum += Math.abs(x[i] - x[i - 1]);
        }

        return sum * 1.0 / (len - 1);
    }

    public static double icr(float[][] data, int axis1, int axis2, int len) {
        int count = 0;
        //int len = data.length;
        int i;
        double x, y, x1, y1;
        for (i = 0; i < len - 1; i++) {
            x = data[i][axis1];
            y = data[i][axis2];
            x1 = data[i + 1][axis1];
            y1 = data[i + 1][axis2];
            if (x >= y && x1 < y1 || x <= y && x1 > y1) {
                count += 1;
            }
        }

        return count * 1.0 / len;
    }


}

