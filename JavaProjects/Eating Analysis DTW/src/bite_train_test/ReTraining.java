package bite_train_test;

import constants_config.MyParameters;
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
public class ReTraining {

    public static ArrayList<Pattern> updateTrainingList(int excludeSession, SessionData[] sds, ArrayList<Pattern> patListTrain) throws Exception {
        patListTrain = PatternUtils.copyPatternList(patListTrain);
        for (int i = 0; i < sds.length; i++) {
            if (i == excludeSession) {
                continue;
            }

            //int[][] mealIndices = MyArrayUtils.floatToIntArray(sds[i].annots);
            patListTrain = updateTrainingList(sds[i].accelTime, sds[i].accelData, sds[i].meals, patListTrain);
            System.out.print(" >> Retraining done by sesson " + i + " :: ");
            PatternUtils.printPatternListStat(patListTrain, false);
        }

        return patListTrain;
    }

    public static ArrayList<Pattern> updateTrainingList(float[] accelTime, float[][] accelData, int[][] mealIndices, ArrayList<Pattern> patListTrain) throws Exception {
        int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(accelData);
        if (minIndices == null) {
            return null;
        }
        
        Pattern[] pats = PatternManager.getPatterns(accelTime, accelData, minIndices);
        if (mealIndices != null) {
            for (int i = 0; i < pats.length; i++) {
                for (int j = 0; j < mealIndices.length; j++) {
                    if (pats[i].minPointIndex >= mealIndices[j][0] && pats[i].minPointIndex <= mealIndices[j][1]) {
                        pats[i].label = 1;
                    }
                }
            }
        }

        for (int i = 0; i < pats.length; i++) {
            patListTrain = updateTrainingList(pats[i], patListTrain);
        }
        return patListTrain;

    }

    public static ArrayList<Pattern> updateTrainingList(Pattern pat, ArrayList<Pattern> patListTrain) {
        Testing.findNearestPattern(pat, patListTrain);
        
        if(pat.nearestDistance > MyParameters.DISTANCE_RADIUS_RETRAIN){
            pat.coverageCounts[pat.label] = 1;
            patListTrain.add(pat);
        }else{
            patListTrain.get(pat.nearestPatternIndex).coverageCounts[pat.label]+=1;
        }
        
        return patListTrain;
    }

}
