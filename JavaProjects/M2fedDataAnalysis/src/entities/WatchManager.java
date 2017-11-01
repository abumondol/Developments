package entities;

import myutils.MyDeviceUtils;
import myutils.MyFileUtils;

/**
 *
 * @author mm5gg
 */
public class WatchManager {

    public static Watch[] watches() {
        Watch[] watches = new Watch[MyDeviceUtils.watchListUSC.length];
        for (int i = 0; i < watches.length; i++) {
            watches[i] = new Watch();
        }

        return watches;
    }

    public static void writeBeaconData(Watch[] watches) throws Exception{
        for (int i = 0; i < watches.length; i++) {
            String data = watches[i].beaconData.toString();
            if(data.length()>0){
                String filename = "results\\beacon data watch "+(i+1)+".csv";
                MyFileUtils.writeToFile(filename, data);
            }
        }
    }

}
