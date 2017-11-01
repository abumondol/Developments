/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m2fedutils;

import constants_config.MyConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.tools.FileObject;

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
        br.close();
        return csv;
    }

    public static void writeToCSVFile(String file_name, float[][] data, String header) throws Exception {
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
    
    
    public static void appendToResultFile(int fileType, long timeForFileName, String watchId, String data) throws Exception {
        timeForFileName = System.currentTimeMillis();

        String dir = "M2FED_Results\\Eating\\";
        String fileName = DateTimeUtils.getDateString(timeForFileName) + "_";

        switch (fileType) {
            case (MyConstants.FILE_TYPE_BATTERY):
                dir += "battery";
                fileName += "watch_battery";
                break;
                case (MyConstants.FILE_TYPE_BEACON):
                dir += "beacon";
                fileName += "beacon_data";
                break;
            case (MyConstants.FILE_TYPE_LOCATION):
                dir += "location";
                fileName += "beacon_location";
                break;
            case (MyConstants.FILE_TYPE_BITE):
                dir += "bite";
                fileName += "eating_bite";
                break;
            case (MyConstants.FILE_TYPE_MEAL):
                dir += "meal";
                fileName += "eating_meal";
                break;
            case (MyConstants.FILE_TYPE_EMA):
                dir += "ema";
                fileName += "eating_ema";
                break;
            default:
                System.out.println("Error. Wrong file type. File type: " + fileType);
                return;
        }

        File dirFolder = new File(dir);
        if (!dirFolder.exists()) {
            dirFolder.mkdirs();
        }

        fileName = dir + "\\" + fileName + "_" + watchId;
        appendToFile(fileName, data);
    }

    public static void appendToFile(String file_name, String data) throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(file_name), true);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(data);
        pw.flush();
        pw.close();
    }

    public static void writeToFile(String file_name, String data) throws Exception {
        PrintWriter pw = new PrintWriter(file_name);
        pw.print(data);
        pw.flush();
        pw.close();
    }

    public static void serializeFile(Object data, String fileName) throws Exception {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        fos.flush();
        oos.close();
        fos.close();
    }

    public static Object deSerializeFile(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }

}
