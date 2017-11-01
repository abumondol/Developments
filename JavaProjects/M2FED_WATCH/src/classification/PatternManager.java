package classification;

import constants_config.EatConfig;
import entities.Pattern;
import m2fedutils.FileUtils;
import m2fedutils.MyArrayUtils;

public class PatternManager {

    public static float distance_DTW(float[][] t, float[][] r) {
        int N = t[0].length;
        int M = r[0].length;

        float[][] d = new float[N][M];
        float[][] D = new float[N][M];
        float dx, dy, dz;

        int n, m;
        for (n = 0; n < N; n++) {
            for (m = 0; m < M; m++) {
                d[n][m] = 1 - (t[0][n] * r[0][m] + t[1][n] * r[1][m] + t[2][n] * r[2][m]);
//                dx = t[0][n] - r[0][m];
//                dy = t[1][n] - r[1][m];
//                dz = t[2][n] - r[2][m];
//                d[n][m] = (float)Math.sqrt(dx * dx + dy * dy + dz* dz);

                /*if(d[n][m]<-0.001){
                    System.out.println(String.format("Distance error:%f", d[n][m]));
                    System.out.println(String.format("T: %f, %f, %f",t[0][n], t[1][n], t[2][n]));
                    System.out.println(String.format("R: %f, %f, %f",r[0][m], r[1][m], r[2][m]));
                    
                    System.exit(0);
                }*/
            }
        }

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

    public static Pattern[] readPatterns() throws Exception {
        String[][] data = FileUtils.readCSV("my_data/patterns.csv", true);
        int total = data.length;
        int sample_count = data[0].length;
        System.out.println("Pattern size: " + total + ", " + sample_count);

        if (EatConfig.pattern_count_to_be_used > 0 && EatConfig.pattern_count_to_be_used < total) {
            total = EatConfig.pattern_count_to_be_used;
        }

        Pattern[] p = new Pattern[total];
        float[][] left, right;
        int i, j;
        int window_size = EatConfig.window_size_left + EatConfig.window_size_right - 1;

        for (i = 0; i < total; i++) {
            left = new float[3][EatConfig.window_size_left];
            right = new float[3][EatConfig.window_size_right];

            for (j = 0; j < EatConfig.window_size_left; j++) {
                left[0][j] = Float.parseFloat(data[i][j]);
                left[1][j] = Float.parseFloat(data[i][window_size + j]);
                left[2][j] = Float.parseFloat(data[i][2 * window_size + j]);
            }
            left = MyArrayUtils.fliplr(left);

            for (j = 0; j < EatConfig.window_size_right; j++) {
                right[0][j] = Float.parseFloat(data[i][EatConfig.window_size_left - 1 + j]);
                right[1][j] = Float.parseFloat(data[i][window_size + EatConfig.window_size_left - 1 + j]);
                right[2][j] = Float.parseFloat(data[i][2 * window_size + EatConfig.window_size_left - 1 + j]);
            }

            p[i] = new Pattern();
            p[i].left = left;
            p[i].right = right;
            p[i].minx = Float.parseFloat(data[i][sample_count - 2]);
            p[i].radius = Float.parseFloat(data[i][sample_count - 1]);
            //p[i].covered_count = Integer.parseInt(data[i][sample_count - 1]);
            //System.out.println("Minx: " + p[i].minx + ", Radius: " + p[i].radius);
            //MyArrayUtils.printArray(left);
            //MyArrayUtils.printArray(right);            

        }
        return p;
    }

}
