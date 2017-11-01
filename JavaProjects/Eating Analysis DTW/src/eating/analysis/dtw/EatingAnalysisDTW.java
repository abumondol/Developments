package eating.analysis.dtw;

import bite_train_test.TrainingUVALab;
import data_processing.DataManagerSteven;
import data_processing.DataManagerUVA;
import entities.Pattern;
import java.util.ArrayList;

public class EatingAnalysisDTW {

    public static void main(String[] args) {
        try {                        
            //readAllData();
            //DataManagerUVA.printAnnotStat();
            //DataManagerSteven.printAnnotStat();
            
                     
            //ArrayList<Pattern> patListTrain = TrainingUVALab.getTrainPatterns();
            //patListTrain = TrainingUVALab.retrainByUVANegatives(patListTrain);
            //PatternUtils.printPatternListStat(patListTrain, false);
                        
            //TestingUVA.testLabData(patListTrain);
            //TestingUVA.testNonEatData(patListTrain);            
            //TestingStevenTech.testFreeData(patListTrain, 2);            
            //MealManager.TestMealStevenTech(1);
            
            //DataManagerSteven.writeFreeDataToTextFile();
            //DataManagerSteven.writeLabDataToTextFile();
            //DataManagerStevenTech.writeFreeDataResultLeftToTextFile();            
            
            //DataManagerStevenTech.writeFreeResultsToTextFile();            
            

        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }
    
    public static void readAllData() throws Exception{
        DataManagerUVA.getLabData();
        DataManagerUVA.getFreeData();
        DataManagerSteven.getLabData();
        DataManagerSteven.getFreeData();
        //DataManagerSteven.getFreeData(true);
            
    }

}
