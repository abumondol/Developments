package nursingactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import myutils.MyArrayUtils;
import myutils.MyFileUtils;
import myutils.MyPrintUtils;

/**
 *
 * @author mm5gg
 */
public class DataSummary {

    public static String folderPathRoot = "C:\\Users\\mm5gg\\Box Sync\\Public Data\\nursing";
    public static String folderPathLabels = "C:\\Users\\mm5gg\\Box Sync\\Public Data\\nursing\\labelled\\Labelled\\labels";
    public static String folderPathSensors = "C:\\Users\\mm5gg\\Box Sync\\Public Data\\nursing\\labelled\\Labelled\\sensors";

    public static void analyzeNurseIDs(int type) throws Exception {
        File folderLabels;
        if (type == 1) {
            folderLabels = new File(folderPathLabels);
        } else {
            folderLabels = new File(folderPathSensors);
        }

        File[] files = folderLabels.listFiles();
        int fileCount = files.length;
        System.out.println("Labels File Count: " + fileCount);

        String[] fileNames = new String[fileCount];
        for (int i = 0; i < fileCount; i++) {
            fileNames[i] = files[i].getName();
        }

        int id, prevId = 0;
        ArrayList<Integer> nurseIdList = new ArrayList<>();
        String[] str;
        for (int i = 0; i < fileCount; i++) {
            id = Integer.parseInt(fileNames[i].substring(1, 4));
            if (id != prevId) {
                nurseIdList.add(id);
                prevId = id;
            }
        }

        int[] nurseIds = MyArrayUtils.intListToArray(nurseIdList);
        int nurseCount = nurseIds.length;
        System.out.println("Nurse Count: " + nurseCount);
        MyPrintUtils.print(nurseIds, "IDs: ");

    }

    public static void analyzeActivities() throws Exception {
        File folderLabels;
        folderLabels = new File(folderPathLabels);

        File[] files = folderLabels.listFiles();
        int fileCount = files.length;
        System.out.println("Labels File Count: " + fileCount);

        String[] fileNames = new String[fileCount];
        String[][][] labelData = new String[fileCount][][];
        for (int i = 0; i < fileCount; i++) {
            fileNames[i] = files[i].getName();
            labelData[i] = MyFileUtils.readCSV(files[i], false);
        }

        int hwCount = 0;
        Set<String> acts = new HashSet<>();
        Set<String> actIds = new HashSet<>();
        Set<String> actsWithId = new HashSet<>();

        for (int i = 0; i < fileCount; i++) {
            for (int j = 0; j < labelData[i].length; j++) {
                if(labelData[i][j][1].contains("UP")||labelData[i][j][1].contains("Sat")){
                    //System.out.println("English letters found");
                    //System.out.println(files[i].getName()+":: "+j);
                    //return;
                }
                
                acts.add(labelData[i][j][1]);
                actIds.add(labelData[i][j][0]);
                actsWithId.add(labelData[i][j][0] + "," + labelData[i][j][1]);
                if (labelData[i][j][0].equals("39")) {
                    hwCount++;
                }
            }
        }

        System.out.println("Handwash count: " + hwCount);
        System.out.println("Activity count: " + acts.size());
        System.out.println("Activity Id count: " + actIds.size());
        System.out.println("Activity with Id count: " + actsWithId.size());

        String[] actStr = new String[acts.size()];
        int i = 0, j=1;
        
        for (String s : actsWithId) {
            if(s.charAt(1)==',')
                s = "0"+s;            
            if(s.charAt(2)!=','){
                if(j<10)
                    s = "x0"+j+s;
                else
                    s = "x"+j+s;
                j++;
            }
            
            actStr[i] = s;
            i++;
                        
        }
        Arrays.sort(actStr);
                
        for (String s : actStr) {
            System.out.print("\"" + s + "\", ");
        }
        
    }

}
