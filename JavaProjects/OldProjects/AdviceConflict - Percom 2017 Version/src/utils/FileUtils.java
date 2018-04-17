package utils;

//@author mm5gg
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileUtils {

    public static String[][] readAppFileAdviceCombined(File file) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        br.close();

        int i, j, len, listCount = list.size();
        String[] d, combo;
        ArrayList<String[]> list2 = new ArrayList<String[]>();

        combo = new String[4];
        combo[1] = "XXXX";

        for (i = 1; i < listCount; i++) {//i+1, skip the header    
            d = list.get(i).split(",");            
            if(d.length==0)
                continue;
            
            if(d.length<2){
                System.out.println("No second colmn: "+d.length+", File name: "+file.getName()+" --- "+i);
                System.out.println(list.get(i));
                return null;
            }
                
            d[1] = d[1].trim();
            if (!d[1].equals(combo[1])) {
                if (i > 1) {
                    list2.add(combo);
                }
                combo = new String[4];
                combo[0] = (i + 1) + "";
                combo[1] = d[1];
                combo[2] = "";
                combo[3] = "";

                if (d.length >= 6) {
                    combo[2] = d[5].trim().toLowerCase();
                }

                if (d.length >= 7) {
                    combo[3] = d[6].trim().toLowerCase();
                }

                continue;
            }

            if (d.length >= 6) {
                combo[2] += ";" + d[5].trim().toLowerCase();
            }

            if (d.length >= 7) {
                combo[3] += ";" + d[6].trim().toLowerCase();
            }

        }

        list2.add(combo);

        listCount = list2.size();
        String[][] data = new String[listCount][];
        for (i = 0; i < listCount; i++) {
            data[i] = list2.get(i);
        }
        return data;

    }

    public static String[][] readAppFile(File file) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        br.close();

        int i, j, len, lineCount = list.size(), adviceIndex = 0;
        String[][] data = new String[lineCount][10];
        String[] d;
        String lastAdvice = "XXXX";


        for (i = 0; i < lineCount - 1; i++) {
            d = list.get(i + 1).split(","); //i+1, skip the header
            if (!d[1].trim().equals(lastAdvice)) {
                lastAdvice = d[1].trim();
                adviceIndex++;
            }

            len = d.length;
            if (len < 3) {
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxx");
                System.out.println("Length is less than 3. Filename: " + file.getName() + ", line no: " + (i + 1));
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxx");
                return null;
            }

            data[i][0] = "" + adviceIndex;
            for (j = 0; j < len && j < 9; j++) {
                data[i][j + 1] = d[j].trim();

                if (j >= 3) {
                    data[i][j + 1] = data[i][j + 1].toLowerCase();
                }

                if (data[i][j + 1].length() == 0) {
                    data[i][j + 1] = null;
                }

            }
        }

        return data;
    }

    public static String[][][] readAppFiles() throws Exception {
        String[] appNames = getAppNames(MC.SRC_FOLDER_PATH);
        int fileCount = appNames.length;
        String[][][] allData = new String[fileCount][][];

        int i;
        for (i = 0; i < fileCount; i++) {
            File file = new File(MC.SRC_FOLDER_PATH + "\\" + appNames[i] + ".csv");
            allData[i] = readAppFileAdviceCombined(file);
            if (allData[i] == null) {
                return null;
            } else {
                System.out.println(file.getName() + " - " + allData[i].length);
            }
        }

        return allData;
    }

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

    public static String[] getAppNames(String folderPath) throws Exception {
        File file = new File(folderPath);
        String[] fileNames = file.list();
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = fileNames[i].replace(".csv", "");
        }

        return fileNames;
    }

    public static void writeFile(String folderPath, String fileName, String data) throws Exception {
        PrintWriter pw = new PrintWriter(folderPath + "\\" + fileName);
        pw.print(data);
        pw.flush();
        pw.close();
    }

    public static void writeStringArrayToFile(String folderPath, String fileName, String[] data) throws Exception {
        StringBuilder sb = new StringBuilder();
        int i, count = data.length;
        for (i = 0; i < count; i++) {
            sb.append(data[i]);
            sb.append("\n");
        }

        PrintWriter pw = new PrintWriter(folderPath + "\\" + fileName);
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }

    public static void writeStringArray2DToFile(String folderPath, String fileName, String[][] data) throws Exception {
        StringBuilder sb = new StringBuilder();
        int i, j, rows = data.length, cols;

        for (i = 0; i < rows; i++) {
            cols = data[i].length;
            for (j = 0; j < cols; j++) {
                if (data[i][j] != null) {
                    sb.append(data[i][j]);
                } else {
                    sb.append(data[i][j]);
                }
                if (j == cols - 1) {
                    sb.append("\n");
                } else {
                    sb.append(",");
                }

            }

        }

        PrintWriter pw = new PrintWriter(folderPath + "\\" + fileName);
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }
    
    public static void writeAppData(String[][][] data) throws Exception{
        int appCount = data.length;
        String[] appNames = getAppNames(MC.SRC_FOLDER_PATH);
        
        for(int i=0;i<appCount;i++){
            writeStringArray2DToFile(MC.DEST_FOLDER_PATH+"\\apps", "generated_"+appNames[i]+".csv", data[i]);
        }
    }
    
}
