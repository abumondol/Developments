package bite_train_test;

import constants_config.MyConstants;
import data_processing.DataManagerSteven;
import data_processing.DataManagerUVA;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import pattern.PatternUtils;

/**
 *
 * @author mm5gg
 */
public class Training {

    public static ArrayList<Pattern> getTrainPatternsSelected(int type) throws Exception {
        ArrayList<Pattern> patList;
        String fileName;

        if (type == MyConstants.UVA) {
            fileName = "training_patterns_uva_lab.ser";
        } else if (type == MyConstants.STEVEN) {
            fileName = "training_patterns_steven_lab.ser";
        } else {
            fileName = "training_patterns_both_lab.ser";
        }

        patList = PatternUtils.getSerializedPatterns(fileName);
        if (patList != null) {
            return patList;
        }
        
        if (type == MyConstants.UVA) {            
            patList = getTrainPatterns(DataManagerUVA.getLabData());            
            
        } else if (type == MyConstants.STEVEN) {            
            patList = getTrainPatterns(DataManagerSteven.getLabData());
            
        } else {            
            patList = getTrainPatterns(DataManagerUVA.getLabData());                        
            patList.addAll(getTrainPatterns(DataManagerSteven.getLabData()));            
        }

        patList = TrainingUtils.selectPatterns(patList);
        PatternUtils.saveSerializedPatterns(patList, fileName);
        return patList;
    }

    public static ArrayList<Pattern> getTrainPatterns(SessionData[][] sds) throws Exception {

        ArrayList<Pattern> patList = new ArrayList<>();
        ArrayList<Pattern> pList;

        System.out.println("Generating patterns... ");
        for (int i = 0; i < sds.length; i++) {
            for (int j = 0; j < sds[i].length; j++) {
                pList = TrainingUtils.getPatternsWithLabels(sds[i][j]);
                patList.addAll(pList);
            }
        }

        System.out.println("Total patterns generated: " + patList.size());
        return patList;
    }

}
