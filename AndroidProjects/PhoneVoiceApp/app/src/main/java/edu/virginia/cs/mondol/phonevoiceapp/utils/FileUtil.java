package edu.virginia.cs.mondol.phonevoiceapp.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FileUtil {

    public static int fileCount() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File folder = new File(folderName);
        if (folder.exists() == false) {
            folder.mkdirs();
        }
        File[] fileList = folder.listFiles();
        if (fileList == null)
            return 0;
        return fileList.length;
    }

    public static boolean deleteFile(String fileName) {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        String filePath = folderName + "/" + fileName;
        File file = new File(filePath);
        return file.delete();
    }

    public static void deleteAllFiles() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File folder = new File(folderName);
        File[] fileList = folder.listFiles();

        for (int i = 0; fileList != null && i < fileList.length; i++) {
            fileList[i].delete();
        }
    }

    public static File[] getFileList() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File folder = new File(folderName);
        return folder.listFiles();
    }

    public static String[] getFileListString() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File folder = new File(folderName);
        return folder.list();
    }

    public static boolean saveStringToFile(String fileName, String data) {
        boolean flag = true;

        if (fileName.length() < 1)
            fileName = "X_" + System.currentTimeMillis();

        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/" + fileName;
        file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            flag = false;
            Log.e("File save error", ex.toString());
        }

        return flag;
    }

    public static boolean appendStringToFile(String fileName, String data) {
        boolean flag = true;
        fileName += ".csv";

        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder/data";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/" + fileName;

        try {
            FileOutputStream fos = new FileOutputStream(filePath, true);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            flag = false;
            Log.e("File save error", ex.toString());
        }

        return flag;
    }

    public static boolean isConfigAvailable() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdir();
        }

        String filePath = folderName + "/config.json";
        file = new File(filePath);
        return file.exists();
    }

    public static String readConfig() {
        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch/reminder";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/config.json";
        file = new File(filePath);
        if (file.exists() == false) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (Exception ex) {
            Log.i("Error", "Reading config.txt");
            return null;
        }

        return sb.toString();
    }

    public static String[] getSubjectList() {

        String[] subjects = {
                "Subject1", "Subject2", "Subject3", "Subject4", "Subject5"
        };

        String folderName = Environment.getExternalStorageDirectory() + "/vocalwatch";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdir();
        }

        String filePath = folderName + "/subjects.txt";
        file = new File(filePath);
        if (file.exists() == false) {
            return subjects;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            if ((line = br.readLine()) != null) {
                subjects = line.split(",");
                for (int i = 0; i < subjects.length; i++) {
                    subjects[i] = subjects[i].trim();
                }
            }
            br.close();

        } catch (Exception ex) {
            Log.i("Error", "Reading config.txt");
        }
        return subjects;
    }


}
