package bite_train_test;

import pattern.PatternManager;
import pattern.PotentialIndexFinder;
import data_processing.DataManager;
import data_processing.DataManagerUVA;
import constants_config.MyParameters;
import distance_finder.DTW;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import myutils.MyFileUtils;
import pattern.PatternUtils;

/**
 *
 * @author mm5gg
 */
public class TrainingUVALab {

    public static ArrayList<Pattern> getTrainPatterns() throws Exception {
        String fileName = "training_patterns_uva_lab.ser";
        if (DataManager.isSerFileExists(fileName)) {
            return (ArrayList<Pattern>) MyFileUtils.deSerializeFile(DataManager.serFilePath + fileName);
        }

        System.out.println("generating all pattern ...");
        ArrayList<Pattern> patList = getPatternsAll();
        System.out.println("Selecting pattern ...");
        patList = Training.selectPatterns(patList);
        PatternUtils.printPatternListStat(patList, true);        

        MyFileUtils.serializeFile(patList, DataManager.serFilePath + fileName);
        return patList;
    }

    //********** ReTrain by UVA Negatives
    public static ArrayList<Pattern> retrainByUVANegatives(ArrayList<Pattern> patList) throws Exception {
        patList = ReTraining.updateTrainingList(-1, DataManagerUVA.getFreeData(), patList);
        for (int i = patList.size() - 1; i >= 0; i--) {
            if (patList.get(i).totalCoverageCount() < 2) {
                patList.remove(i);
            }
        }

        PatternUtils.printPatternListStat(patList, true);
        return patList;
    }

    // **************** get Patterns **********************    
    public static ArrayList<Pattern> getPatternsAll() throws Exception {
        SessionData[] sds = DataManagerUVA.getLabData();
        ArrayList<Pattern> patList = new ArrayList<Pattern>();

        for (int i = 0; i < DataManagerUVA.SESSION_COUNT_LAB; i++) {
            int[] minIndices = PotentialIndexFinder.findMinPointIndicesFiltered(sds[i].accelData);
            //MyArrayUtils.printArray(minIndices);
            int[] labels = Training.labelMinPoints(sds[i].annots, minIndices);
            Pattern[] pats = PatternManager.getPatterns(sds[i].accelTime, sds[i].accelData, minIndices);

            for (int j = 0; j < labels.length; j++) {
                if (labels[j] >= 0 && labels[j] < 400) {
                    if (labels[j] == 0) {
                        pats[j].label = Pattern.PATTERN_TYPE_NEG;
                    } else {
                        pats[j].label = Pattern.PATTERN_TYPE_POS;
                    }

                    pats[j].increaseCoverageCount(pats[j].label);
                    patList.add(pats[j]);
                }
            }

        }

        return patList;
    }

    

}
