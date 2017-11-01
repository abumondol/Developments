/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwash;

import java.io.File;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class DataProcessor {

    public static void processData() throws Exception {
        String folder = "C:\\Users\\mm5gg\\Box Sync\\Data Sets\\Hand wash\\Dispenser with Beacon data\\Test on Sep 9 2017 Beacon";
        String[] subjects = {"chris", "zeya"};

        long[] clock_time_watch = {1504900827428L, 1504900827468L};
        long[] clock_time_vid = {4000L, 17000L};
        long[] up_time_clock = {95615574L, 95092286L};
        long[] up_time_vid = {6000L, 17000L};

        String[] macs = {"db4774f579d4", "e8faa71b16dd"};

        for (int i = 0; i < subjects.length; i++) {
            File subFolder = new File(folder + "\\" + subjects[i]);
            File[] files = subFolder.listFiles();
            for (int j = 0; j < files.length; j++) {
                String[][] data = MyFileUtils.readCSV(files[j], false);
                if (files[j].getName().startsWith("beacon")) {

                    for (int k = 0; k < macs.length; k++) {
                        String s = processBeaconData(data, macs[k], clock_time_watch[i], clock_time_vid[i]);
                        MyFileUtils.writeToFile(folder + "//" + "beacon_" + subjects[i] + "_" + k + "_.csv", s);
                    }
                }
            }
        }
    }

    public static String processBeaconData(String[][] data, String mac, long twatch, long tvid) {
        StringBuilder sb = new StringBuilder();
        long t;
        for (int i = 0; i < data.length; i++) {
            if (data[i][1].equals(mac)) {
                t = Long.parseLong(data[i][0]);
                t = t - twatch + tvid;
                sb.append(t / 1000.0);
                sb.append(",");
                sb.append(data[i][4]);
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

}
