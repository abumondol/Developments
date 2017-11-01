/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pattern;

import constants_config.MyParameters;
import entities.Pattern;
import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class PatternUtils {
    
    public static ArrayList<Pattern> copyPatternList(ArrayList<Pattern> list) {
        ArrayList<Pattern> copyList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            copyList.add(list.get(i));
        }

        return copyList;
    }

    public static void sortPatternListByTotalCoverageCount(ArrayList<Pattern> list) {
        Pattern temp;
        for (int i = list.size() - 1; i > 0; i--) {
            if (list.get(i).totalCoverageCount()> list.get(i - 1).totalCoverageCount()) {
                temp = list.remove(i - 1);
                list.add(i, temp);
            }
        }
    }

    public static void printPatternListStat(ArrayList<Pattern> list, boolean patternWiseCount) {
        int size = list.size();
        int[] counts = {0, 0, 0};
        Pattern p;
        for (int i = 0; i < size; i++) {
            p = list.get(i);
            if (patternWiseCount) {
                System.out.print(String.format("Pattern(%d/%d)>> ", i, size));
                printPatternInfo(p);
            }
            counts[p.label]++;
        }

        System.out.println("List Size: " + size + ", nCount: " + counts[0] + ", xCount: " + counts[1] + ", pCount:" + counts[2]);
    }

    public static void printPatternInfo(Pattern p) {
        System.out.print("label:" + p.label);
        System.out.print("; n: " + p.coverageCounts[0]);
        System.out.print("; x: " + p.coverageCounts[1]);
        System.out.println("; p: " + p.coverageCounts[2]);

    }

    public static Pattern[] filterPatternsByScore(Pattern[] pats, float scoreTh) {
        ArrayList<Pattern> patList = new ArrayList<>();
        for (int i = 0; i < pats.length; i++) {
            if (pats[i].biteScore >= scoreTh) {
                patList.add(pats[i]);
            }
        }
        return patternListToArray(patList);
    }

    public static ArrayList<Pattern> patternArrayToList(Pattern[] pats) {
        if (pats == null) {
            return null;
        }
        ArrayList<Pattern> patList = new ArrayList<>();
        for (int i = 0; i < pats.length; i++) {
            patList.add(pats[i]);
        }

        return patList;
    }

    public static Pattern[] patternListToArray(ArrayList<Pattern> patList) {
        if (patList == null) {
            return null;
        }
        int count = patList.size();
        Pattern[] pats = new Pattern[count];
        for (int i = 0; i < pats.length; i++) {
            pats[i] = patList.get(i);
        }

        return pats;
    }

}
