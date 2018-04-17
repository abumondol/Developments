package classification;

import java.util.ArrayList;
import m2fedutils.FileUtils;
import java.util.Arrays;
import m2fedutils.MyArrayUtils;

/**
 *
 * @author mm5gg
 */
public class BiteClassifier {

    Pattern[] patterns;
    int window_size_for_min = 16;
    float min_x = (float) (-2.5);
    float smooth_factor = (float) (0.9);
    int window_size_left = 48, window_size_right = 32;
    int pattern_count = 0;

    public BiteClassifier() throws Exception {
        String[][] data = FileUtils.readCSV("my_data/patterns.csv", true);
        patterns = getPatterns(data);
    }

    public ArrayList<Bite> findBites(long[] time, float[][] data) {
        ArrayList<Bite> biteList = new ArrayList<>();
        data = smooth_data(data);        

        ArrayList<Integer> minIndices = findMinPointIndices(data[0]);
        System.out.println("Min Indices Count: " + minIndices.size());
        int res, bite_count = 0;
        for (int i = 0; i < minIndices.size(); i++) {
            /*if(i==1){
                break;
            }*/
            
            res = biteCode(data, minIndices.get(i));            
            bite_count += res;
            if (res > 0) {
                Bite b = new Bite();
                b.index = minIndices.get(i);
                b.time = time[b.index];
                b.type = res;
                biteList.add(b);
            }
        }

        System.out.println("Bite count: " + bite_count);
        return biteList;
    }

    int biteCode(float[][] data, int min_index) {
        if (min_index < window_size_left - 1 || min_index > data[0].length - window_size_right) {
            return 0;
        }

        float minx = data[0][min_index];
        float[][] left = new float[3][], right = new float[3][];
        int i, j;

        for (i = 0; i < 3; i++) {
            left[i] = Arrays.copyOfRange(data[i], min_index - window_size_left + 1, min_index + 1);
            right[i] = Arrays.copyOfRange(data[i], min_index, min_index + window_size_right);
        }
        left = MyArrayUtils.fliplr(left);
        left = normalize_data(left);
        right = normalize_data(right);
        //MyArrayUtils.printArray(left);
        //MyArrayUtils.printArray(right);

        float dist, dist_left, dist_right;
        for (i = 0; i < pattern_count; i++) {
            if (Math.abs(minx - patterns[i].minx) <= 2) {
                dist_left = DTW.distance(left, patterns[i].left);
                dist_right = DTW.distance(right, patterns[i].right);
                dist =  dist_left + dist_right ;
                
                if (dist <= patterns[i].radius) {
                    //System.out.println(i + ", " + patterns[i].radius + ", " + dist_left + ", " + dist_right+ ", " + dist);
                    return 1;
                }
            }
        }

        return 0;
    }

    ArrayList<Integer> findMinPointIndices(float[] x) {
        System.out.println("Size of x:" + x.length);
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<Integer> tempList = new ArrayList<Integer>();

        int w = window_size_for_min;
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

        System.out.println("Size of tempList:" + tempList.size());
        boolean cond1, cond2, cond3;
        for (i = 1; i < tempList.size() - 1; i++) {
            cond1 = x[tempList.get(i)] < x[tempList.get(i - 1)] && x[tempList.get(i)] <= x[tempList.get(i + 1)];
            cond2 = x[tempList.get(i)] <= min_x;
            cond3 = tempList.get(i) >= window_size_left - 1 && tempList.get(i) < x.length - window_size_right;
            if (cond1 && cond2 && cond3) {
                list.add(tempList.get(i));
                //System.out.print(x[tempList.get(i)] + ", ");
            }
        }
        return list;
    }

    float[][] smooth_data(float[][] data) {
        data[0] = smooth_data(data[0]);
        data[1] = smooth_data(data[1]);
        data[2] = smooth_data(data[2]);
        return data;
    }

    float[] smooth_data(float[] data) {
        for (int i = 1; i < data.length; i++) {
            data[i] = data[i - 1] * smooth_factor + data[i] * (1 - smooth_factor);
        }
        return data;
    }

    float[][] normalize_data(float[][] data) {
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

    Pattern[] getPatterns(String[][] data) {
        int total = data.length;
        int sample_count = data[0].length;
        System.out.println("Pattern size: " + total + ", " + sample_count);
        Pattern[] p = new Pattern[total];
        float[][] left, right;
        float minx, radius;
        int i, j, k;
        int window_size = window_size_left + window_size_right - 1;

        for (i = 0; i < total; i++) {
            left = new float[3][window_size_left];
            right = new float[3][window_size_right];

            for (j = 0; j < window_size_left; j++) {
                left[0][j] = Float.parseFloat(data[i][j]);
                left[1][j] = Float.parseFloat(data[i][window_size + j]);
                left[2][j] = Float.parseFloat(data[i][2 * window_size + j]);
            }
            left = MyArrayUtils.fliplr(left);

            for (j = 0; j < window_size_right; j++) {
                right[0][j] = Float.parseFloat(data[i][window_size_left - 1 + j]);
                right[1][j] = Float.parseFloat(data[i][window_size + window_size_left - 1 + j]);
                right[2][j] = Float.parseFloat(data[i][2 * window_size + window_size_left - 1 + j]);
            }

            p[i] = new Pattern();
            p[i].left = left;
            p[i].right = right;
            p[i].minx = Float.parseFloat(data[i][sample_count - 2]);
            p[i].radius = Float.parseFloat(data[i][sample_count - 1]);
            //System.out.println("Minx: " + p[i].minx + ", Radius: " + p[i].radius);
            //MyArrayUtils.printArray(left);
            //MyArrayUtils.printArray(right);            

        }
        if(pattern_count==0)
            pattern_count = p.length;

        return p;
    }
}
