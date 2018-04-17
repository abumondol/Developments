/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m2fed;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import m2fed.myutils.FileUtils;
import static m2fed.myutils.FileUtils.writeToFile;

/**
 *
 * @author mm5gg
 */
public class CombineDataFiles {

    public static void combineFilesEatingData() throws Exception {
        String srcFolder = "mydata\\fragmented_files";
        String destFolder = "mydata\\combined_files";

        File src = new File(srcFolder);
        File[] frag_files = src.listFiles();
        System.out.println("Total file count: " + frag_files.length);
        ArrayList<File> fileList;

        int s, i;
        for (s = 1; s <= 5; s++) {
            fileList = new ArrayList();
            for (i = 0; i < frag_files.length; i++) {
                if (frag_files[i].getName().contains("subject" + s) && frag_files[i].getName().contains("right")) {
                    fileList.add(frag_files[i]);                    
                }
            }
            
            if(fileList.size()>0)
                combineAndSaveFiles(fileList, destFolder);

            
            fileList = new ArrayList();
            for (i = 0; i < frag_files.length; i++) {
                if (frag_files[i].getName().contains("subject" + s) && frag_files[i].getName().contains("left")) {
                    fileList.add(frag_files[i]);                    
                }
            }
            
            if(fileList.size()>0)            
                combineAndSaveFiles(fileList, destFolder);
        }

    }

    public static void combineAndSaveFiles(ArrayList<File> list, String destFolder) throws Exception {
        int i, j, k, count, len1, nrows = 0;
        count = list.size();
        String[][][] data = new String[count][][];
        long nanoFactor = 1000*1000*1000;
        String destFileName = getCombinedFileName(list.get(0).getName());
        System.out.println(destFileName);
        StringBuilder sb = new StringBuilder();

        for (i = 0; i < count; i++) {
            data[i] = FileUtils.readCSV(list.get(i), true);
            sb.append(arrayToStringBuilder(data[i], null));
            
            nrows += data[i].length;            
            System.out.println((i+1)+ ": "+data[i].length);
            
            if(i>1){ // check file sequence is correct
                len1 = data[i-1].length;
                long t1 = Long.parseLong(data[i-1][len1-1][0])/nanoFactor;
                long t2 = Long.parseLong(data[i][0][0])/nanoFactor;
                
                if(t1>t2 || t2-t1>1){
                    System.out.print("Error in file order: ");
                    System.out.print(t1+", "+list.get(i-1).getName());
                    System.out.print(t2+", "+list.get(i).getName());
                    return;
                }
            }            
        }        
        
        System.out.println("Total: "+nrows);        
        FileUtils.writeToFile(destFolder+"\\"+destFileName, sb.toString());
        if(FileUtils.readCSV(destFolder+"\\"+destFileName, true).length != nrows){
            System.out.println("Row count mismatch: "+destFileName);
        }
        
    }

    public static String getCombinedFileName(String oneFileName) {
        String[] frags = oneFileName.split("-");
        String combName = "";

        for (int i = 0; i < frags.length - 1; i++) {
            combName += frags[i] + "-";
        }

        combName += "combined_files.wada";
        return combName;
    }
    
    
    public static StringBuilder arrayToStringBuilder(String[][] data, String header) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (header != null) {
            sb.append(header + "\n");
        }

        int i, j;
        for (i = 0; i < data.length; i++) {
            for (j = 0; j < data[i].length; j++) {
                sb.append(data[i][j]);
                if (j == data[i].length - 1) {
                    sb.append("\n");
                } else {
                    sb.append(",");
                }
            }
        }

        return sb;

    }

}
