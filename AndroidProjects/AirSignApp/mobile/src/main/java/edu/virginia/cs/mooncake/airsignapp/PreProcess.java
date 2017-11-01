package edu.virginia.cs.mooncake.airsignapp;

/**
 * Created by TOSHIBA on 3/18/2016.
 */

////64 HZ Preprocess from begining, 32 later
public class PreProcess{

    public static double[][] processSignSingle(double[][] data, int step) {

        int acl_count = 0, gyro_count = 0;
        int i, j, k;
        for (i = 0; i < data.length; i++) {
            if (data[i][1] == 1) {
                acl_count++;
            } else if (data[i][1] == 4) {
                gyro_count++;
            }
        }
        //System.out.println(String.format("Count  ACL: %d, Gyro:%d", acl_count, gyro_count));

        double[][] acl = new double[acl_count][];
        double[][] gyro = new double[gyro_count][];

        acl_count = gyro_count = 0;
        for (i = 0; i < data.length; i++) {
            if (data[i][1] == 1) {
                acl[acl_count] = data[i];
                acl_count++;

            } else if (data[i][1] == 4) {
                gyro[gyro_count] = data[i];
                gyro_count++;
            }
        }

        // smooth data //
        acl = smoothData(acl, 5);
        gyro = smoothData(gyro, 5);


        //  find cut times
        int[] cut_indices = findCutIndicesAcl(acl);
        //System.out.println("Length, start_cut, end_cut:" + " " + acl.length + " " + cut_indices[0] + " " + cut_indices[1]);
        if (cut_indices[0] < 0 || cut_indices[1] < 0) {
            return null;
        }

        // cut acl data
        acl_count = cut_indices[1] - cut_indices[0] + 1;
        double[][] acl_processed = new double[3][acl_count];
        for (i = cut_indices[0], j = 0; i <= cut_indices[1]; i++, j++) {
            acl_processed[0][j] = acl[i][3];
            acl_processed[1][j] = acl[i][4];
            acl_processed[2][j] = acl[i][5];
            ////System.out.println(j + ": " + acl_processed[0][j] + "," + acl_processed[1][j] + "," + acl_processed[2][j]);
        }

        //cut gyro data
        double start_time = acl[cut_indices[0]][0];
        double end_time = acl[cut_indices[1]][0];
        for (i = 0; i < gyro.length && gyro[i][0] < start_time; i++) ;
        cut_indices[0] = i;

        for (i = gyro.length - 1; i >= 0 && gyro[i][0] > end_time; i--) ;
        cut_indices[1] = i;

        gyro_count = cut_indices[1] - cut_indices[0] + 1;
        double[][] gyro_processed = new double[3][gyro_count];
        for (i = cut_indices[0], j = 0; i <= cut_indices[1]; i++, j++) {
            gyro_processed[0][j] = gyro[i][3];
            gyro_processed[1][j] = gyro[i][4];
            gyro_processed[2][j] = gyro[i][5];
            ////System.out.println(j + ": " + gyro_processed[0][j] + "," + gyro_processed[1][j] + "," + gyro_processed[2][j]);
        }

        //downsample

        double[][] processed_data = new double[6][];
        processed_data[0] = downSample(acl_processed[0], step);
        processed_data[1] = downSample(acl_processed[1], step);
        processed_data[2] = downSample(acl_processed[2], step);
        processed_data[3] = downSample(gyro_processed[0], step);
        processed_data[4] = downSample(gyro_processed[1], step);
        processed_data[5] = downSample(gyro_processed[2], step);
        return processed_data;

    }

    public static double[] downSample(double[] data, int step) {
        final int len = data.length;
        int new_len = len / step;
        double[] res = new double[new_len];

        int i;
        for (i = 0; i < new_len; i += step) {
            res[i] = data[i * step];
        }
        return res;

    }

    public static double[][] smoothData(double[][] data, int period) {
        double factor = 2.0 / (period + 1);
        final int len = data.length;

        int i, j;
        for (i = 1; i < len; i++) {
            data[i][3] = factor * data[i][3] + (1 - factor) * data[i - 1][3];
            data[i][4] = factor * data[i][4] + (1 - factor) * data[i - 1][4];
            data[i][5] = factor * data[i][5] + (1 - factor) * data[i - 1][5];
        }
        return data;

    }

    public static int[] findCutIndicesAcl(double[][] acl) {

        int len = acl.length;
        double[] acl_mag = new double[len];
        int i, j;
        for (i = 0; i < len; i++) {
            acl_mag[i] = Math.sqrt(acl[i][3] * acl[i][3] + acl[i][4] * acl[i][4] + acl[i][5] * acl[i][5]);
        }

        acl_mag = zscore_abs(acl_mag);

        int[] indices = new int[2];
        int skip_points = 20;
        int start_index = skip_points, end_index = len - skip_points;
        double th = 0.5;
        double th_sq = th * th;

        // FInding start cut pint //
        int a = start_index;
        while (a < end_index && acl_mag[a] < th) {
            a++;
        }

        if (a >= end_index) {
            indices[0] = -1;
            return indices;
        }
        //System.out.println("Start goes up to: " + a);

        while (a > start_index && acl_mag[a] > acl_mag[a - 1]) {
            a--;
        }

        //System.out.println("Start at " + a);
        indices[0] = a;


        /**
         * **** Finding end cut pint ******
         */
        a = end_index;
        while (a > start_index && acl_mag[a] < th) {
            a--;
        }

        if (a <= start_index) {
            indices[1] = -1;
            return indices;
        }

        //System.out.println("Start comes down to: " + a);
        while (a < end_index && acl_mag[a] > acl_mag[a + 1]) {
            a = a + 1;
        }
        //System.out.println("End at " + a);
        indices[1] = a;

        return indices;
    }

    public static double[] zscore_abs(double[] data) {
        int n = data.length;
        double[] res = new double[n];

        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += data[i];
        }

        double avg = sum / n;
        sum = 0;
        double d;
        for (int i = 0; i < n; i++) {
            d = (data[i] - avg);
            sum += d * d;
        }

        double std = Math.sqrt(sum / (n - 1));

        for (int i = 0; i < n; i++) {
            res[i] = (data[i] - avg) / std;
        }

        return res;
    }
}
