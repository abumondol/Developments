package edu.virginia.cs.mondol.medwatch.utils;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

import edu.virginia.cs.mondol.medwatch.config.MyNetConfig;

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


    public static boolean appendStringToFile(String fileName, String data, boolean willUpload) {
        boolean flag = true;

        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        if (willUpload == false)
            folderName = Environment.getExternalStorageDirectory() + "/M2FED_log";

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

    public static boolean appendStringToFile2(String fileName, String data, boolean willUpload) {
        boolean flag = true;

        String folderName = Environment.getExternalStorageDirectory() + "/M2FED_COMBO_DATA";
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
            Log.e("Combo File save error", ex.toString());
        }

        return flag;
    }

    public static boolean renameFile(String oldFileName, String newFileName) {
        String folderName = Environment.getExternalStorageDirectory() + "/M2FED";
        File file1 = new File(folderName + "/" + oldFileName);
        File file2 = new File(folderName + "/" + newFileName);
        boolean success = file1.renameTo(file2);
        return success;
    }

    public static String readConfig() {
        String folderName = Environment.getExternalStorageDirectory() + "/";
        File file = new File(folderName + FedConstants.FED_CONFIG);

        if (!file.exists())
            return null;

        String line;
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();

        } catch (Exception ex) {
            Log.e("Config read error", ex.toString());
            return null;
        }

        return sb.toString();
    }

    public static boolean saveConfig(String data) {
        boolean flag = true;

        String folderName = Environment.getExternalStorageDirectory() + "/";
        String filePath = folderName + FedConstants.FED_CONFIG;

        try {
            FileOutputStream fos = new FileOutputStream(filePath, false);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            flag = false;
            Log.e("File save error", ex.toString());
        }

        return flag;
    }


    public static MyNetConfig readNetConfig() {
        String filename = Environment.getExternalStorageDirectory() + "/fed_config/watch_config.txt";
        File file = new File(filename);

        MyNetConfig nc = new MyNetConfig();
        if (!file.exists()) {
            nc.file_read_message = "Config unavailable";
            return nc;
        }


        String s, line;
        String[] sarr;
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sarr = line.trim().split(":");
                s = sarr[0].trim();
                if(s.equals("wifi_ssid")){
                    nc.wifi_ssid = sarr[1].trim();
                }else if(s.equals("wifi_password")){
                    nc.wifi_password = sarr[1].trim();
                }else if(s.equals("server_ip")){
                    nc.server_ip = sarr[1].trim();
                }else if(s.equals("beacons")){
                    if(sarr.length>1) {
                        s = sarr[1].trim();
                        if(s.length()>0) {
                            sarr = sarr[1].split(",");
                            int[] beacon_indices = new int[sarr.length];
                            for (int i = 0; i < sarr.length; i++)
                                beacon_indices[i] = Integer.parseInt(sarr[i].trim());

                            nc.beacon_indices = beacon_indices;
                        }
                    }

                }
            }

            br.close();
        } catch (Exception ex) {
            Log.e("Config read error", ex.toString());
            nc.file_read_message = "Config read error";

        }

        return nc;

    }

    public static float[][][][] getPatterns(){
        String filename = Environment.getExternalStorageDirectory() + "/fed_config/patterns.ser";
        File file = new File(filename);

        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            float[][][][] patterns = (float[][][][])ois.readObject();
            ois.close();
            fis.close();
            return patterns;
        }catch(Exception ex){
            return null;
        }

    }




}
