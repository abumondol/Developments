package data_process;

import classification.PatternManager;
import classification.BiteDetector;
import constants_config.MyConstants;
import entities.Bite;
import java.util.ArrayList;
import entities.FileDetails;
import entities.Pattern;
import m2fedutils.FileUtils;
import m2fedutils.ResultStringUtils;

public class SensorDataManager {

    String watch_id;
    long lastServiceTime;
    int lastFileIndex;
    long[] lastTimes;
    float[][] lastAccels;
    int lastDataLength = 80;

    Pattern[] patterns;
    MealManager mealManager;

    public SensorDataManager(String wid) throws Exception {
        watch_id = wid;
        lastServiceTime = 0;
        lastFileIndex = 0;
        lastTimes = new long[lastDataLength];
        lastAccels = new float[3][lastDataLength];

        patterns = PatternManager.readPatterns();
        mealManager = new MealManager(watch_id);
    }

    public void processNewData(FileDetails fd) throws Exception {
        String[][] data = fd.data;
        int i, j, total = data.length;

        long[] times;
        float[][] accels;

        if (lastServiceTime == fd.service_start_time_watch && lastFileIndex == fd.file_index - 1) {
            System.out.print("Using data from previous file. Last timestamp: " + lastTimes[lastDataLength - 1]);
            times = new long[lastDataLength + total];
            accels = new float[3][lastDataLength + total];
            for (i = 0; i < lastDataLength; i++) {
                times[i] = lastTimes[i];
                accels[0][i] = lastAccels[0][i];
                accels[1][i] = lastAccels[1][i];
                accels[2][i] = lastAccels[2][i];
            }

            for (i = lastDataLength, j = 0; j < total; i++, j++) {
                times[i] = Long.parseLong(data[j][0]) + fd.sync_diff;
                accels[0][i] = Float.parseFloat(data[j][1]);
                accels[1][i] = Float.parseFloat(data[j][2]);
                accels[2][i] = Float.parseFloat(data[j][3]);
            }
            
        } else {
            times = new long[total];
            accels = new float[3][total];
            for (j = 0; j < total; j++) {
                times[j] = Long.parseLong(data[j][0]) + fd.sync_diff;
                accels[0][j] = Float.parseFloat(data[j][1]);
                accels[1][j] = Float.parseFloat(data[j][2]);
                accels[2][j] = Float.parseFloat(data[j][3]);
            }
        }
        
        System.out.println(", New start end timestamps: " + times[0] + ", " + times[times.length - 1]);
        fd.file_data_end_time_adjusted = times[times.length - 1];

        lastServiceTime = fd.service_start_time_watch;
        lastFileIndex = fd.file_index;
        for (i = 0, j = times.length - lastDataLength; i < lastDataLength; i++, j++) {
            lastTimes[i] = times[j];
            lastAccels[0][i] = accels[0][j];
            lastAccels[1][i] = accels[1][j];
            lastAccels[2][i] = accels[2][j];
        }

        ArrayList<Bite> bList = BiteDetector.findBites(times, accels, patterns);
        

        if (bList.size() > 0) {
            String res = ResultStringUtils.biteListToString(bList, fd.watch_id, fd.upload_time);
            FileUtils.appendToResultFile(MyConstants.FILE_TYPE_BITE, fd.file_start_time_watch_adjusted, fd.watch_id, res);
            mealManager.addBites(bList);
        }

        mealManager.process();
        
    }

}
