/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class FileUtils {

    public static String[][] readCSV(String filePath, boolean includeFirstLine) throws Exception {
        return readCSV(new File(filePath), includeFirstLine);
    }

    public static String[][] readCSV(File file, boolean includeFirstLine) throws Exception {
        ArrayList<String[]> list = new ArrayList<String[]>();
        String line;
        int i,j,k,len, rowCount;
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while((line=br.readLine())!=null){
            list.add(line.split(","));
        }
        
        if(!includeFirstLine){
            list.remove(0);
        }
        
        rowCount = list.size();
        String[][] csv = new String[rowCount][];        
        for(i=0;i<rowCount;i++){
            csv[i] = list.get(i);
        }
        
        return csv;
    }
    
    
}
