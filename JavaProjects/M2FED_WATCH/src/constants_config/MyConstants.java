package constants_config;

public class MyConstants {

    public static final int UPLOAD_FILE_TYPE_SENSOR = 1;
    public static final int UPLOAD_FILE_TYPE_BLE = 2;
    public static final int UPLOAD_FILE_TYPE_OTHER = 0;

    public static final int FILE_TYPE_BATTERY = 101;
    public static final int FILE_TYPE_BEACON = 102;
    public static final int FILE_TYPE_LOCATION = 103;
    public static final int FILE_TYPE_BITE = 201;
    public static final int FILE_TYPE_MEAL = 202;
    public static final int FILE_TYPE_EMA = 203;

    public static int DBSCAN_NOISE = 0;
    public static int DBSCAN_REACHABLE = 1;
    public static int DBSCAN_CORE = 2;

    public static String[] BLE_MAC_LIST_ALL = {
        "d4c416bf5385", "c196b85904c4", "fd7676b335a3", "f70b5243e7b3", "fc4a94985124", "e5830df962f0",
        "f563f3c569d7", "d0d70c19c171", "cf247561d166", "c83bf6930eba", "f6881c4e7ae4", "d61831d4cc96",
        "f9123090c43f", "c8d3064862bc", "fbd8d4d9b31a"
    };

}
