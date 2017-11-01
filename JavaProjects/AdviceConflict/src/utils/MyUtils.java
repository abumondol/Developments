/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class MyUtils {

    public static void addToListUnique(ArrayList<String> list, String data) {

        int i, j, k, listCount = list.size();
        for (j = 0; j < listCount; j++) {
            k = data.compareTo(list.get(j));
            if (k == 0) {
                break;
            }
            if (k < 0) {
                list.add(j, data);
                break;
            }
        }

        if (j == listCount) {
            list.add(data);
        }

    }

    public static String stringArrayToString(String[] strArr) {
        StringBuilder sb = new StringBuilder();

        for (String s : strArr) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String intArrToCSV(int[][] arr) {
        int i, j, rowCount = arr.length, colCount = arr[0].length;
        StringBuilder sb = new StringBuilder();

        for (i = 0; i < rowCount; i++) {
            for (j = 0; j < colCount; j++) {
                sb.append(arr[i][j]);
                sb.append(",");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public static String[] arrayListToArray(ArrayList<String> list) {
        int count = list.size();
        String[] arr = new String[count];
        for (int i = 0; i < count; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static String[][] toLowerCaseAndremoveSpace(String[][] data) {
        int i, j, len1, len2;
        len1 = data.length;
        for (i = 0; i < len1; i++) {
            len2 = data[i].length;
            for (j = 0; j < len2; j++) {
                data[i][j] = data[i][j].toLowerCase().trim();
            }
        }

        return data;
    }

    public static String[][] combineList(String[][][] data, String[] label) {
        int i, j, k, total = 0;

        for (i = 0; i < data.length; i++) {
            total += data[i].length;
        }

        String[][] comboData = new String[total][];

        k = 0;
        for (i = 0; i < data.length; i++) {
            for (j = 0; j < data[i].length;j++) {                
                comboData[k] = addToStringArray(data[i][j], label[i]);
            }
        }
        
        return comboData;
    }

    public static String[] addToStringArray(String[] data, String d) {
        String[] res = new String[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        res[data.length] = d;
        return res;
    }
}
