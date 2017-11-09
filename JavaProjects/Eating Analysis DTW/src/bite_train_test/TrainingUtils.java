package bite_train_test;

import constants_config.MyParameters;
import distance_finder.DTW;
import entities.Pattern;
import entities.TrainPattern;
import entities.SessionData;
import java.util.ArrayList;
import pattern.PatternManager;
import pattern.PotentialIndexFinder;

//@author mm5gg

public class TrainingUtils {

    public static ArrayList<Pattern> getPatternsWithLabels(SessionData sd) throws Exception {
        ArrayList<Pattern> patList = new ArrayList<>();
        int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(sd.accelData);        
        int[] labels = labelMinPoints(sd.annots, minIndices);
        Pattern[] pats = PatternManager.getPatterns(sd.accelTime, sd.accelData, minIndices);

        for (int j = 0; j < labels.length; j++) {
            if (labels[j] == 0 || labels[j] == 1) {
                if (labels[j] == 0) {
                    pats[j].label = Pattern.PATTERN_TYPE_NEG;
                } else {
                    pats[j].label = Pattern.PATTERN_TYPE_POS;
                }
                patList.add(pats[j]);
            }
        }

        return patList;

    }

    // **************** label min points **********************
    public static int[] labelMinPoints(int[][] annots, int[] minIndices) {
        int[] labels = new int[minIndices.length];

        int ix, i, j, minDistanceIndex;
        int annotCount = annots.length;

        for (i = 0; i < minIndices.length; i++) {
            ix = minIndices[i];
            minDistanceIndex = 0;
            for (j = 1; j < annotCount; j++) {
                if (Math.abs(annots[j][0] - ix) < Math.abs(annots[minDistanceIndex][0] - ix)) {
                    minDistanceIndex = j;
                }
            }

            int minDist = (int) Math.abs(annots[minDistanceIndex][0] - ix);
            if (minDist <= MyParameters.MAX_ANNOT_DISTANCE) {
                labels[i] = (int) annots[minDistanceIndex][1];
            } else if (minDist <= MyParameters.EXCLUDE_ANNOT_DISTANCE) {
                labels[i] = -1;
            } else {
                labels[i] = 0;
            }
        }

        return labels;
    }

    // **************** Select Patterns **********************     
    public static ArrayList<TrainPattern> findDistances(ArrayList<TrainPattern> trainPatList) {
        System.out.println("Finding distances...");
        int i, j, count = trainPatList.size();
        float d;
        TrainPattern tp1, tp2;

        for (i = 0; i < count; i++) {
            tp1 = trainPatList.get(i);
            for (j = i + 1; j < count; j++) {
                tp2 = trainPatList.get(j);
                d = DTW.distance(tp1.pat.dataNormalized, tp2.pat.dataNormalized, MyParameters.MAX_BIN_DISTANCE);
                if (d < MyParameters.MAX_BIN_DISTANCE) {
                    tp1.addPattternCovered(tp2, d);
                    tp2.addPattternCovered(tp1, d);
                }
            }
        }

        return trainPatList;
    }

    public static ArrayList<Pattern> selectPatterns(ArrayList<Pattern> patList) {
        ArrayList<TrainPattern> trainPatList = patternToTrainPattern(patList);
        trainPatList = findDistances(trainPatList);

        System.out.println("Selecting patterns...");
        ArrayList<Pattern> selectedList = new ArrayList<>();

        int i, j, c, maxBins, count = trainPatList.size();
        float d;
        TrainPattern tp;

        maxBins = (int) (MyParameters.TAINING_DISTANCE / MyParameters.BIN_SIZE);
        System.out.println("Pattern Size Before Filter by Min Coverage: " + count);
        for (i = count - 1; i >= 0; i--) {
            tp = trainPatList.get(i);
            if (tp.coveredDistances.size() <= 0 || tp.coveredDistances.get(0).floatValue() >= MyParameters.TAINING_DISTANCE) {
                trainPatList.remove(i);
            } else {
                c = 0;
                for (j = 0; j < maxBins; j++) {
                    c += tp.pat.coverageCountBins[0][j] + tp.pat.coverageCountBins[1][j] + tp.pat.coverageCountBins[2][j];
                }

                tp.coveredCount = c;                
            }
        }

        count = trainPatList.size();
        System.out.println("Pattern Size After Filter by Min Coverage: " + count);

        while (!trainPatList.isEmpty()) {
            count = trainPatList.size();
            System.out.println("Patlist Size: " + trainPatList.size() + ", SelectedListSize: " + selectedList.size());
            tp = trainPatList.get(0);
            for (i = 1; i < count; i++) {
                if (tp.coveredCount < trainPatList.get(i).coveredCount) {
                    tp = trainPatList.get(i);
                }
            }

            selectedList.add(tp.pat);
            trainPatList.remove(tp);

            count = tp.coveredList.size();
            for (i = 0; i < count; i++) {
                if(tp.coveredDistances.get(i).floatValue() < MyParameters.TAINING_DISTANCE)
                    trainPatList.remove(tp.coveredList.get(i));
            }
        }

        return selectedList;
    }

    public static ArrayList<Pattern> trainPatternToPattern(ArrayList<TrainPattern> trainPatList) {
        ArrayList<Pattern> patList = new ArrayList<>();
        for (int i = 0; i < trainPatList.size(); i++) {
            patList.add(trainPatList.get(i).pat);
        }

        return patList;
    }

    public static ArrayList<TrainPattern> patternToTrainPattern(ArrayList<Pattern> patList) {
        ArrayList<TrainPattern> trainPatList = new ArrayList<>();
        TrainPattern tp;
        for (int i = 0; i < patList.size(); i++) {
            tp = new TrainPattern();
            tp.pat = patList.get(i);
            trainPatList.add(tp);
        }

        return trainPatList;
    }

}
