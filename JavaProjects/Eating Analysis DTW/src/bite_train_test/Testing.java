package bite_train_test;

import constants_config.MyParameters;
import distance_finder.DTW;
import entities.Pattern;
import java.util.ArrayList;
import pattern.PatternManager;
import pattern.PotentialIndexFinder;

/**
 *
 * @author mm5gg
 */
public class Testing {

    public static Pattern[] test(float[] accelTime, float[][] accelData, ArrayList<Pattern> patListTrain) throws Exception {
        int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(accelData);
        if (minIndices == null) {
            return null;
        }

        Pattern[] pats = PatternManager.getPatterns(accelTime, accelData, minIndices);
        return Testing.test(pats, patListTrain);
    }

    public static Pattern[] test(Pattern[] pats, ArrayList<Pattern> patListTrain) throws Exception {
        for (int i = 0; i < pats.length; i++) {
            findNearestPattern(pats[i], patListTrain);
        }
        return pats;
    }

    public static void findNearestPattern(Pattern pat, ArrayList<Pattern> list) {

        int min_index = 0, count = list.size();
        float d;
        float nCount = 0, xCount = 0, pCount = 0, min_dist = Float.MAX_VALUE;
        Pattern p;

        for (int i = 0; i < count; i++) {
            p = list.get(i);
            d = DTW.distanceUCR(pat.dataNormalized, p.dataNormalized, MyParameters.DISTANCE_RADIUS_RETRAIN);
            if (d < min_dist) {
                min_index = i;
                min_dist = d;
            }
        }

        pat.nearestPatternIndex = min_index;
        pat.nearestDistance = min_dist;
        pat.nearestPattern = list.get(min_index);
    }

    public static float getScore(Pattern pat) {
        if (pat.nearestDistance < 2) {
            int n = pat.nearestPattern.coverageCounts[Pattern.PATTERN_TYPE_NEG];
            int x = pat.nearestPattern.coverageCounts[Pattern.PATTERN_TYPE_X];
            int p = pat.nearestPattern.coverageCounts[Pattern.PATTERN_TYPE_POS];

            return p + MyParameters.XWEIGHT / (n + p + MyParameters.XWEIGHT);
        } else {
            return -1;
        }

    }

}
