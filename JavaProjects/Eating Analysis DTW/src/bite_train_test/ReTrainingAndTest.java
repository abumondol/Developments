package bite_train_test;

//@author mm5gg
import data_processing.DataManagerSteven;
import entities.Pattern;
import entities.SessionData;
import java.util.ArrayList;
import pattern.PatternUtils;

public class ReTrainingAndTest {

    public static void testRetrainByStevenFree(ArrayList<Pattern> patListTrain) throws Exception {
        SessionData[][] sdss = DataManagerSteven.getFreeData();
        System.out.println("Data read done. Subject count: "+sdss.length);
                
        ArrayList<Pattern> patListOriginal = PatternUtils.copyPatternList(patListTrain);
        
        
        for(int i=0;i<sdss.length;i++){            
            int[] total = new int[6];
            patListTrain = ReTrainingUtils.reTrainingExcludingSubejct(i, sdss, patListOriginal);
            Testing.testSubject(patListTrain, sdss[i], total);
        }        
        
        //Testing.testDataset(patListTrain, 1, total);
        //Testing.testDataset(patListTrain, 2, total);
        //Testing.testDataset(patListTrain, 101, total);

    }

}
