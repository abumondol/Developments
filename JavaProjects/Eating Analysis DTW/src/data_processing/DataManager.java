package data_processing;

import java.io.File;
import java.util.ArrayList;

public class DataManager {

    public static String serFilePath = "C:\\ASM\\DevData\\eating\\java\\";

    public static boolean isSerFileExists(String fileName) {
        return new File(serFilePath + fileName).exists();
    }

    public static int timeToIndex(float[] times, float t) {
        for (int i = 0; i < times.length - 1; i++) {
            if (times[i] <= t && times[i + 1] > t) {
                return i;
            }
        }
        return -1;
    }
    
    public static int[][] getAnnotsForType(int[][] annots, int type) {
        
        ArrayList<int[]> list = new ArrayList<>();
        
        for (int i = 0; i < annots.length; i++) { 
                if (annots[i][1] == type) {
                    list.add(annots[i]);
                }            
        }
        
        int[][] res = new int[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }

        return res;
    }
    
    public static float[][] adjustSamplingRate(float[][] data){
        float sampling_rate = 16;
        float period = 1/sampling_rate;
        
        float duration = data[data.length-1][0] - data[0][0];
        int count = (int)(duration*sampling_rate);
        float[][] d = new float[count][4];
        
        int j = 0;
        float t, factor;
        for(int i=0;i<count; i++){
            t = data[0][0] + period*i;
            d[i][0] = t;
            
            while(!(data[j][0]<=t && data[j+1][0]>t))
                j++;
            
            factor = t - data[j][0];
            d[i][1] = (1-factor)*data[j][1] + factor*data[j+1][1];
            d[i][2] = (1-factor)*data[j][2] + factor*data[j+1][2];
            d[i][3] = (1-factor)*data[j][3] + factor*data[j+1][3];
        }
        
        return d;
    }
    
}
