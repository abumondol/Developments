package adviceconflict;
// @author mm5gg

import java.util.HashMap;
import utils.FileUtils;
import utils.MC;
import utils.MyUtils;

public class AdviceConflict {

    public static void main(String[] args) {
        boolean flag = true;
        try {
            //topicListAndConflictFinder();
            ConflictStat.findConflctStat();

        } catch (Exception ex) {
            System.out.println(ex.toString());
            //ex.printStackTrace();
        }
    }
    
    public static void topicListAndConflictFinder() throws Exception{
        String[][][] data = FileUtils.readAppFiles();

            String[] topics;
            topics = PreProcess.processAndGetTopicList(data);            
//            FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "TopicList.csv", topics);            
//            if(flag)
//                return;
            
            String[][] topicCorrections = FileUtils.readCSV(MC.DEST_FOLDER_PATH + "\\" + "TopicListCorrections.csv", true);
            PreProcess.correctTopics(data, topicCorrections);
            topics = PreProcess.processAndGetTopicList(data);
            System.out.println("Topic Count: " + topics.length);
            FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "TopicListNew.csv", topics);

            
            //PreProcess.checkDuplicateTopics(topics, 1);                        

            int[][][] adviceGrid = PreProcess.getAdviceGrid(data, topics);
            System.out.println(adviceGrid.length);
            String intraAppConflicts = ConflictFinder.getIntraAppConflict(data, topics, adviceGrid);
            String interAppConflicts = ConflictFinder.getInterAppConflict(data, topics, adviceGrid);
            FileUtils.writeFile(MC.DEST_FOLDER_PATH, "IntraAppConflicts.csv", intraAppConflicts);
            FileUtils.writeFile(MC.DEST_FOLDER_PATH, "InterAppConflicts.csv", interAppConflicts);
            
            //FileUtils.writeAppData(data);

//            for(int i=0;i<MC.FILE_NAMES.length;i++){
//                FileUtils.writeFile(MC.DEST_FOLDER_PATH, "grid-"+MC.FILE_NAMES[i]+".csv", MyUtils.intArrToCSV(adviceGrid[i]));
//            }

    }
    
    
}
