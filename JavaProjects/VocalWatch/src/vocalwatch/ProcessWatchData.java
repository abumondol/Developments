package vocalwatch;

// @author mm5gg
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessWatchData {

    public static JSONObject readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");

        JSONObject json = new JSONObject(str);
        return json;
    }

    public static void processFiles() throws Exception {
        String srcFolderPath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\from_watch";
        String labeledFolderPath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\labeled";
        String destFolderPath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\Research\\Projects\\Vocal Reminder\\data\\not_labeled";
        String destFileName;

        File[] srcFiles = new File(srcFolderPath).listFiles();
        File destFile, labeledFile;
        JSONObject jsonObj, logObj;
        JSONArray jsonArr;
        int i, j, k, len, processedCount;
        StringBuilder sb;
        PrintWriter out;

         
        processedCount = 0;
        for (i = 0; i < srcFiles.length; i++) {
            destFileName = srcFiles[i].getName();
            destFileName = destFileName.substring(destFileName.indexOf("-") + 1);
            destFile = new File(destFolderPath+"\\"+destFileName);
            labeledFile = new File(labeledFolderPath+"\\"+destFileName);
            if(destFile.exists() || labeledFile.exists()){
                System.out.println("Already labeled: "+srcFiles[i].getName());
                continue;            
            }
            
            processedCount++;            
            System.out.println("Processing: " + i + ", " + srcFiles[i].getName());
            jsonObj = readFile(srcFiles[i]);
            jsonArr = jsonObj.getJSONArray("log_array");
            len = jsonArr.length();

            sb = new StringBuilder();
            sb.append("time,reminderIndex,state,resultCode,action,sttIndex,data_recognized,data_actual\n");
            for (j = 0; j < len; j++) {
                //System.out.println(j);
                logObj = jsonArr.getJSONObject(j);
                if (!logObj.get("action").equals("stt")) {
                    continue;
                }
                sb.append(logObj.get("time"));
                sb.append(",");
                sb.append(logObj.get("reminderIndex"));
                sb.append(",");
                sb.append(logObj.get("state"));
                sb.append(",");
                sb.append(logObj.get("resultCode"));
                sb.append(",");
                sb.append(logObj.get("action"));
                sb.append(",");
                sb.append(logObj.get("sttIndex"));
                sb.append(",");
                if(logObj.has("data")){
                    sb.append(logObj.get("data"));
                }else{
                    sb.append("xxx");
                }
                
                sb.append("\n");
            }
            
            out = new PrintWriter(destFolderPath + "\\" + destFileName);
            out.print(sb.toString());
            out.flush();
            out.close();
        }
        
        System.out.println("File count: "+srcFiles.length+",  Processed: "+processedCount);

    }
}
