package bite_train_test;

import constants_config.MyConstants;
import constants_config.MyParameters;
import data_processing.DataManagerSteven;
import data_processing.DataManagerUVA;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class Testing {

    public static Pattern[][][] testDataset(ArrayList<Pattern> patListTrain, int datasetId, int[] total) throws Exception {
        SessionData[][] sdss = null;
        switch (datasetId) {
            case MyConstants.DATAET_ID_UVA_LAB:
                sdss = DataManagerUVA.getLabData();
                break;
            case MyConstants.DATAET_ID_STEVEN_LAB:
                sdss = DataManagerSteven.getLabData();
                break;
            case MyConstants.DATAET_ID_UVA_FREE:
                sdss = DataManagerUVA.getFreeData();
                break;
            case MyConstants.DATAET_ID_STEVEN_FREE:
                sdss = DataManagerSteven.getFreeData();
                break;
            default:
                System.out.println("dataset id problem");
                System.exit(0);
        }

        System.out.println("Test Results >>  Dataset: " + datasetId);
        Pattern[][][] pats = new Pattern[sdss.length][][];
        for (int subject = 0; subject < sdss.length; subject++) {
            pats[subject] = testSubject(patListTrain, sdss[subject], total);
        }

        System.out.println("Total Results: ");
        printResult(total);
        return pats;
    }

    public static Pattern[][] testSubject(ArrayList<Pattern> patListTrain, SessionData[] sds, int[] total) throws Exception {
        Pattern[][] pats = new Pattern[sds.length][];
        for (int sess = 0; sess < sds.length; sess++) {
            pats[sess] = testSession(patListTrain, sds[sess], total);
        }

        return pats;
    }

    public static Pattern[] testSession(ArrayList<Pattern> patListTrain, SessionData sd, int[] total) throws Exception {
        Pattern[] pats = TestUtils.testSession(sd, patListTrain);
        int[] res;

        System.out.print("Results Subject :" + sd.subjectId + ", Session: " + sd.sessionId + " >> ");
        if (sd.annots != null) {
            res = resultLab(pats, sd);
        } else {
            res = resultFree(pats, sd);
        }

        printResult(res);
        sumResult(total, res);
        return pats;
    }

    public static int[] resultLab(Pattern[] pats, SessionData sd) {
        int[][] a = sd.annots;
        int btp = 0, bfn = 0, stp = 0, sfn = 0, fp = 0, tn = 0; //b for bite, s for sip

        int[] resAnnots = new int[a.length];

        int minIndex;
        int minDist, dist;
        for (int i = 0; i < pats.length; i++) {
            //System.out.println("Score "+i+": "+pats[i].biteScore);
            if (pats[i].minPointXVal > -2.5 || pats[i].nearestDistance > 2 || pats[i].biteScore < MyParameters.SCORE_THRESHOLD) {
                tn++;
                continue;
            }

            minIndex = 0;
            minDist = Math.abs(pats[i].minPointIndex - a[0][0]);
            for (int j = 1; j < a.length; j++) {
                dist = Math.abs(pats[i].minPointIndex - a[j][0]);
                if (dist < minDist) {
                    minDist = dist;
                    minIndex = j;
                }
            }

            if (minDist < MyParameters.TEST_ANNOT_DISTANCE) {
                resAnnots[minIndex] = 1;
            } else {
                fp++;
            }
        }

        for (int j = 0; j < resAnnots.length; j++) {
            if (resAnnots[j] == 0 && a[j][1] == 1) {
                bfn++;
            } else if (resAnnots[j] == 0 && a[j][1] == 2) {
                sfn++;
            } else if (resAnnots[j] == 1 && a[j][1] == 1) {
                btp++;
            } else if (resAnnots[j] == 1 && a[j][1] == 2) {
                stp++;
            }
        }

        System.out.print(tn + " >>");
        int[] res = {btp, bfn, stp, sfn, fp, tn};
        return res;
    }

    public static int[] resultFree(Pattern[] pats, SessionData sd) {
        int[] res = new int[6];
        int[][] meals = sd.meals;
        int c = 0;
        boolean flag = false, lastMealIndex = false;

        System.out.println();
        for (int i = 0; i < pats.length; i++) {
            //System.out.println("Score "+i+": "+pats[i].biteScore);
            if (pats[i].minPointXVal > -2.5 || pats[i].nearestDistance > 2 || pats[i].biteScore < MyParameters.SCORE_THRESHOLD) {
                continue;
            }

            System.out.print(i + "/" + pats.length + ": " + pats[i].minPointTime + ", ");
            if (meals == null) {
                System.out.println(", N");
                continue;
            }

            flag = false;
            for (int j = 1; j < meals.length; j++) {
                if (pats[i].minPointIndex >= meals[j][0] && pats[i].minPointIndex <= meals[j][1]) {
                    System.out.println(", Y, "+meals[j][2]);
                    c++;
                    flag = true;
                    break;
                }
            }

            if (!flag) {                
                System.out.println(", N");
                if(c>0)
                    System.out.println(c);                
                c = 0;               
            }
            
        }

        return res;
    }

    public static void sumResult(int[] total, int[] res) {
        for (int i = 0; i < total.length; i++) {
            total[i] += res[i];
        }
    }

    public static void printResult(int[] res) {
        for (int i = 0; i < res.length - 1; i++) {
            System.out.print(res[i] + ", ");
        }

        System.out.println(res[res.length - 1]);
    }
}
