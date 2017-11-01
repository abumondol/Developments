package bite_train_test;

import data_processing.DataManager;
import data_processing.DataManagerSteven;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class TestingSteven {

    public static void testFreeData(ArrayList<Pattern> patListTrain, int reTrain) throws Exception {
        SessionData[][] sds = DataManagerSteven.getFreeData();
        Pattern[] pats;
        Pattern[][][] allPats = new Pattern[sds.length][][];
        ArrayList<Pattern> patListTrainOriginal = patListTrain;

        for (int subject = 0; subject < sds.length; subject++) {
            allPats[subject] = new Pattern[sds[subject].length][];
            if (reTrain == 2 || reTrain == 3) {
                patListTrain = updateTrainingListForSubject(subject, sds, patListTrainOriginal);
            }

            for (int session = 0; session < sds[subject].length; session++) {
                System.out.println("Tesing StevenTech Free Subject, Session : " + subject + ", " + session);

                if (reTrain == 1 || reTrain == 3) {
                    patListTrain = ReTraining.updateTrainingList(session, sds[subject], patListTrainOriginal);

                }

                pats = Testing.test(sds[subject][session].accelTime, sds[subject][session].accelData, patListTrain);
                allPats[subject][session] = pats;

                if (pats == null) {
                    System.out.println(" Detected bite Count: 0");
                    continue;
                }

                int biteCount = 0;
                for (int j = 0; j < pats.length; j++) {
                    if (pats[j].biteScore > 0.5) {
                        //System.out.println(sds[subject][session].accelTime[pats[j].minPointIndex] + ", " + pats[j].score);
                        biteCount++;
                    }
                }

                System.out.println("Potential Bite Count: " + pats.length + ", Detected bite count: " + biteCount);

            }
        }

        MyFileUtils.serializeFile(allPats, DataManager.serFilePath + "steven_bite_results_free_retrain_" + reTrain + ".ser");

    }

    public static ArrayList<Pattern> updateTrainingListForSubject(int excludeSubject, SessionData[][] sds, ArrayList<Pattern> patListTrain) throws Exception {
        for (int i = 0; i < sds.length; i++) {
            if (i == excludeSubject) {
                continue;
            }

            System.out.println("ReTraining for Subejct " + excludeSubject + ", by Subject: " + i);
            patListTrain = ReTraining.updateTrainingList(-1, sds[i], patListTrain);

        }

        return patListTrain;
    }

}
