package vocalwatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import utils.MyUtils;

/**
 *
 * @author mm5gg
 */
public class ProcessData {

    public static void summarizeData() throws Exception {

        String srcFolderPath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\labeled";
        String destFolderPath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data";

        BufferedReader br;
        File[] srcFiles = new File(srcFolderPath).listFiles();
        String fileName, user, date, time, line;
        String[] s;
        StringBuilder sbAll = new StringBuilder();
        StringBuilder sbAllFiltered = new StringBuilder();
        StringBuilder sbError = new StringBuilder();
        sbAll.append("subject, isNative, date, time, reminder_sl, recognized, actual\n");
        sbAllFiltered.append("subject, isNative, date, time, reminder_sl, recognized, actual\n");
        sbError.append("subject, isNative, date, time, reminder_sl, recognized, actual\n");
        boolean flag;
        String[] row = new String[7];

        int i, j, k;
        int allCount = 0, filteredCount = 0, errorCount = 0;
        int allCountNative = 0, filteredCountNative = 0, errorCountNative = 0;
        int totalReminderCount = 0, nativeReminderCount = 0, totalFileCount = 0, nativeFileCount = 0;
        boolean isNative;

        for (i = 0; i < srcFiles.length; i++) {
            fileName = srcFiles[i].getName();
            System.out.println("Processing : " + fileName);
            s = fileName.split("[-.]");
            user = fileName.substring(0, fileName.indexOf("-")).trim();
            date = s[2] + "-" + s[3] + "-" + s[4];
            time = s[5] + "-" + s[6] + "-" + s[7];
            totalFileCount++;
            isNative = MyUtils.isNative(user);
            if (isNative) {
                nativeFileCount++;
            }

            br = new BufferedReader(new FileReader(srcFiles[i]));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                s = line.split(",");
                s[6] = s[6].trim();
                row[0] = user;                
                row[1] = isNative ? "1" : "0";
                row[2] = date;
                row[3] = time;
                row[4] = s[1];
                row[5] = s[6];
                row[6] = "";
                if (s.length == 8) {
                    s[7] = s[7].trim();
                    row[6] = s[7];
                }

                addEntry(sbAll, row);
                allCount++;
                if (isNative) {
                    allCountNative++;
                }

                if (!s[6].equals("null xxx") && !row[6].equals("xxx")) {
                    addEntry(sbAllFiltered, row);
                    filteredCount++;
                    if (isNative) {
                        filteredCountNative++;
                    }
                    if (row[6].length() > 0) {
                        addEntry(sbError, row);
                        errorCount++;
                        if (isNative) {
                            errorCountNative++;
                        }
                    }
                }
            }

            totalReminderCount += Integer.parseInt(s[1]) + 1;
            if (isNative) {
                nativeReminderCount += Integer.parseInt(s[1]) + 1;
            }

        }

        PrintWriter out = new PrintWriter(destFolderPath + "\\all_data.csv");
        out.print(sbAll.toString());
        out.flush();
        out.close();

        out = new PrintWriter(destFolderPath + "\\all_data_filtered.csv");
        out.print(sbAllFiltered.toString());
        out.flush();
        out.close();

        out = new PrintWriter(destFolderPath + "\\all_errors.csv");
        out.print(sbError.toString());
        out.flush();
        out.close();

        System.out.println("Type, Total, Native, NonNative ");
        System.out.println("File count," + totalFileCount + ", " + nativeFileCount + ", " + (totalFileCount - nativeFileCount));
        System.out.println("Reminder count," + totalReminderCount + ", " + nativeReminderCount + ", " + (totalReminderCount - nativeReminderCount));
        System.out.println("All Command count," + allCount + ", " + allCountNative + ", " + (allCount - allCountNative));
        System.out.println("Filtered Command count," + filteredCount + ", " + filteredCountNative + ", " + (filteredCount - filteredCountNative));
        System.out.println("Error count," + errorCount + ", " + errorCountNative + ", " + (errorCount - errorCountNative));

    }

    public static void addEntry(StringBuilder sb, String[] data) {
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i]);
            if (i == data.length - 1) {
                sb.append("\n");
            } else {
                sb.append(",");
            }
        }

    }
}
