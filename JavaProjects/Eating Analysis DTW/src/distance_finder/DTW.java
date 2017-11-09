package distance_finder;

import constants_config.MyParameters;
import myutils.MyArrayUtils;

/**
 *
 * @author mm5gg
 */
public class DTW {

    static float[] infinityCostArray;
    static float[][] infinityCostArray2D;
    static int r = 16;
    static int invariance_length = 16;
    static int length = MyParameters.LEFT_LENGTH + MyParameters.RIGHT_LENGTH + 1;

    public static void setInfinityCostArray() {
        if (infinityCostArray == null) {
            float[] a = new float[2 * r + 1];
            for (int i = 0; i < 2 * r + 1; i++) {
                a[i] = Float.MAX_VALUE;
            }
            infinityCostArray = a;
            //System.out.println("Created infinityCostArray.");
        }
    }

    public static void setInfinityCostArray2D() {
        if (infinityCostArray2D == null) {
            float[][] a = new float[length + 1][length + 1];
            int i, j;
            for (i = 0; i <= length; i++) {
                for (j = 0; j <= length; j++) {
                    a[i][j] = Float.MAX_VALUE;
                }
            }

            a[0][0] = 0;
            /*for (i = 1; i <= invariance_length; i++) {
                a[i][0] = 0;
                a[0][i] = 0;
            }*/

            infinityCostArray2D = a;
            //System.out.println("Created infinityCostArray.");
        }
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
  

    public static float distance(float[][] A, float[][] B, float maxDistance) {
        if (infinityCostArray2D == null) {
            setInfinityCostArray2D();
        }

        float[][] cost = infinityCostArray2D;
        int i = 0, j = 0, start, end;
        float d, min_cost;

        for (i = 1; i <= length; i++) {
            
            if (i <= r) {
                start = 1;
            } else {
                start = i - r + 1;
            }

            if (i >= length - r + 1) {
                end = length;
            } else {
                end = i + r - 1;
            }

            //start = (1 >= i - r + 1 ? 1 : i - r + 1); //max(0, i - r)
            //end = (length <= i + r - 1 ? length : i + r - 1); //min(m - 1, i + r)
            
            min_cost = Float.MAX_VALUE;
            for (j = start; j <= end; j++) {
                d = 1 - (A[0][i - 1] * B[0][j - 1] + A[1][i - 1] * B[1][j - 1] + A[2][i - 1] * B[2][j - 1]);
                cost[i][j] = d + min(cost[i - 1][j], cost[i][j - 1], cost[i - 1][j - 1]);
                if (cost[i][j] < min_cost) {
                    min_cost = cost[i][j];
                }
            }

            //if (i <= length - invariance_length && j <= length - invariance_length && min_cost > maxDistance) {
            if (min_cost > maxDistance) {
                return min_cost;
            }
        }

        min_cost = cost[length][length]; 
        return min_cost;
    }
    
    public static float distanceInvariance(float[][] A, float[][] B, float maxDistance) {
        if (infinityCostArray2D == null) {
            setInfinityCostArray2D();
        }

        float[][] cost = infinityCostArray2D;
        int i = 0, j = 0, start, end;
        float d, min_cost;

        for (i = 1; i <= length; i++) {
            
            if (i <= r) {
                start = 1;
            } else {
                start = i - r + 1;
            }

            if (i >= length - r + 1) {
                end = length;
            } else {
                end = i + r - 1;
            }

            //start = (1 >= i - r + 1 ? 1 : i - r + 1); //max(0, i - r)
            //end = (length <= i + r - 1 ? length : i + r - 1); //min(m - 1, i + r)
            
            min_cost = Float.MAX_VALUE;
            for (j = start; j <= end; j++) {
                d = 1 - (A[0][i - 1] * B[0][j - 1] + A[1][i - 1] * B[1][j - 1] + A[2][i - 1] * B[2][j - 1]);
                cost[i][j] = d + min(cost[i - 1][j], cost[i][j - 1], cost[i - 1][j - 1]);
                if (cost[i][j] < min_cost) {
                    min_cost = cost[i][j];
                }
            }

            //if (i <= length - invariance_length && j <= length - invariance_length && min_cost > maxDistance) {
            if (min_cost > maxDistance) {
                return min_cost;
            }
        }

        min_cost = cost[length][length];        
        for (i = length - invariance_length+1; i <= length; i++) {
            //System.out.println(cost[i][length] + ", " + cost[length][i]);
            if (cost[i][length] < min_cost) {
                min_cost = cost[i][length];
            }

            if (cost[length][i] < min_cost) {
                min_cost = cost[length][i];
            }
        }        
         
        return min_cost;
    }

    public static float distanceUCR(float[][] A, float[][] B, float maxDistance) {
        if (A[0].length != B[0].length || A[0].length != MyParameters.RIGHT_LENGTH + MyParameters.LEFT_LENGTH + 1) {
            System.out.println("Error: length problem in DTW.");
            System.exit(0);
        }

        setInfinityCostArray();
        float[] cost = (float[]) infinityCostArray.clone();
        float[] cost_prev = (float[]) infinityCostArray.clone();
        //System.out.println(infinityCostArray.length+", "+cost.length+", "+cost_prev.length);
        for (int i = 0; i < infinityCostArray.length; i++) {
            if (cost[i] != Float.MAX_VALUE || cost_prev[i] != Float.MAX_VALUE) {
                System.out.println("Error: problem cloning");
                System.exit(0);
            }
        }

        float[] cost_tmp;
        int i, j, k = 0, start, end;
        float x, y, z, min_cost;
        int m = A[0].length;

        for (i = 0; i < m; i++) {
            k = (0 >= r - i ? 0 : r - i); //max(0,r-i);
            min_cost = Float.MAX_VALUE;

            start = (0 >= i - r ? 0 : i - r); //max(0, i - r)
            end = (m - 1 <= i + r ? m - 1 : i + r); //min(m - 1, i + r)
            for (j = start; j <= end; j++, k++) {
                /// Initialize all row and column
                if ((i == 0) && (j == 0)) {
                    cost[k] = 1 - (A[0][0] * B[0][0] + A[1][0 ] * B[1][0] + A[2][0] * B[2][0]);//dist(A[0], B[0]);
                    min_cost = cost[k];
                    continue;
                }

                y = (j - 1 < 0) || (k - 1 < 0) ? Float.MAX_VALUE : cost[k - 1];
                x = (i - 1 < 0) || (k + 1 > 2 * r) ? Float.MAX_VALUE : cost_prev[k + 1];
                z = (i - 1 < 0) || (j - 1 < 0) ? Float.MAX_VALUE : cost_prev[k];

                /// Classic DTW calculation
                cost[k] = min(x, y, z) + 1 - (A[0][i] * B[0][j] + A[1][i] * B[1][j] + A[2][i] * B[2][j]); //dist(A[i], B[j]);

                /// Find minimum cost in row for early abandoning (possibly to use column instead of row).
                if (cost[k] < min_cost) {
                    min_cost = cost[k];
                }
            }

            /// We can abandon early if the current cummulative distace with lower bound together are larger than maxDistance
            if (min_cost > maxDistance) {
                return min_cost;
            }

            /// Move current array to previous array.
            cost_tmp = cost;
            cost = cost_prev;
            cost_prev = cost_tmp;
        }

        k--;

        /// the DTW distance is in the last cell in the matrix of size O(m^2) or at the middle of our array.
        //double final_dtw = cost_prev[k];        
        return cost_prev[k];
    }

}
