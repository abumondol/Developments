package entities;
// @author mm5gg

import java.io.File;
import m2fedutils.FileUtils;
import constants_config.MyConstants;

public class FileDetails {

    public File file;
    public String file_name;

    public int file_type;
    public String watch_id;
    public long reference_time_server;
    public long reference_time_watch;
    public long file_start_time_watch;
    public long service_start_time_watch;
    public int file_index;
    public long file_start_time_watch_adjusted;
    public long file_data_end_time_adjusted;
    public long sync_diff;

    public long upload_time;

    public String[][] data;
    public String home_id = "1";
    public String person_id;

    public FileDetails(File f) throws Exception {
        file = f;
        file_name = file.getName();
        String[] str = file_name.split("-");

        if (str[0].equals("sensor")) {
            file_type = MyConstants.UPLOAD_FILE_TYPE_SENSOR;
        } else if (str[0].equals("ble")) {
            file_type = MyConstants.UPLOAD_FILE_TYPE_BLE;
        } else {
            file_type = MyConstants.UPLOAD_FILE_TYPE_OTHER;
        }

        watch_id = str[1];
        reference_time_server = Long.parseLong(str[2]);
        reference_time_watch = Long.parseLong(str[3]);
        service_start_time_watch = Long.parseLong(str[4]);
        file_start_time_watch = Long.parseLong(str[5]);
        file_index = Integer.parseInt(str[6]);
        sync_diff = reference_time_server - reference_time_watch;
        file_start_time_watch_adjusted = file_start_time_watch + sync_diff;
        upload_time = System.currentTimeMillis();

        data = FileUtils.readCSV(file, true);

    }

}
