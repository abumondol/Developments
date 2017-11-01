package distance_finder;

import constants_config.MyParameters;
import entities.Pattern;
import java.util.ArrayList;
import myutils.MyMathUtils;

/**
 *
 * @author mm5gg
 */
public class DistanceManager {

    public static int NC = MyParameters.LEFT_LENGTH + MyParameters.RIGHT_LENGTH + 1;
    public static float[][] d = d = new float[NC][NC];

    public static float[][] findDistancesListToList(ArrayList<Pattern> list1, ArrayList<Pattern> list2) {
        int i, count = list1.size();
        float[][] dist = new float[count][];
        for (i = 0; i < count; i++) {
            System.out.print(i + ",");
            if (i % 10 == 0) {
                System.out.println();
            }
            dist[i] = findDistancesOneToList(list1.get(i), list2);
        }

        System.out.println("\n");

        return dist;
    }

    public static float[] findDistancesOneToList(Pattern p, ArrayList<Pattern> list) {
        int i, count = list.size();
        float[] dist = new float[count];
        for (i = 0; i < count; i++) {
            dist[i] = findDistanceDTW(p.dataNormalized, list.get(i).dataNormalized);
        }
        return dist;

    }

    public static float findDistanceDTW(float[][] t, float[][] r) {
        int N = t[0].length;
        int M = r[0].length;

        if (N != NC || M != NC) {
            System.out.println("N = " + N + ", M = " + M + ", NC = " + NC);
            System.exit(0);
        }

        int n, m;
        for (n = 0; n < N; n++) {
            for (m = 0; m < M; m++) {
                //d[n][m] = 1 - (t[n][0] * r[m][0] + t[n][1] * r[m][1] + t[n][2] * r[m][2]);
                d[n][m] = 1 - (t[0][n] * r[0][m] + t[1][n] * r[1][m] + t[2][n] * r[2][m]);
            }
        }

        for (n = 1; n < N; n++) {
            d[n][0] = d[n][0] + d[n - 1][0];
        }

        for (m = 1; m < M; m++) {
            d[0][m] = d[0][m] + d[0][m - 1];
        }

        for (n = 1; n < N; n++) {
            for (m = 1; m < M; m++) {
                d[n][m] = d[n][m] + MyMathUtils.min(d[n - 1][m], d[n][m - 1], d[n - 1][m - 1]);
            }
        }

        return d[N - 1][M - 1];
    }

    public static boolean dtw(float[][] t, float[][] r, float maxDist) {
        int N = t[0].length;
        int M = r[0].length;

        if (N != NC || M != NC) {
            System.out.println("N = " + N + ", M = " + M + ", NC = " + NC);
            System.exit(0);
        }        

        int n, m;
        for (n = 0; n < N; n++) {
            for (m = 0; m < M; m++) {
                //d[n][m] = 1 - (t[n][0] * r[m][0] + t[n][1] * r[m][1] + t[n][2] * r[m][2]);
                d[n][m] = 1 - (t[0][n] * r[0][m] + t[1][n] * r[1][m] + t[2][n] * r[2][m]);
            }
        }

        for (n = 1; n < N; n++) {
            d[n][0] = d[n][0] + d[n - 1][0];
        }

        for (m = 1; m < M; m++) {
            d[0][m] = d[0][m] + d[0][m - 1];
        }

        for (n = 1; n < N; n++) {
            for (m = 1; m < M; m++) {
                d[n][m] = d[n][m] + MyMathUtils.min(d[n - 1][m], d[n][m - 1], d[n - 1][m - 1]);
            }
        }

        if (d[N - 1][M - 1] <= maxDist) {
            return true;
        } else {
            return false;
        }
    }
}
