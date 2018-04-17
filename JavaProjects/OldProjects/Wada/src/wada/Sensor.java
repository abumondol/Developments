package wada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 *
 * @author mm5gg
 */
public class Sensor {
    
    public static void filterAllSensorFiles(String srcFolder, String destFolder, int sensorID) throws Exception{
        File folder = new File(srcFolder);
        File[] files = folder.listFiles();
        StringBuilder sb;
        PrintWriter out;
        
        File dest = new File(destFolder);
        if(!dest.exists()){
            dest.mkdirs();
        }
        
        System.out.println("Total file: "+files.length);
        for(int i=0;i<files.length;i++){
            System.out.print("File "+(i+1)+"/"+files.length+": "+files[i].getName());
            if(files[i].isHidden()){
                System.out.println(", Hidden: not processed");
                continue;
            }            
            
            sb = filterSensorFile(files[i], sensorID);
            out = new PrintWriter(destFolder+"//"+files[i].getName().replace(".wada", ".csv"));
            out.print(sb.toString());
            out.flush();
            out.close();
            System.out.println(":: Done");
        }        
    }
    
    public static StringBuilder filterSensorFile(File file, int sensorID) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;       
        String[] tokens;
        int i, j;

        while ((line = br.readLine()) != null) {
            tokens = line.split(",");
            if (sensorID == Integer.parseInt(tokens[1])) {
                sb.append(Long.parseLong(tokens[0]) / MyConstants.nanoMilliFactor);
                sb.append(",");
                for (i = 3; i < tokens.length - 1; i++) {
                    sb.append(tokens[i]);
                    sb.append(",");
                }
                sb.append(tokens[i]);
                sb.append("\n");
            }
        }

        br.close();
        return sb;
    }
}
