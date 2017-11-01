package data_process;

import entities.BeaconReading;
import java.util.ArrayList;
import entities.FileDetails;
import m2fedutils.DateTimeUtils;
import m2fedutils.FileUtils;
import constants_config.MyConstants;

public class Watch {

    public String watch_id;
    public BeaconDataManager bdm;
    public SensorDataManager sdm;

    FileDetails lastSensorFd;

    public Watch(String wid) throws Exception {
        watch_id = wid;
        bdm = new BeaconDataManager(watch_id);
        sdm = new SensorDataManager(watch_id);
    }

    public void processUploadedFile(FileDetails fd) throws Exception {
        System.out.print(fd.file_name);
        System.out.print(", " + DateTimeUtils.getDateTimeString(fd.upload_time, 3));
        System.out.println(", " + DateTimeUtils.getDateTimeString(fd.file_start_time_watch_adjusted, 3) + ", " + fd.data.length);
        switch (fd.file_type) {
            case MyConstants.UPLOAD_FILE_TYPE_BLE:
                processBleFile(fd);
                break;
            case MyConstants.UPLOAD_FILE_TYPE_SENSOR:
                sdm.processNewData(fd);
                break;
            default:
                System.out.println("File Type doesn't match. File Type: " + fd.file_type);

        }
    }

    void processBleFile(FileDetails fd) throws Exception {
        String[][] data = fd.data;
        StringBuilder sb = new StringBuilder();
        int i, code;        
        long lastBatteryTime = 0;
        ArrayList<BeaconReading> bleReadList = new ArrayList<>();
        BeaconReading br;
        String lastBatteryPct = "";

        for (i = 0; i < data.length; i++) {
            code = Integer.parseInt(data[i][1]);
            if (code == 1) {
                br = new BeaconReading(data[i]);
                br.time = br.time + fd.sync_diff;
                bleReadList.add(br);
            } else if (code > 1 && code < 1000) {
                lastBatteryTime = Long.parseLong(data[i][0]) + fd.sync_diff;
                sb.append(lastBatteryTime);
                sb.append(",");
                sb.append(data[i][1]);
                sb.append(",");
                sb.append(data[i][2]);
                sb.append(",");
                sb.append(data[i][3]);
                sb.append("\n");
                lastBatteryPct = data[i][3];

            } else {
                System.out.println("Error in ble file code");
            }
        }

        if (sb.length() > 0) {
            System.out.print("Battery info count:" + (data.length - bleReadList.size()) + ", ");
            System.out.println(", Last Pct at " + DateTimeUtils.getDateTimeString(lastBatteryTime, 3) + ": " + lastBatteryPct);
            FileUtils.appendToResultFile(MyConstants.FILE_TYPE_BATTERY, fd.file_start_time_watch_adjusted, watch_id, sb.toString());
        }

        if (bleReadList.size() > 0) {
            bdm.addNewData(fd, bleReadList);
            System.out.print("Beacon reading count:" + bleReadList.size());
            System.out.println(", Last BLE Reading at:" + DateTimeUtils.getDateTimeString(bleReadList.get(bleReadList.size() - 1).time, 3));
        }
    }
   
}
