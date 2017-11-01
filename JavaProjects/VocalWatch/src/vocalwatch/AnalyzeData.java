/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vocalwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import utils.FileUtils;
import utils.MyUtils;

/**
 *
 * @author mm5gg
 */
public class AnalyzeData {

    public static void lopo() throws Exception {
        int i, j;
        String srcFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\all_errors.csv";
        //String destFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data";
        String[][] data = FileUtils.readCSV(srcFilePath, false);
        for (i = 0; i < data.length; i++) {
            data[i][5] = data[i][5].toLowerCase().trim();
            data[i][6] = data[i][6].toLowerCase().trim();
        }

        String[] subjects = MyUtils.getUniqueList(data, 0);

        Dictionary dict;
        String[][] trainData;
        String[][] testData;
        boolean[] correctedList;
        int errorCount;

        System.out.println("Total error: " + data.length);
        for (i = 0; i < subjects.length; i++) {
            trainData = getSubjectData(data, subjects[i], true);
            testData = getSubjectData(data, subjects[i], false);
            errorCount = getErrorCount(trainData, testData);
            System.out.println(testData[0][0] + ", " + testData.length + "," + errorCount);

        }
    }

    public static void personalized() throws Exception {
        int i, j, k, m, n;
        String srcFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\all_errors.csv";
        //String destFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data";
        String[][] data = FileUtils.readCSV(srcFilePath, false);
        for (i = 0; i < data.length; i++) {
            data[i][5] = data[i][5].toLowerCase().trim();
            data[i][6] = data[i][6].toLowerCase().trim();
        }

        String[] subjects = MyUtils.getUniqueList(data, 0);

        Dictionary dict;
        String[][] subData;
        String[] uniqueWords;
        ArrayList<String[]> trainData;
        ArrayList<String[]> testData;

        int errorCount, trainCount, traiIndex;

        for (i = 0; i < subjects.length; i++) {
            subData = getSubjectData(data, subjects[i], false);
            System.out.print(subjects[i] + "," + subData.length + ",");
            
            ArrayList<String> exploredWords = new ArrayList<String>();            
            for (trainCount = 1; trainCount <= 26; trainCount++) {                
                
                for (j = 0; j < subData.length; j++) {
                    if(!MyUtils.isAvailableInList(exploredWords, subData[j][5])){
                        exploredWords.add(subData[j][5]);
                        break;                        
                    }
                }
               
                trainData = new ArrayList<String[]>();
                testData = new ArrayList<String[]>();
                for(k=0;k<=j;k++){ 
                    trainData.add(subData[k]);
                }
                
                for(k=j+1;j<subData.length;j++){
                    testData.add(subData[j]);
                }

                errorCount = getErrorCount(trainData, testData);
                System.out.print(errorCount+",");
                if(errorCount==0)
                    break;
            }            
            System.out.println();
        }

    }
    
    public static void personalized_lopo() throws Exception {
        int i, j, k, m, n;
        String srcFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\all_errors.csv";
        //String destFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data";
        String[][] data = FileUtils.readCSV(srcFilePath, false);
        for (i = 0; i < data.length; i++) {
            data[i][5] = data[i][5].toLowerCase().trim();
            data[i][6] = data[i][6].toLowerCase().trim();
        }

        String[] subjects = MyUtils.getUniqueList(data, 0);

        Dictionary dict;
        String[][] subData;
        String[][] otherSubData;
        
        String[] uniqueWords;
        ArrayList<String[]> trainData;
        ArrayList<String[]> testData;

        int errorCount, trainCount, traiIndex;

        for (i = 0; i < subjects.length; i++) {
            otherSubData = getSubjectData(data, subjects[i], false);
            dict = getDictionary(otherSubData);            
            
            subData = getSubjectData(data, subjects[i], false);
            System.out.print(subjects[i] + "," + subData.length + ",");
            
            ArrayList<String> exploredWords = new ArrayList<String>();            
            for (trainCount = 1; trainCount <= 20; trainCount++) {                
                
                for (j = 0; j < subData.length; j++) {
                    if(!MyUtils.isAvailableInList(exploredWords, subData[j][5])){
                        exploredWords.add(subData[j][5]);
                        break;                        
                    }
                }
               
                trainData = new ArrayList<String[]>();
                testData = new ArrayList<String[]>();
                for(k=0;k<=j;k++){ 
                    trainData.add(subData[k]);
                }
                
                for(k=j+1;j<subData.length;j++){
                    testData.add(subData[j]);
                }

                errorCount = getErrorCount(trainData, testData);
                System.out.print(errorCount+",");
                if(errorCount==0)
                    break;
            }            
            System.out.println();
        }

    }

    public static int getErrorCount(String[][] trainData, String[][] testData) {
        int errorCount = 0;
        Dictionary dict = getDictionary(trainData);
        for (int i = 0; i < testData.length; i++) {
            if (!dict.search(testData[i][5])) {
                errorCount++;
            }
        }

        return errorCount;
    }

    public static int getErrorCount(ArrayList<String[]> trainData, ArrayList<String[]> testData) {
        int errorCount = 0, testCount = testData.size();
        Dictionary dict = getDictionary(trainData);
        String[] s;
        for (int i = 0; i < testCount; i++) {
            s = testData.get(i);
            if (!dict.search(s[5])) {
                errorCount++;
            }
        }
        return errorCount;
    }

    public static Dictionary getDictionary(String[][] data) {
        int i, dataCount = data.length;
        String word, meaning;

        Dictionary d = new Dictionary();
        for (i = 0; i < dataCount; i++) {
            word = data[i][5];
            meaning = data[i][6];
            d.addWordAndMeaning(word, meaning);
            //d.addWordAndMeaning(meaning, word);
        }
        return d;
    }

    public static Dictionary getDictionary(ArrayList<String[]> data) {
        int i, dataCount = data.size();
        String word, meaning;
        Dictionary d = new Dictionary();
        String[] s;
        for (i = 0; i < dataCount; i++) {
            s = data.get(i);
            word = s[5];
            meaning = s[6];
            d.addWordAndMeaning(word, meaning);
        }
        return d;
    }

    public static String[][] getSubjectData(String[][] data, String subject, boolean othersData) {
        ArrayList<String[]> list = new ArrayList<String[]>();

        for (int i = 0; i < data.length; i++) {
            if (!othersData && subject.equals(data[i][0])) {
                list.add(data[i]);
            } else if (othersData && !subject.equals(data[i][0])) {
                list.add(data[i]);
            }
        }

        int count = list.size();
        String[][] res = new String[count][];

        for (int i = 0; i < count; i++) {
            res[i] = list.get(i);
        }

        return res;
    }

    
}
