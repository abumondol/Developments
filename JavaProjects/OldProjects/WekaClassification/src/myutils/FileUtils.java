/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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
        int i, j, k, len, rowCount;

        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            list.add(line.split(","));
        }

        if (!includeFirstLine) {
            list.remove(0);
        }

        rowCount = list.size();
        String[][] csv = new String[rowCount][];
        for (i = 0; i < rowCount; i++) {
            csv[i] = list.get(i);
        }

        return csv;
    }

    public static void writeToCSVFile(String file_name, String[][] data, String header) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (header != null) {
            sb.append(header + "\n");
        }

        int i, j;
        for (i = 0; i < data.length; i++) {
            for (j = 0; j < data[i].length; j++) {
                sb.append(data[i][j]);
                if (j == data[i].length - 1) {
                    sb.append("\n");
                } else {
                    sb.append(",");
                }
            }
        }

        writeToFile(file_name, sb.toString());

    }

    public static void writeToFile(String file_name, String data) throws Exception {
        PrintWriter pw = new PrintWriter(file_name);
        pw.print(data);
        pw.flush();
        pw.close();
    }
}
