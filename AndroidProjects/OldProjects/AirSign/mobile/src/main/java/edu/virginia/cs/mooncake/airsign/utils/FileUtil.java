package edu.virginia.cs.mooncake.airsign.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import edu.virginia.cs.mooncake.airsign.myclasses.SensorSample;

public class FileUtil {
    public static String folder = "airsign";

    public static boolean deleteTemplateFiles(Context context) {
        boolean flag = false;

        String[] fileNames = context.fileList();
        for (int i = 0; fileNames != null && i < fileNames.length; i++)
            flag = context.deleteFile(fileNames[i]);

        return flag;
    }

    public static boolean deleteFile(Context context, String fileName) {
        boolean flag = false;

        if (fileName == null) {
            String folderName = Environment.getExternalStorageDirectory() + "/" + folder;
            File file = new File(folderName);
            File[] fileList = file.listFiles();
            for (int i = 0; fileList != null && i < fileList.length; i++)
                flag = fileList[i].delete();
        } else {
            flag = context.deleteFile(fileName);
        }

        return flag;
    }

    public static int getFileCount(Context context) {
        String folderName = Environment.getExternalStorageDirectory() + "/" + folder;
        File file = new File(folderName);
        File[] fileList = file.listFiles();
        if (fileList != null)
            return fileList.length;
        else
            return -1;
    }

    public static int getTemplateCount(Context context) {
        String[] fileNames = context.fileList();
        if (fileNames != null)
            return fileNames.length;
        else
            return -1;

    }

    public static boolean saveStringToFileInternal(Context context, String data, String fileName) {
        boolean flag = true;

        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception ex) {
            Log.e("Internal File save error", ex.toString());
        }

        return flag;
    }

    public static boolean saveStringToFile(Context context, String data, String fileName) {
        boolean flag = true;
        String folderName = Environment.getExternalStorageDirectory() + "/" + folder;
        File file = new File(folderName);
        if (file.exists() == false) {
            file.mkdir();
        }

        String filePath = folderName + "/" + fileName;
        file = new File(fileName);
        if (file.exists()) {
            return true;
        }

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception ex) {
            flag = false;
            Log.e("File save error", ex.toString());
        }

        return flag;
    }

    public static boolean saveSampleListToFileAsText(Context context, ArrayList<SensorSample> list, String fileName, boolean type) {
        int i, j, size = list.size();

        StringBuilder sb = new StringBuilder();
        SensorSample s;

        for (i = 0; i < size; i++) {
            s = list.get(i);
            sb.append(s.timeStamp);
            sb.append(",");
            sb.append(s.sensorType);
            sb.append(",");
            sb.append(s.accuracy);

            for (j = 0; j < s.values.length; j++) {
                sb.append(",");
                sb.append(s.values[j]);
            }
            sb.append("\n");
        }

        if (type == false)
            return saveStringToFile(context, sb.toString(), fileName);
        else
            return saveStringToFileInternal(context, sb.toString(), fileName);
    }

    public static ArrayList<ArrayList<SensorSample>> getTemplateSigns(Context context) {
        ArrayList<ArrayList<SensorSample>> list = new ArrayList<ArrayList<SensorSample>>();
        ArrayList<SensorSample> l;

        File file = context.getFilesDir();
        File[] file_list = file.listFiles();
        Log.i("Template file read", "count: "+file_list.length);

        for (int i = 0; i < file_list.length ; i++) {
            list.add(deserializeTemplate(context, file_list[i]));
        }

        return list;
    }

    public static void copyTemplateFiles(Context context, boolean success) {
        String suffix = "";
        if (success == false)
            suffix = "_f";

        File file = context.getFilesDir();
        File[] sourceFiles = file.listFiles();
        //String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder;
        //File destinationFile;

//        File dir = new File(destinationPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }

        ArrayList<SensorSample> list;
        for (int i = 0; sourceFiles != null && i < sourceFiles.length; i++) {
            //destinationFile = new File(destinationPath + "/" + sourceFiles[i].getName() + suffix);
            list = deserializeTemplate(context, sourceFiles[i]);
            saveSampleListToFileAsText(context, list, sourceFiles[i].getName() + suffix, false);

        }

    }

    public static void serializeTemplate(Context context, ArrayList<SensorSample> list, String fileName){

        try{
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objout = new ObjectOutputStream(fout);
            objout.writeObject(list);
            objout.flush();
            objout.close();
            if(fout!=null)
                fout.close();
        }catch(Exception ex){
            Log.i("Serialize Template", ex.toString());
        }

    }

    public static ArrayList<SensorSample>  deserializeTemplate(Context context, File file){
        ArrayList<SensorSample> list = null;
        try{
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fileIn);
            list = (ArrayList<SensorSample>) oin.readObject();
            oin.close();
            if(fileIn!=null)
                fileIn.close();
        }catch(Exception ex){
            Log.i("DeSerialize Template", ex.toString());
        }

        return list;
    }

    public static void copyTextTemplateFiles(Context context, boolean success) {
        String suffix = "";
        if (success == false)
            suffix = "_f";

        File file = context.getFilesDir();
        File[] sourceFiles = file.listFiles();
        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder;
        File destinationFile;

        for (int i = 0; sourceFiles != null && i < sourceFiles.length; i++) {

            try {
                InputStream in = null;
                OutputStream out = null;

                //create output directory if it doesn't exist
                File dir = new File(destinationPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                destinationFile = new File(destinationPath + "/" + sourceFiles[i].getName() + suffix);

                in = new FileInputStream(sourceFiles[i]);
                out = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
            } catch (Exception ex) {
                Log.e("Internal File save error", ex.toString());
            }

        }

    }


}
