package eating.analysis.dtw;

import bite_train_test.ReTrainingAndTest;
import bite_train_test.Testing;
import bite_train_test.Training;
import constants_config.MyConstants;
import data_processing.DataManagerSteven;
import data_processing.DataManagerUVA;
import entities.Pattern;
import java.util.ArrayList;
import pattern.PatternUtils;

public class EatingAnalysisDTW {

    public static void main(String[] args) {
        try {                        
            //readAllData();
            //DataManagerUVA.printAnnotStat();
            //DataManagerSteven.printAnnotStat();
            
            int[] total = new int[6];         
            ArrayList<Pattern> patListTrain = Training.getTrainPatternsSelected(MyConstants.UVA_STEVEN);
            PatternUtils.printPatternListStat(patListTrain, false);
            ReTrainingAndTest.testRetrainByStevenFree(patListTrain);
            //Testing.testDataset(patListTrain, MyConstants.DATAET_ID_STEVEN_FREE, total);
            
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
