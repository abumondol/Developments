package edu.virginia.cs.mondol.beacon.myutils;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtils {

    public static int fileCount() {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        File folder = new File(folderName);
        if (folder.exists() == false) {
            folder.mkdirs();
        }
        File[] fileList = folder.listFiles();
        if (fileList == null)
            return 0;
        return fileList.length;
    }

    public static int fileCount2() {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED_log";
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
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        String filePath = folderName + "/" + fileName;
        File file = new File(fileName);
        return file.delete();
    }

    public static void deleteAllFiles() {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        File folder = new File(folderName);
        File[] fileList = folder.listFiles();

        for (int i = 0; fileList != null && i < fileList.length; i++) {
            fileList[i].delete();
        }
    }

    public static void deleteAllFiles2() {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED_log";
        File folder = new File(folderName);
        File[] fileList = folder.listFiles();

        for (int i = 0; fileList != null && i < fileList.length; i++) {
            fileList[i].delete();
        }
    }


    public static File[] getFileList() {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        File folder = new File(folderName);
        return folder.listFiles();
    }


    public static boolean appendStringToFile(String fileName, String data) {
        boolean flag = true;

        String folderName = Environment.getExternalStorageDirectory() + "/Beacon";
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


    public static String[] getMacList() {
        String folderName = Environment.getExternalStorageDirectory() + "/";
        File file = new File(folderName +"Beacon/macList.txt");

        if (!file.exists())
            return null;

        String line;
        ArrayList<String> macList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                macList.add(line.trim());
            }
            br.close();

        } catch (Exception ex) {
            Log.e("Config read error", ex.toString());
            return null;
        }
        int count = macList.size();
        if(count == 0)
            return null;
        String[] macs = new String[count];

        for(int i=0;i<count;i++)
            macs[i] = macList.get(i);

        return macs;
    }

}
