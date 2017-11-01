package edu.virginia.cs.mooncake.airsignapp.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;


public class FileUtil {

    public static int fileCount(String path) {
        if (path.length() <= 2)
            return 0;

        String folderName = Environment.getExternalStorageDirectory() + path;
        File folder = new File(folderName);
        if (folder.exists() == false) {
            return 0;
        }

        return folder.listFiles().length;
    }

    public static boolean deleteFile(String path, String fileName) {

        if (path.length() <= 2)
            return false;

        String folderName = Environment.getExternalStorageDirectory() + path;
        File folder = new File(folderName);
        if (folder.exists() == false) {
            return false;
        }

        File[] files = folder.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].getName().equals(fileName)) {
                return files[i].delete();
            }
        }

        return false;
    }


    public static boolean deleteAllFiles(String path) {
        if (path.length() <= 2)
            return false;

        String folderName = Environment.getExternalStorageDirectory() + path;
        File folder = new File(folderName);
        if (folder.exists() == false) {
            return false;
        }

        boolean flag = false;
        File[] files = folder.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            flag = files[i].delete();
        }

        return flag;
    }

    public static boolean appendStringToFile(String path, String fileName, String data) {
        boolean flag = true;

        if (fileName.length() < 1)
            fileName = "X_" + System.currentTimeMillis();
        fileName += ".csv";

        String folderName = Environment.getExternalStorageDirectory() + path;
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdir();
        }

        String filePath = folderName + "/" + fileName;
        //file = new File(filePath);

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


    public static boolean saveStringToFile(String path, String fileName, String data) {

        if (path.length() <= 2)
            return false;

        boolean flag = true;

        if (fileName.length() < 1)
            fileName = "X_" + System.currentTimeMillis();
        fileName += ".csv";

        String folderName = Environment.getExternalStorageDirectory() + path;
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

    public static boolean saveByteArrayToFile(String folderName, String fileName, byte[] data) {

        File file = new File( folderName);
        if (file.exists() == false) {
            file.mkdirs();
        }

        String filePath = folderName + "/" + fileName;
        file = new File(filePath);
        if (file.exists()) {
            return true;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            Log.e("File save error", ex.toString());
            return false;
        }

        return true;
    }

    public static byte[] readByteArrayFromFile(String path, String fileName) {
        if (path.length() <= 2)
            return null;

        String folderName = Environment.getExternalStorageDirectory() + path;
        String filePath = folderName + "/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            byte fileContent[] = new byte[(int) file.length()];
            fis.read(fileContent);
            fis.close();
            return fileContent;
        } catch (Exception ex) {
            Log.e("File save error", ex.toString());
        }

        return null;
    }


    public static String getDataStat() {
        StringBuilder sb = new StringBuilder();

        File usersFolder = new File(MyConstants.PATH_DATA_FOLDER);
        if (!usersFolder.exists())
            return "There is no user listed.";

        File[] files = usersFolder.listFiles();
        for(int i=0;i<files.length;i++){
            File[] f = files[i].listFiles();
            if(f!=null && f.length>0){
                sb.append(files[i].getName());
                sb.append(": ");
                sb.append(f.length);
                sb.append("; ");
            }

        }

        return sb.toString();
    }

    public static String[] getUserList() {
        File usersFolder = new File(MyConstants.PATH_DATA_FOLDER);
        if (!usersFolder.exists())
            return null;

        String[] userList = usersFolder.list();
        if(userList==null || userList.length==0)
            return null;

        Arrays.sort(userList);
        return userList;
    }

    public static String[] getPasswordList(String userName)  {
        File passwordFile = new File(MyConstants.PATH_PASSWORDS_FOLDER + "/" + userName + ".txt");
        if (!passwordFile.exists())
            return null;

        String line=null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(passwordFile));
            line = br.readLine();
            br.close();

        }catch(Exception ex){
            Log.i("MyTAG",ex.toString());
        }

        if (line == null)
            return null;

        return line.split(",");
    }

    public static void addUser(String userName) {
        File usersFolder = new File(MyConstants.PATH_DATA_FOLDER + "/" + userName);
        if (!usersFolder.exists())
            usersFolder.mkdirs();
    }

    public static void addPassword(String userName, String password) {
        File passwordFolder = new File(MyConstants.PATH_PASSWORDS_FOLDER);
        if (!passwordFolder.exists())
            passwordFolder.mkdirs();

        File passwordFile = new File(MyConstants.PATH_PASSWORDS_FOLDER + "/" + userName + ".txt");
        try {
            if (passwordFile.exists()) {
                password = "," + password;
            }

            FileOutputStream fos = new FileOutputStream(passwordFile, true);
            fos.write(password.getBytes());
            fos.flush();
            fos.close();

        }catch(Exception ex){
            Log.i("MyTAG", ex.toString());
        }

    }

}
