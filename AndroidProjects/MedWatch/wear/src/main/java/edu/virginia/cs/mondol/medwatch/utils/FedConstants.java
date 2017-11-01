package edu.virginia.cs.mondol.medwatch.utils;

/**
 * Created by Abu on 2/4/2017.
 */

public class FedConstants {
    public static final String SHARED_PREF = "edu.virginia.cs.mondol.fed";
    public static final String AT_HOME = "at_home";
    public static final String FED_CONFIG = "fed_config";
    public static final String NET_CONFIG = "net_config.txt";
    public static final String CONFIG_VERSION = "config_version";
    public static final String BITE_COUNT = "bite_count";
    public static final String LAST_UPLOAD_ATTEMPT_TIME = "last_upload_attempt_time";
    public static final String LAST_UPLOAD_ATTEMPT_RESULT = "last_upload_attempt_result";
    public static final String LAST_EPISODE_DURATION = "last_episode_duration";
    public static final String DISCARD_DURATION = "discard_duration";
    public static final String LAST_BITE_TIME = "last_bite_time";
    public static final String LAST_ALARM_TIME = "last_alerm_time";

    public static final String LAST_TIME_BLE_FOUND = "last_time_ble_found";
    public static final String LAST_TIME_BLE_STARTED = "last_time_ble_started";
    public static final String LAST_TIME_BLE_STOPPED = "last_time_ble_stopped";


    public static final String BATTERY_ALARM_TIME = "battery_alarm_time";


    public static final String TIME_SYNC_STATUS = "time_sync_status";
    public static final int CODE_TIME_SYNC_CALLED = 1;
    public static final int CODE_TIME_SYNC_FAILED = 2;
    public static final int CODE_TIME_SYNC_SUCCESS = 3;

    public static final int WATCH_INFO_CODE_START = 101;
    public static final int WATCH_INFO_CODE_STOP = 102;
    public static final int WATCH_INFO_CODE_PCON = 201;
    public static final int WATCH_INFO_CODE_DCON = 202;
    public static final int WATCH_INFO_CODE_BOOT = 300;
    public static final int WATCH_INFO_CODE_BATTERY = 401;
    public static final int WATCH_INFO_CODE_BATTERY_ALARM = 402;
    public static final int WATCH_INFO_CODE_PLUGGED = 501;
    public static final int WATCH_INFO_CODE_NOT_PLUGGED = 502;
    public static final int WATCH_INFO_CODE_BEACON = 1;


    public static final String TIME_SYNC_SERVER_CALL = "time_sync_server_call";
    public static final String TIME_SYNC_SERVER_RESPONSE = "time_sync_server_response";
    public static final String TIME_SYNC_SERVER = "time_sync_server";
    public static final String TIME_SYNC_WATCH = "time_sync_base_station";
    public static final String TIME_SYNC_DIFF = "time_sync_diff";
    public static final String TIME_SYNC_RESPONSE_PERIOD = "time_sync_response_period";

    public static final String BOOT = "boot";
    public static final String PCON = "pcon";
    public static final String DCON = "dcon";
    public static final String BROADCAST_FOR_BLE = "broadcast_for_ble";
    public static final String MESSAGE = "message";
    public static final String CODE = "code";


    public static final int DOWNLOAD_TYPE_TIME = 1;
    public static final int DOWNLOAD_TYPE_PARAMS = 2;
    public static final int CODE_SAVE_BLE = 1;


    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String MYTAG = "MyTAG";
    public static final String BEACON = "beacon";
    public static final String BLE_SAVE = "ble_save";
    public static final String WATCH_ID = "watch_id";
    public static final String DOWNLOAD_TIME = "download_time";
    public static final String DOWNLOAD_CONFIG = "broadcast_config";

    public static final String UPLOAD_WORKING = "upload_working";
    public static final String DOWNLOAD_WORKING = "download_working";

    public static final String TURN_ON = "TURN_ON";
    public static final String TURN_OFF = "TURN_OFF";
    public static final long NANO_MILLI_FACTOR = 1000000;

    public static final String WIFI_SSID = "wifi_ssid";
    public static final String WIFI_PASSWORD = "wifi_password";
    public static final String SERVER_IP = "server_ip";

    //public static final String SSID_M2FED ="\"M2FED\"";
    //public static final String SSID_WAHOO ="\"wahoo\"";
    //public static final String SSID_NAME = SSID_M2FED;

    //public static final String SERVER_URL_ROOT = "http://mooncake.cs.virginia.edu/m2fed/";
    //public static final String SERVER_URL_ROOT = "http://192.168.0.100/m2fed_watch/";
    //public static final String SERVER_URL_ROOT = "http://172.26.6.218/m2fed_watch/";

    public static final String SERVER_URL_ROOT = ""; //http://192.168.0.108/m2fed_watch/";
    public static final String SERVER_URL_CONNECTION_TEST = SERVER_URL_ROOT
            + "conn_test.php";
    public static final String SERVER_URL_DOWNLOAD_TIME = SERVER_URL_ROOT
            + "get_time.php";
    public static final String SERVER_URL_DOWNLOAD_PARAMS = SERVER_URL_ROOT
            + "get_config.php";
    public static final String SERVER_URL_DOWNLOAD_PATTERNS = SERVER_URL_ROOT
            + "get_patterns.php";
    public static final String SERVER_URL_UPLOAD_FILE = SERVER_URL_ROOT
            + "upload_data.php";

    public static String BLE_MAC_INDICES_STRING = "ble_mac_indices_string";
    public static String[] BLE_MAC_LIST_ALL = {
            "d4c416bf5385", "c196b85904c4", "fd7676b335a3", "f70b5243e7b3", "fc4a94985124", "e5830df962f0",
            "f563f3c569d7", "d0d70c19c171", "cf247561d166", "c83bf6930eba", "f6881c4e7ae4", "d61831d4cc96",
            "984fee0f8605","f5d5fa52b881",
            "f9123090c43f", "c8d3064862bc", "fbd8d4d9b31a"
    };

    public static String PATTERN_VERSION = "pattern_version";
    public static String PATTERN_SIZE = "pattern_size";

    public static String MONITOR = "monitor";
    public static int MONITOR_TYPE_NET_CONFIG = 1;
    public static int MONITOR_TYPE_SERVER_SYNC = 2;

    public static String PATTERN_USE = "pattern_use";
    public static String AT_HOME_ONLY = "at_home_only";
    public static String TEST_MEALS = "test_meals";

    public static String RUNNING_BLE_FILENAME = "running_ble_filename";
    public static String RUNNING_SENSOR_FILENAME = "running_sensor_filename";


}
