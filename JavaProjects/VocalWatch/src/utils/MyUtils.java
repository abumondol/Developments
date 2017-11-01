package utils;

import java.util.ArrayList;
// @author mm5gg

public class MyUtils {
    
    public static String[] getUniqueList(String[][] data, int column) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        int i, rowCount = data.length;
        for (i = 0; i < rowCount; i++) {
            MyUtils.addToList(list, data[i][column]);
        }

        return MyUtils.stringListToArray(list);
    }
    
    
    public static boolean isAvailableInList(ArrayList<String> list, String item) {
        int i, count = list.size();
        for (i = 0; i < count; i++) {
            if (item.equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static void addToList(ArrayList<String> list, String item) {
        int i, count = list.size();
        for (i = 0; i < count; i++) {
            if (item.equals(list.get(i))) {
                return;
            }
        }

        list.add(item);
    }

    public static String[] stringListToArray(ArrayList<String> list) {
        int i, count = list.size();
        String[] s = new String[count];
        for (i = 0; i < count; i++) {
            s[i] = list.get(i);
        }

        return s;
    }

    public static int getItemIndex(String[] list, String item) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static void printStringArray(String[] s, String countStr) {
        if (countStr != null) {
            System.out.println(countStr + s.length);
        }
        for (int i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] d = new int[len1 + 1][len2 + 1];
        int i, j, k;

        for (i = 1; i <= len1; i++) {
            for (j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    d[i][j] = min(d[i - 1][j - 1], d[i - 1][j], d[i][j - 1]);
                } else {
                    d[i][j] = 1 + min(d[i - 1][j - 1], d[i - 1][j], d[i][j - 1]);
                }
            }
        }

        return d[len1][len2];
    }

    public static int min(int a, int b, int c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }

    public static boolean isNative(String name) {        
        String[] list = MC.nativeSubjects;
        for (int i = 0; i < list.length; i++) {
            if (name.equals(list[i])) {
                return true;
            }
        }

        return false;
    }
}
