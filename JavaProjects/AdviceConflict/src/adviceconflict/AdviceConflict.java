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
            /*String[][] catItems = new String[2][];
            String[][][] catPairs = Prescription.getPossiblePair(catItems);
            FileUtils.writeStringArray2DToFile(MC.DEST_FOLDER_PATH, "pair_drug_drug.csv", catPairs[0]);
            FileUtils.writeStringArray2DToFile(MC.DEST_FOLDER_PATH, "pair_dis_dis.csv", catPairs[1]);
            FileUtils.writeStringArray2DToFile(MC.DEST_FOLDER_PATH, "pair_drug_dis.csv", catPairs[2]);
            FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "drug_items.csv", catItems[0]);
            FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "disease_items.csv", catItems[1]);*/
            
            
            
            topicListAndConflictFinder();
            //ConflictStat.findConflctStat();

        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }

    public static void topicListAndConflictFinder() throws Exception {
        boolean flag = true;
        String[][][] data = FileUtils.readAppFiles();

        String[] topics;
        topics = PreProcess.processAndGetTopicList(data);
        
        
        /*FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "TopicList.csv", topics);
        if (flag) {
            return;
        }*/

        String[][] topicCorrections = FileUtils.readCSV(MC.DEST_FOLDER_PATH + "\\" + "TopicListCorrections.csv", true);
        PreProcess.correctTopics(data, topicCorrections);
        topics = PreProcess.processAndGetTopicList(data);
        System.out.println("Topic Count: " + topics.length);
        FileUtils.writeStringArrayToFile(MC.DEST_FOLDER_PATH, "TopicListNew.csv", topics);

        /*if (flag) {
            return;
        }*/
        
        
        //PreProcess.checkDuplicateTopics(topics, 1);
        String[] label = {"disease","drug"};
        //String[][] comboData = MyUtils.combineList(data, label);
        int[][][] adviceGrid = PreProcess.getAdviceGrid(data, topics);
        System.out.println("Advice Grid Size: "+adviceGrid.length+","+adviceGrid[0][0].length);
        String intraAppConflicts = ConflictFinderFilter.getIntraAppConflict(data, topics, adviceGrid);
        String interAppConflicts = ConflictFinderFilter.getInterAppConflict(data, topics, adviceGrid);
        FileUtils.writeFile(MC.DEST_FOLDER_PATH, "IntraCategoryConflictsFiltered.csv", intraAppConflicts);
        FileUtils.writeFile(MC.DEST_FOLDER_PATH, "InterCategoryConflictsFiltered.csv", interAppConflicts);

        //FileUtils.writeAppData(data);
//            for(int i=0;i<MC.FILE_NAMES.length;i++){
//                FileUtils.writeFile(MC.DEST_FOLDER_PATH, "grid-"+MC.FILE_NAMES[i]+".csv", MyUtils.intArrToCSV(adviceGrid[i]));
//            }
    }

}
