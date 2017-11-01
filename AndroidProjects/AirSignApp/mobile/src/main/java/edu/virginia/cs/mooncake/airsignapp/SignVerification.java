package edu.virginia.cs.mooncake.airsignapp;

/**
 * Created by TOSHIBA on 3/20/2016.
 */
public class SignVerification {

    public static int[] verifyTrain(double[][][] distances, float trainTh) {
        int trainCount = distances.length;
        int testPairs = 4; // it should be by combination theory
        int otherPairs = 6; // it should be by combination theory
        double[][] testPairDistances = new double[testPairs][];
        double[][] otherPairDistances = new double[otherPairs][];
        double[][] mean_std_test;
        double[][] mean_std_other;
        double deviation, maxDeviation = 0;
        int[] res = new int[trainCount + 1];

        int axis_count = MyConfig.AXIS_COUNT;

        int s, i, j, m, n, maxIndex = 0;
        for (s = 0; s < trainCount; s++) {

            m = n = 0;
            for (i = 0; i < trainCount - 1; i++) {
                for (j = i + 1; j < trainCount; j++) {
                    if (i == s || j == s) {
                        testPairDistances[m] = distances[i][j];
                        m++;
                    } else {
                        otherPairDistances[n] = distances[i][j];
                        n++;
                    }
                }
            }

            mean_std_other = meanStd(otherPairDistances);
            mean_std_test = meanStd(testPairDistances);

            deviation = 0;
            for (i = 0; i < axis_count; i++) {
                deviation += (mean_std_test[0][i] - mean_std_other[0][i]) / mean_std_other[1][i];
            }
            deviation = deviation/axis_count;

            res[s + 1] = (int) (deviation * 1000);

            if (s == 0 || deviation > maxDeviation) {
                maxDeviation = deviation;
                maxIndex = s;
            }
        }

        if (maxDeviation > trainTh) {
            res[0] = maxIndex;
            return res;
        }

        res[0] = 100;
        return res;
    }


    public static double  findTestDeviation(double[][] muSigmaTemplate, double[][] muSigmaTest){
        double[][] ms = muSigmaTemplate;
        double[][] mst = muSigmaTest;
        int axis_count = MyConfig.AXIS_COUNT;

        double deviation = 0;
        for (int i = 0; i < axis_count; i++) {
            deviation += (mst[0][i] - ms[0][i]) / ms[1][i];
        }

        deviation = deviation/axis_count;

        return deviation;
    }


    public static double[][] findMeanStdAllPairs(double[][][] distances){
        //finding mean for all pairs of signatures
        int signCount = distances.length;
        int pairsCount = 10;
        double[][] allPairDistances = new double[pairsCount][];
        int i, j, count=0;
        for (i = 0; i < signCount - 1; i++) {
            for (j = i + 1; j < signCount; j++) {
                    allPairDistances[count] = distances[i][j];
                    count++;
            }
        }

        return meanStd(allPairDistances);
    }

    public static double[][] meanStd(double[][] pairDistances) {
        int len = pairDistances.length;
        double[][] m_s = new double[2][6];
        double sum, mean, std, d;

        int i, j;
        for (j = 0; j < 6; j++) {
            sum = 0;
            for (i = 0; i < len; i++) {
                sum += pairDistances[i][j];
            }

            mean = sum / len;

            sum = 0;
            for (i = 0; i < len; i++) {
                d = pairDistances[i][j] - mean;
                sum += d * d;
            }
            std = Math.sqrt(sum / (len - 1));

            m_s[0][j] = mean;
            m_s[1][j] = std;

        }

        return m_s;
    }


}
