package pattern;

import constants_config.MyParameters;
import data_processing.DataManager;
import entities.Pattern;
import java.util.ArrayList;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class PatternUtils {

    public static int distanceToBinIndex(float d) {
        return (int) (d / MyParameters.BIN_SIZE);
    }

    public static ArrayList<Pattern> getSerializedPatterns(String filename) throws Exception {
        if (!DataManager.isSerFileExists(filename)) {
            System.out.println("File doesn't exist: " + filename);
            return null;
        }

        System.out.print("Deserializing file: " + filename);
        Pattern[] pats = (Pattern[]) MyFileUtils.deSerializeFile(DataManager.serFilePath + filename);
        System.out.println(" ... Done.");
        return patternArrayToList(pats);
    }

    public static void saveSerializedPatterns(ArrayList<Pattern> patList, String filename) throws Exception {
        System.out.print("Serializing file: " + filename);
        MyFileUtils.serializeFile(patternListToArray(patList), DataManager.serFilePath + filename);
        System.out.println(" ... Done.");
    }

    public static ArrayList<Pattern> copyPatternList(ArrayList<Pattern> list) {
        ArrayList<Pattern> copyList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            copyList.add(list.get(i));
        }

        return copyList;
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
                System.out.println();
            }
            counts[p.label]++;
        }

        System.out.println("List Size: " + size + ", nCount: " + counts[0] + ", pCount: " + counts[1] + ", xCount:" + counts[2]);
    }

    public static void printPatternInfo(Pattern p) {
        System.out.print("label:" + p.label);

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
