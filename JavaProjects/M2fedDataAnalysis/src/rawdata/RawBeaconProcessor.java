package rawdata;

import entities.Watch;
import entities.WatchManager;
import java.io.File;
import myutils.MyDateTimeUtils;
import myutils.MyDeviceUtils;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class RawBeaconProcessor {

    public static void processBeaconData(String folderPath) throws Exception {
        File folder = new File(folderPath + "/Eating/beacon");
        File[] fileList = folder.listFiles();

        int i, j, k, watchIndex, beaconIndex;
        String[][] d;
        String[] str;
        String watchSerial, mac, dateTime;
        Watch[] watches = WatchManager.watches();

        for (i = 0; i < fileList.length; i++) {
            str = fileList[i].getName().split("_");
            watchSerial = str[3];
            watchIndex = MyDeviceUtils.getWatchIndex(watchSerial);

            d = MyFileUtils.readCSV(fileList[i], false);
            for (j = 0; j < d.length; j++) {
                mac = d[j][0];
                beaconIndex = MyDeviceUtils.getBeaconIndex(mac);
                dateTime = MyDateTimeUtils.getDateTimeString(Long.parseLong(d[j][2]), 3);
                watches[watchIndex].beaconData.append(dateTime + "," + (beaconIndex + 1) + "," + d[j][3] + "," + d[j][4]+"\n");
            }
        }
        
        WatchManager.writeBeaconData(watches);

    }

}
