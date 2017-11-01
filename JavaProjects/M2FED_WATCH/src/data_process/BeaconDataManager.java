package data_process;

import entities.BeaconReading;
import java.util.ArrayList;
import entities.FileDetails;
import constants_config.MyConstants;
import m2fedutils.FileUtils;

//@author mm5gg 
public class BeaconDataManager {

    String watch_id;
    String[] macList;
    int[] deployedMacNumbers = {13, 14, 15};
    String[] roomList = {"Living Room", "Kitchen", "Bed Room"};

    ArrayList<BeaconReading> readingList;

    public BeaconDataManager(String wid) {
        watch_id = wid;
        int beaconCount = deployedMacNumbers.length;
        System.out.println("Initiating Bacon Manager. BeaconCount: " + beaconCount);

        macList = new String[beaconCount];
        for (int i = 0; i < beaconCount; i++) {
            int mc = deployedMacNumbers[i] - 1;
            macList[i] = MyConstants.BLE_MAC_LIST_ALL[mc];
            System.out.println(roomList[i] + ": " + deployedMacNumbers[i] + ", " + macList[i]);
        }

    }

    public void addNewData(FileDetails fd, ArrayList<BeaconReading> brList) throws Exception {
        if (brList.isEmpty()) {
            return;
        }

        sortBeaconData(brList);
        StringBuilder sb = new StringBuilder();
        for (BeaconReading br : brList) {
            sb.append(br.mac);
            sb.append(",");
            sb.append(fd.watch_id);
            sb.append(",");
            sb.append(br.time);
            sb.append(",");
            sb.append(br.txPower);
            sb.append(",");
            sb.append(br.rssi);            
            sb.append("\n");
        }

        if (sb.length() > 0) {
            FileUtils.appendToResultFile(MyConstants.FILE_TYPE_BEACON, System.currentTimeMillis(), fd.watch_id, sb.toString());
        }

        sb.setLength(0);

        ArrayList<BeaconReading> list = new ArrayList<>();
        list.add(brList.get(0));
        for (int i = 1; i < brList.size(); i++) {
            if (brList.get(i).time > brList.get(i - 1).time + 10 * 1000) {
                sb.append(fd.watch_id + "," + processLocation(list));
                list.clear();;
            }
            list.add(brList.get(i));
        }

        sb.append(fd.watch_id + "," + processLocation(list));
        
        if (sb.length() > 0) {
            FileUtils.appendToResultFile(MyConstants.FILE_TYPE_LOCATION, System.currentTimeMillis(), fd.watch_id, sb.toString());
        }


    }

    String processLocation(ArrayList<BeaconReading> list) {
        BeaconReading max = list.get(0);
        for (BeaconReading b : list) {
            if (b.rssi > max.rssi) {
                max = b;
            }
        }

        return max.mac+","+"0,"+max.time+","+max.time+"0\n";
    }

    void sortBeaconData(ArrayList<BeaconReading> brList) {
        boolean swapped;
        int len = brList.size();
        BeaconReading tempBr;
        do {
            swapped = false;
            for (int i = 1; i < len; i++) {
                if (brList.get(i - 1).time > brList.get(i).time) {
                    tempBr = brList.remove(i);
                    brList.add(i, brList.get(i - 1));
                    brList.remove(i - 1);
                    brList.add(i - 1, tempBr);
                    swapped = true;
                }
            }
        } while (swapped);
    }

}
