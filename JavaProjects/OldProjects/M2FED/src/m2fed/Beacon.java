/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m2fed;

import java.io.File;
import static m2fed.AnnotationProcess.getActivityCode;
import static m2fed.AnnotationProcess.timeStringToFloat;
import m2fed.myutils.FileUtils;

/**
 *
 * @author mm5gg
 */
public class Beacon {
    
    public static void processBeacon() throws Exception {
        String srcFolder = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\M2FED\\Beacon\\data";
        String destFolder = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\M2FED\\Beacon\\data_2";
        
        File folder = new File(srcFolder);
        File[] files = folder.listFiles();
        boolean b1, b2, b3;
        for (int i = 0; i < files.length; i++) {
            StringBuilder sb = new StringBuilder();
            String[][] data = FileUtils.readCSV(files[i], true);                                    

            for (int j = 0; j < data.length; j++) {
                b1 = data[j][2].equals("C8:86:52:8B:6C:AE");
                b2 = data[j][2].equals("D6:43:46:17:64:70");
                b3 = data[j][2].equals("F1:6D:3A:C9:92:FE");
                if( b1 || b2 || b3){
                    sb.append(data[j][0]+","+data[j][1]+",");                    
                    if(b1)
                        sb.append(1);
                    else if(b2)
                        sb.append(2);
                    else 
                        sb.append(3);
                    
                    sb.append(","+data[j][3]+","+data[j][4]+"\n");
                    
                }
            }

            FileUtils.writeToFile(destFolder + "\\" + files[i].getName(), sb.toString());
        }

    }
    
    
    
}
