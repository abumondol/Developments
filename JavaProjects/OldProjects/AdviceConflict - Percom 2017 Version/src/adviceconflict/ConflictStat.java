package adviceconflict;

import java.util.ArrayList;
import utils.FileUtils;
import utils.MyUtils;

/**
 *
 * @author mm5gg
 */
public class ConflictStat {

    public static void findConflctStat() throws Exception {

        String srcTrain = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\AdviceConflict\\data\\stat data\\TrainDataConflict.csv";
        String srcTest = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\AdviceConflict\\data\\stat data\\TestDataConflict.csv";
        String srcCases = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\AdviceConflict\\data\\stat data\\CasesToCompare.csv";
        String destFolder = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\AdviceConflict\\data\\stat data";

        String[][] train = FileUtils.readCSV(srcTrain, false);
        String[][] test = FileUtils.readCSV(srcTest, false);
        String[][] cases = FileUtils.readCSV(srcCases, false);
        String[][] result = new String[cases.length][6];

        train = MyUtils.toLowerCaseAndremoveSpace(train);
        test = MyUtils.toLowerCaseAndremoveSpace(test);
        cases = MyUtils.toLowerCaseAndremoveSpace(cases);

        //System.out.println(train.length+", "+train[0].length);
        int caseCount = cases.length;
        int i;

        for (i = 0; i < caseCount; i++) {
            String app1 = cases[i][0];
            String app2 = cases[i][2];
            int[] lines1 = getLinesArray(cases[i][1]);
            int[] lines2 = getLinesArray(cases[i][3]);

            result[i][0] = cases[i][0];
            result[i][1] = cases[i][1];
            result[i][2] = cases[i][2];
            result[i][3] = cases[i][3];
            result[i][4] = "" + findConflictCountBetweenApps(app1, app2, lines1, lines2, train);
            result[i][5] = "" + findConflictCountBetweenApps(app1, app2, lines1, lines2, test);
            System.out.println(app1 + ":" + intArrToStr(lines1) + ";; " + app2 + "," + intArrToStr(lines2) + ";; " + result[i][4] + ", " + result[i][5]);
        }

        FileUtils.writeStringArray2DToFile(destFolder, "stats.csv", result);
    }

    public static int findConflictCountBetweenApps(String app1, String app2, int[] lines1, int[] lines2, String[][] data) throws Exception {
        //System.out.println(app1 + "," + intArrToStr(lines1) + ", " + app2 + "," + intArrToStr(lines2)+","+data.length+"\n");
        int count = 0, len, len1, len2, i, j, k;
        len = data.length;
        for (i = 0; i < len; i++) {
            //System.out.println(i+","+data[i][0]+", "+data[i][1]+","+data[i][2]+", "+data[i][3]+"\n");
            int line1 = Integer.parseInt(data[i][1]);
            int line2 = Integer.parseInt(data[i][3]);
            boolean cond1 = data[i][0].equals(app1) && data[i][2].equals(app2);
            boolean cond2 = data[i][0].equals(app2) && data[i][2].equals(app1);
            if (!cond1 && !cond2) {
                continue;
            }

            if (cond2) {
                String temp = app1;
                app1 = app2;
                app2 = temp;
                int[] t = lines1;
                lines1 = lines2;
                lines2 = t;
            }

            if (lines1 == null && lines2 == null) {
                count++;
            } else if (lines1 == null) {
                for (j = 0; j < lines2.length; j++) {
                    if (line2 == lines2[j]) {
                        count++;
                        break;
                    }
                }
            } else if (lines2 == null) {
                for (j = 0; j < lines1.length; j++) {
                    if (line1 == lines1[j]) {
                        count++;
                        break;
                    }
                }
            } else {
                for (j = 0; j < lines1.length; j++) {
                    if (line1 == lines1[j]) {
                        for (k = 0; k < lines2.length; k++) {
                            if (line2 == lines2[k]) {
                                count++;
                                break;
                            }
                        }
                        break;
                    }
                }
            }

        }

        return count;
    }

    public static int[] getLinesArray(String s) {
        int[] lines = null;

        if (!s.equals("all")) {
            int i, j, k, a, b, len;
            String[] s1, s2;
            ArrayList<Integer> list = new ArrayList<Integer>();

            s1 = s.split(";");
            for (i = 0; i < s1.length; i++) {
                if (s1[i].length() > 0) {
                    s2 = s1[i].split("-");
                    a = Integer.parseInt(s2[0].trim());
                    b = Integer.parseInt(s2[1].trim());
                    for (j = a; j <= b; j++) {
                        list.add(new Integer(j));
                    }
                }
            }

            len = list.size();
            lines = new int[len];
            for (i = 0; i < len; i++) {
                lines[i] = list.get(i).intValue();
            }

        }

        return lines;
    }

    public static String intArrToStr(int[] a) {
        if (a == null) {
            return "null";
        }
        String s = "";
        for (int i = 0; i < a.length; i++) {
            s += a[i] + "; ";
        }

        return s;
    }
}
