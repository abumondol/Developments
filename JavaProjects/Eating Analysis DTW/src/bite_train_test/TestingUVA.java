package bite_train_test;

import constants_config.MyParameters;
import data_processing.DataManagerUVA;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import pattern.PatternUtils;

/**
 *
 * @author mm5gg
 */
public class TestingUVA {

    public static void testLabData(ArrayList<Pattern> patListTrain) throws Exception {        
        //patListTrain = ReTraining.updateTrainingList(-1, DataManagerUVA.getFreeData(), patListTrain);            
        SessionData[] sds = DataManagerUVA.getLabData();
        Pattern[] pats;
        int count = sds.length;
        for (int i = 0; i < count; i++) {
            System.out.println("UVA Eat Session: " + i);
            pats = Testing.test(sds[i].accelTime, sds[i].accelData, patListTrain);
            if (pats == null) {
                System.out.println("Count: 0");
                continue;
            }

            int biteCount = 0;
            for (int j = 0; j < pats.length; j++) {
                //System.out.println(sds[i].accelTime[pats[j].minPointIndex] + ", " + pats[j].score);
                if (Testing.getScore(pats[j]) > MyParameters.SCORE_THRESHOLD) {
                    //System.out.println(sds[i].accelTime[pats[j].minPointIndex] + ", " + pats[j].score);
                    biteCount++;
                }
            }

            System.out.println("Potential Bite Count: " + pats.length + ", Detected bite count: " + biteCount);
            System.out.println();

        }

    }

    public static void testNonEatData(ArrayList<Pattern> patListTrain) throws Exception {

        SessionData[] sds = DataManagerUVA.getFreeData();
        Pattern[] pats;
        ArrayList<Pattern> patListTrainOriginal = patListTrain;

        int count = sds.length;
        for (int i = 0; i < count; i++) {
            System.out.println("UVA NonEat Session " + i);
            patListTrain = ReTraining.updateTrainingList(i, sds, patListTrainOriginal);
            pats = Testing.test(sds[i].accelTime, sds[i].accelData, patListTrain);

            if (pats == null) {
                System.out.println("No minIndices found, Detected bite Count: 0");
                continue;
            }

            int biteCount = 0;
            for (int j = 0; j < pats.length; j++) {
                //System.out.println(sds[i].accelTime[pats[j].minPointIndex] + ", " + pats[j].score);
                if (Testing.getScore(pats[j]) > MyParameters.SCORE_THRESHOLD) {                    
                    biteCount++;
                }
            }

            System.out.println("Potential Bite Count: " + pats.length + ", Detected bite count: " + biteCount);
            System.out.println();
        }

    }

}
