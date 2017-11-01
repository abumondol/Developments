package classification;

import constants_config.EatConfig;
import entities.Pattern;
import entities.Bite;
import m2fedutils.MathUtils;
import java.util.ArrayList;
import java.util.Arrays;
import m2fedutils.DataUtils;
import m2fedutils.MyArrayUtils;

public class BiteDetector {

    public static ArrayList<Bite> findBites(long[] time, float[][] data, Pattern[] patterns) {
        ArrayList<Bite> biteList = new ArrayList<>();
        data = DataUtils.smooth_data(data);

        System.out.print("Counts >> ");
        ArrayList<Integer> minIndices = findMinPointIndices(data[0]);
        System.out.print(", MinIndicesSelected: " + minIndices.size());
        int res;
        for (int i = 0; i < minIndices.size(); i++) {
            if (!preClassification(data, minIndices.get(i))) {
                continue;
            }

            res = matchPattern(data, minIndices.get(i), patterns);
            if (res >= 0) {
                Bite b = new Bite();
                b.index = minIndices.get(i);
                b.time = time[b.index];
                b.type = res;
                biteList.add(b);
                //System.out.println(b.time / 1000 + "," + b.time / 1000 / 60 + ":" + (b.time / 1000) % 60 + "," + res);
            }
        }

        System.out.println(", Bites: " + biteList.size());
        return biteList;
    }

    public static boolean preClassification(float[][] data, int min_index) {
        if (min_index < EatConfig.window_size_left - 1 || min_index > data[0].length - EatConfig.window_size_right - 1) {
            return false;
        }

        float varx = MathUtils.var(data[0], min_index - EatConfig.window_size_left + 1, min_index + EatConfig.window_size_right);
        float vary = MathUtils.var(data[1], min_index - EatConfig.window_size_left + 1, min_index + EatConfig.window_size_right);
        float varz = MathUtils.var(data[2], min_index - EatConfig.window_size_left + 1, min_index + EatConfig.window_size_right);
        return varx + vary + varz > EatConfig.varTh;
    }

    public static int matchPattern(float[][] data, int min_index, Pattern[] patterns) {
        float[][] left = new float[3][], right = new float[3][];
        int i, j;

        for (i = 0; i < 3; i++) {
            left[i] = Arrays.copyOfRange(data[i], min_index - EatConfig.window_size_left + 1, min_index + 1);
            right[i] = Arrays.copyOfRange(data[i], min_index, min_index + EatConfig.window_size_right);
        }
        left = MyArrayUtils.fliplr(left);
        left = DataUtils.normalize_data(left);
        right = DataUtils.normalize_data(right);
        //MyArrayUtils.printArray(left);
        //MyArrayUtils.printArray(right);

        float dist, dist_left, dist_right;
        for (i = 0; i < patterns.length; i++) {
            dist_left = PatternManager.distance_DTW(left, patterns[i].left);
            dist_right = PatternManager.distance_DTW(right, patterns[i].right);
            dist = dist_left + dist_right;
            if (dist <= EatConfig.radius_extension_factor * patterns[i].radius) {
                //System.out.println(i + ", " + patterns[i].radius + ", " + dist_left + ", " + dist_right+ ", " + dist);
                return i;
            }
        }

        return -1;
    }

    public static ArrayList<Integer> findMinPointIndices(float[] x) {
        System.out.print("Sample: " + x.length);
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> tempList = new ArrayList<>();

        int w = EatConfig.window_size_for_min;
        int i, j, ix;

        for (i = 0; i < x.length - w; i += w) {
            ix = i;
            for (j = i; j < i + w; j++) {
                if (x[j] < x[ix]) {
                    ix = j;
                }
            }
            //System.out.print(x[ix]+",");
            tempList.add(ix);
        }

        System.out.print(", TempMinPointIndices:" + tempList.size());
        boolean cond1, cond2, cond3;
        for (i = 1; i < tempList.size() - 1; i++) {
            cond1 = x[tempList.get(i)] < x[tempList.get(i - 1)] && x[tempList.get(i)] <= x[tempList.get(i + 1)];
            cond2 = x[tempList.get(i)] <= EatConfig.min_x;
            cond3 = tempList.get(i) >= EatConfig.window_size_left - 1 && tempList.get(i) < x.length - EatConfig.window_size_right;
            if (cond1 && cond2 && cond3) {
                list.add(tempList.get(i));
            }
        }
        return list;
    }

}
