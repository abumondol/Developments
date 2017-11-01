package vocalwatch;

import utils.FileUtils;
import utils.MyUtils;

/**
 *
 * @author mm5gg
 */
public class Stats {

    public static void dataStats() throws Exception {
        String allFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\all_data_filtered.csv";
        String errorFilePath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\all_errors.csv";
        String[][] allData = FileUtils.readCSV(allFilePath, false);
        String[][] errorData = FileUtils.readCSV(errorFilePath, false);
        String[] subjects = MyUtils.getUniqueList(allData, 0);

        int i, j;
        String[][] subAllData, subErrorData;

        System.out.println("Subject, isNative, Reminder Count, Command Count, Error Count, Unique error word count");

        for (i = 0; i < subjects.length; i++) {
            subAllData = AnalyzeData.getSubjectData(allData, subjects[i], false);
            subErrorData = AnalyzeData.getSubjectData(errorData, subjects[i], false);
            if (MyUtils.isNative(subjects[i])) {
                j = 1;
            } else {
                j = 0;
            }
            System.out.println(subjects[i] + "," + j + "," + getReminderCount(subAllData) + "," + subAllData.length + "," + subErrorData.length + "," + MyUtils.getUniqueList(subErrorData, 5).length);
        }

        System.out.println("All,x," + getReminderCount(allData) + "," + allData.length + "," + errorData.length + "," + MyUtils.getUniqueList(errorData, 5).length);

    }

    public static int getReminderCount(String[][] data) {
        if (data.length == 0) {
            return 0;
        }
        int i, prevIndex = 0, currentIndex;
        int count = 1;
        for (i = 1; i < data.length; i++) {
            currentIndex = Integer.parseInt(data[i][4]);
            if (currentIndex != prevIndex) {
                count++;
            }

            prevIndex = currentIndex;
        }

        return count;
    }
}
