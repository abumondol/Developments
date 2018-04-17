package adviceconflict;
// @author mm5gg

import java.util.ArrayList;
import java.util.HashMap;
import utils.EditDistance;
import utils.FileUtils;
import utils.MC;
import utils.MyUtils;

public class PreProcess {

    public static HashMap getTopicHashMap(String[] topics) throws Exception {
        HashMap hm = new HashMap();
        for (int i = 0; i < topics.length; i++) {
            hm.put(topics[i], new Integer(i));
        }
        return hm;
    }

    public static String[] processAndGetTopicList(String[][][] data) throws Exception {
        ArrayList<String> topicList = new ArrayList();
        int i, j, fileCount, lineCount;
        fileCount = data.length;
        String topicString;

        String[] appNames = FileUtils.getAppNames(MC.SRC_FOLDER_PATH);

        for (i = 0; i < fileCount; i++) {
            lineCount = data[i].length;
            for (j = 0; j < lineCount; j++) {
                topicString = addTopicsToList(topicList, data[i][j][2]);
                data[i][j][2] = topicString;
                topicString = addTopicsToList(topicList, data[i][j][3]);
                data[i][j][3] = topicString;
            }
        }

        return MyUtils.arrayListToArray(topicList);

    }

    public static String addTopicsToList(ArrayList<String> list, String topicString) {
        if (topicString == null) {
            return "";
        }

        topicString.replace("\"", "");

        int i, listCount;
        String[] topics = topicString.split(";");
        String topic, newTopicString = "";
        ArrayList<String> newTopicList = new ArrayList<String>();

        for (i = 0; i < topics.length; i++) {
            topics[i] = topics[i].trim();
            if (topics[i].length() == 0) {
                continue;
            }
            MyUtils.addToListUnique(newTopicList, topics[i]);
        }

        listCount = newTopicList.size();
        for (i = 0; i < listCount; i++) {
            topic = newTopicList.get(i);
            newTopicString += topic + ";";
            MyUtils.addToListUnique(list, topic);
        }

        return newTopicString;
    }

    public static void checkDuplicateTopics(String[] topics, int distance) {
        int i, j, d, count = topics.length;
        for (i = 0; i < count - 1; i++) {
            for (j = i + 1; j < count; j++) {
                d = EditDistance.getDistance(topics[i], topics[j]);
                if (d <= distance) {
                    System.out.println(topics[i] + " - " + topics[j]);
                }
            }
        }

    }

    public static int[][][] getAdviceGrid(String[][][] data, String[] topics) throws Exception {
        int topicCount = topics.length;
        int appCount = data.length;
        int[][][] adviceGrid = new int[appCount][][];

        HashMap topicHashMap = PreProcess.getTopicHashMap(topics);

        int i, j, k, lineCount;
        String[] st;
        String topicStr, s;
        Integer intObj;

        for (i = 0; i < appCount; i++) {
            lineCount = data[i].length;
            adviceGrid[i] = new int[lineCount][topicCount];
            for (j = 0; j < lineCount; j++) {

                topicStr = data[i][j][2]; // for positive topic tags                                
                if (topicStr != null && topicStr.length() > 0) {
                    st = topicStr.split(";");
                    for (k = 0; k < st.length; k++) {
                        s = st[k].trim();
                        if (s.length() > 0) {
                            intObj = (Integer) topicHashMap.get(s);
                            //System.out.print(intObj.intValue() + ": ");
                            adviceGrid[i][j][intObj.intValue()] = 1;
                            //System.out.println(i + ", " + (j + 1) + ", " + topics[intObj] + ", 1");
                        }
                    }
                }

                topicStr = data[i][j][3]; // for negative topic tags                                
                if (topicStr != null && topicStr.length() > 0) {
                    st = topicStr.split(";");
                    for (k = 0; k < st.length; k++) {
                        s = st[k].trim();
                        if (s.length() > 0) {
                            intObj = (Integer) topicHashMap.get(s);
                            //System.out.print(intObj.intValue() + ": ");
                            adviceGrid[i][j][intObj.intValue()] = -1;
                            //System.out.println(i + ", " + (j + 1) + ", " + topics[intObj] + ", -1");
                        }
                    }
                }

            }

        }

        return adviceGrid;
    }

    public static int[][] getWordOccuranceAdviceList(String[][][] data, String word, int column) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] appNames = FileUtils.getAppNames(MC.SRC_FOLDER_PATH);
        int i, j, lineCount, appCount;
        appCount = data.length;
        ArrayList<int[]> list = new ArrayList<int[]>();
        int[] res;

        for (i = 0; i < appCount; i++) {
            lineCount = data[i].length;
            for (j = 0; j < lineCount; j++) {
                if (data[i][j][column] != null && data[i][j][column].contains(word)) {
                    res = new int[2];
                    res[0] = i;
                    res[1] = j;
                    list.add(res);
                }
            }
        }

        int count = list.size();
        int[][] result = new int[count][];

        for (i = 0; i < count; i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    public static void replaceWordOccuranceInAdviceList(String[][][] data, String word1, String word2, int column) throws Exception {
        int i, j, lineCount, appCount;
        appCount = data.length;

        for (i = 0; i < appCount; i++) {
            lineCount = data[i].length;
            for (j = 0; j < lineCount; j++) {
                if (data[i][j][column] != null) {
                    data[i][j][column] = data[i][j][column].replace(word1, word2);
                }
            }
        }

    }

    public static void correctTopics(String[][][] data, String[][] topicList) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        int i, j, k;
        String newStr;
        for (i = 0; i < topicList.length; i++) {
            if (topicList[i].length >= 2 && topicList[i][1] != null && topicList[i][1].trim().length() > 0) {
                newStr = topicList[i][1].trim();
                if (newStr.equals("x")) {
                    replaceWordOccuranceInAdviceList(data, topicList[i][0] + ";", "", 2);
                    replaceWordOccuranceInAdviceList(data, topicList[i][0] + ";", "", 3);
                } else {
                    replaceWordOccuranceInAdviceList(data, topicList[i][0] + ";", newStr + ";", 2);
                    replaceWordOccuranceInAdviceList(data, topicList[i][0] + ";", newStr + ";", 3);
                }
            }
        }


    }
}
