/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handwash;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author mm5gg
 */
public class DataProcessHW {

    public static DataHw getTrainTest(float[][] d, int subject, int hand, int total_activity, boolean pose) {
        DataHw dataHw = new DataHw();
        ArrayList<float[]> trainFeatures = new ArrayList<>();
        ArrayList<float[]> testFeatures = new ArrayList<>();
        ArrayList<Integer> trainLabels = new ArrayList<>();
        ArrayList<Integer> testLabels = new ArrayList<>();
        int nrows = d.length, ncols = d[0].length;
        int i, j, k, label;
        float[] features;
        

        for (i = 0; i < nrows; i++) {
            label = (int) (d[i][ncols - 4]);
            if (!pose) {
                label = (label == total_activity) ? 0 : 1;
            } else if (pose && label > total_activity-2) {
                continue;
            }

            features = Arrays.copyOfRange(d[i], 0, ncols - 4);


            if (d[i][ncols - 3] == hand) {
                if (d[i][ncols - 1] == subject) {
                    testFeatures.add(features);
                    testLabels.add(label);
                } else {
                    trainFeatures.add(features);
                    trainLabels.add(label);
                }
            }

        }

        dataHw.trainFeatures = trainFeatures;
        dataHw.testFeatures = testFeatures;
        dataHw.trainLabels = trainLabels;
        dataHw.testLabels = testLabels;
        return dataHw;
    }
}
