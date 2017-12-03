/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nursingactivity;

import java.io.File;
import java.nio.file.Files;
import myutils.MyArrayUtils;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class ProcessLabels {

    public static void process_labels() throws Exception {
        String path = "C:\\Users\\mm5gg\\Box Sync\\PublicData\\nursing\\labelled\\Labelled\\";
        File f = new File(path + "labels");
        File[] flist = f.listFiles();

        for (int i = 0; i < flist.length; i++) {
            String[][] d = MyFileUtils.readCSV(flist[i], false);
            System.out.println(flist[i]);
            System.out.println("Size: " + d.length + ", " + d[0].length);
            String[][] res = new String[d.length][3];
            for (int j = 0; j < d.length; j++) {
                if(d[j][0].trim().length()==0)
                    res[j][0] = "0";
                else
                    res[j][0] = d[j][0].trim();
                
                res[j][1] = process_time(d[j][2]);                            
                res[j][2] = process_time(d[j][3]);                            
            }
            
            MyFileUtils.writeToCSVFile(path+"labels_processed\\"+flist[i].getName(), res, null);
        }
    }

    public static String process_time(String str){
        
        String[] sarray= str.split(" ")[1].trim().split(":");
        int h = Integer.parseInt(sarray[0]);
        int m = Integer.parseInt(sarray[1]);
        int s = Integer.parseInt(sarray[2]);        
        return ""+(h*60*60+m*60+s);
    }
    
    
}
