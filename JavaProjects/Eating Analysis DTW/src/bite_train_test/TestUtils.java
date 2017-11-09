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
public class TestUtils {

    public static Pattern[][][] test(SessionData[][] sdss, ArrayList<Pattern> patListTrain) throws Exception {
        Pattern[][][] pats = new Pattern[sdss.length][][];
        for(int i=0;i<sdss.length;i++){
            pats[i] = testSubject(sdss[i], patListTrain);
        }
        
        return pats;
    }
    
    public static Pattern[][] testSubject(SessionData[] sds, ArrayList<Pattern> patListTrain) throws Exception {
        Pattern[][] pats = new Pattern[sds.length][];
        for(int i=0;i<sds.length;i++){
            pats[i] = testSession(sds[i], patListTrain);
        }        
        return pats;
    }
    
    public static Pattern[] testSession(SessionData sd, ArrayList<Pattern> patListTrain) throws Exception {
        int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(sd.accelData);
        if (minIndices == null) {
            return null;
        }

        Pattern[] pats = PatternManager.getPatterns(sd.accelTime, sd.accelData, minIndices);
        
        pats = test(pats, patListTrain);
        return pats;
    }

    public static Pattern[] test(Pattern[] pats, ArrayList<Pattern> patListTrain) throws Exception {
        for (int i = 0; i < pats.length; i++) {
            findNearestPattern(pats[i], patListTrain);
            getScore(pats[i]);
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
            d = DTW.distanceUCR(pat.dataNormalized, p.dataNormalized, min_dist);
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
        float d = pat.nearestDistance;
        Pattern p = pat.nearestPattern;
        int binIndex = PatternUtils.distanceToBinIndex(d);
        if (binIndex >= MyParameters.BIN_COUNT) {
            return -1;
        }
        if (binIndex == 0) {
            return p.label;
        }

        int i, pCount = 0, nCount = 0, xCount = 0;
        for (i = 0; i < binIndex; i++) {
            nCount += p.coverageCountBins[Pattern.PATTERN_TYPE_NEG][i];
            pCount += p.coverageCountBins[Pattern.PATTERN_TYPE_POS][i];
            xCount += p.coverageCountBins[Pattern.PATTERN_TYPE_X][i];
        }
        
        if(p.label==Pattern.PATTERN_TYPE_NEG)
            nCount++;
        else if(p.label==Pattern.PATTERN_TYPE_POS)
            pCount++;
        else
            xCount++;

        float w = pCount + xCount - nCount;
        double score = 1 / (1 + Math.exp(-w));
        pat.biteScore = (float) score;
        return pat.biteScore;
        
    }

}
