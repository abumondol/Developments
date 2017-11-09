package bite_train_test;

import constants_config.MyParameters;
import distance_finder.DTW;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import pattern.PatternManager;
import pattern.PatternUtils;
import pattern.PotentialIndexFinder;

/**
 *
 * @author mm5gg
 */
public class ReTrainingUtils {

    public static ArrayList<Pattern> reTrainingExcludingSubejct(int excludeSubject, SessionData[][] sdss, ArrayList<Pattern> patListTrain) throws Exception {
        
        System.out.println();
        for (int subject = 0; subject < sdss.length; subject++) {
            if (subject == excludeSubject) {
                continue;
            }

            patListTrain = reTrainingExcludingSession(-1, sdss[subject], patListTrain);
            System.out.println();
        }

        return patListTrain;
    }

    public static ArrayList<Pattern> reTrainingExcludingSession(int excludeSession, SessionData[] sds, ArrayList<Pattern> patListTrain) throws Exception {
        for (int session = 0; session < sds.length; session++) {
            if (session == excludeSession) {
                continue;
            }

            patListTrain = reTrainingBySession(sds[session], patListTrain);
            
        }

        return patListTrain;
    }

    public static ArrayList<Pattern> reTrainingBySession(SessionData sd, ArrayList<Pattern> patListTrain) throws Exception {
        int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(sd.accelData);
        if (minIndices == null) {
            return null;
        }

        Pattern[] pats = PatternManager.getPatterns(sd.accelTime, sd.accelData, minIndices);
        System.out.println("Retraining by subject: " + sd.subjectId + ", Session:" + sd.sessionId + ", patCount:" + pats.length);
        if (sd.meals != null) {
            for (int i = 0; i < pats.length; i++) {
                for (int j = 0; j < sd.meals.length; j++) {
                    if (pats[i].minPointIndex >= sd.meals[j][0] && pats[i].minPointIndex <= sd.meals[j][1]) {
                        pats[i].label = Pattern.PATTERN_TYPE_X;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < pats.length; i++) {
            patListTrain = reTrainingByPattern(pats[i], patListTrain);
        }

        System.out.print("Retraining done >> ");
        PatternUtils.printPatternListStat(patListTrain, false);

        return patListTrain;
    }

    public static ArrayList<Pattern> reTrainingByPattern(Pattern p1, ArrayList<Pattern> patList) {
        int i, j, count = patList.size();
        float d, min_dist = Float.MAX_VALUE;
        Pattern p2;

        for (i = 0; i < count; i++) {
            p2 = patList.get(i);
            d = DTW.distanceUCR(p1.dataNormalized, p2.dataNormalized, MyParameters.MAX_BIN_DISTANCE);
            if (d < min_dist) {
                min_dist = d;
            }

            if (d < MyParameters.MAX_BIN_DISTANCE) {
                int binIndex = PatternUtils.distanceToBinIndex(d);
                p1.coverageCountBins[p2.label][binIndex]++;
                p2.coverageCountBins[p1.label][binIndex]++;

                float xDiff = Math.abs(p1.minPointXVal - p2.minPointXVal);
                if (xDiff > p1.binXDiffMax[binIndex]) {
                    p1.binXDiffMax[binIndex] = xDiff;
                }

                if (xDiff > p2.binXDiffMax[binIndex]) {
                    p2.binXDiffMax[binIndex] = xDiff;
                }

            }

        }

        if (min_dist > MyParameters.RETAINING_DISTANCE) {
            if (p1.label == Pattern.PATTERN_TYPE_X || min_dist > MyParameters.MAX_BIN_DISTANCE) {
                patList.add(p1);
            }
        }

        return patList;

    }

    public static ArrayList<Pattern> pruneTrainingList(ArrayList<Pattern> patList, int maxBins) {
        int i, j, c, count = patList.size();
        float d;
        Pattern p;

        for (i = count - 1; i > 0; i--) {
            p = patList.get(i);
            c = 0;
            for (i = 0; i < maxBins; i++) {
                c += p.coverageCountBins[0][i] + p.coverageCountBins[1][i] + p.coverageCountBins[2][i];
            }

            if (c == 0) {
                patList.remove(i);
            }
        }

        return patList;
    }

}
