package edu.virginia.cs.mooncake.wada.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static int fileCount() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
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
        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
        String filePath = folderName + "/" + fileName;
        File file = new File(filePath);
        return file.delete();
    }

    public static void deleteAllFiles() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
        File folder = new File(folderName);
        File[] fileList = folder.listFiles();

        for (int i = 0; fileList != null && i < fileList.length; i++) {
            fileList[i].delete();
        }
    }

    public static File[] getFileList() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
        File folder = new File(folderName);
        return folder.listFiles();
    }

    public static String[] getFileListString() {
        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
        File folder = new File(folderName);
        return folder.list();
    }

    public static boolean saveStringToFile(String fileName, String data) {
        boolean flag = true;

        if (fileName.length() < 1)
            fileName = "X_" + System.currentTimeMillis();

        String folderName = Environment.getExternalStorageDirectory() + "/wada/data";
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/" + fileName;
        /*file = new File(filePath);
        if (file.exists()) {
            return true;
        }*/

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

}
