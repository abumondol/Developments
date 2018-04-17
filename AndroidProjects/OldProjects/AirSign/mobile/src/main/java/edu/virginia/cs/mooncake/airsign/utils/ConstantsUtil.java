package edu.virginia.cs.mooncake.airsign.utils;

public class ConstantsUtil {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String USERID = "user_id";
    public static final String CONFIG = "config";
    public static final String TYPE = "type";
    public static final String DATA = "data";
    public static final String TIME = "time";
    public static final String LAST_TIME = "last_time";
    public static final String TYPE_ACL = "acl";
    public static final String TYPE_TRAIN = "train";
    public static final String TYPE_TEST = "test";
    public static final String TYPE_ACTIVITY = "activity";
    public static final long NANO_MILI_FACTOR = 1000000L;

    /**
     * **************** BUNDLE FIELDS ***************
     */
    public static final String BUNDLE_CONFIG = CONFIG;
    public static final String BUNDLE_USERID = USERID;

    /**
     * ******************* JSON FIELDS **************
     */
    public static final String JSON_RESULT = "result";
    public static final String JSON_CONFIG = CONFIG;
    public static final String JSON_LAST_TIME = LAST_TIME;
    public static final String JSON_TYPE_ACL = TYPE_ACL;
    public static final String JSON_TYPE_TRAIN = TYPE_TRAIN;
    public static final String JSON_USERID = USERID;

    public static final String JSON_ACTIVITY_LIST = "activity_list";
    public static final String JSON_POSITION_LIST = "position_list";
    public static final String JSON_SUBJECT_LIST="subject_list";
    public static final String JSON_ACTIVITY = "activity";
    public static final String JSON_POSITION = "position";
    public static final String JSON_CODE = "code";
    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";

    public static final String JSON_ACL_RATE = "acl_rate";
    public static final String JSON_GYRO_RATE = "gyro_rate";
    public static final String JSON_FEATURE_CODE = "feature_code";

    /**
     * ******* Database Constants ***********
     */
    public static final String DB_DATABASE_NAME = "alldata";
    public static final int DB_DATABASE_VERSION = 1;
    public static final String DB_TABLE_DATA = "table_data";
    public static final String DB_COLUMN_TYPE = TYPE;
    public static final String DB_COLUMN_TIME = TIME;
    public static final String DB_COLUMN_DATA = DATA;

    /**
     * ******* Intent Constants ***********
     */
    public static final String INTENT_EMAIL = EMAIL;
    public static final String INTENT_PASSWORD = PASSWORD;
    public static final String INTENT_RESULT_RECEIVER = "resultReceiver";
    public static final String INTENT_TIME = TIME;
    public static final String INTENT_DATA = DATA;
    public static final String INTENT_TYPE = TYPE;
    public static final String INTENT_SERVICE_SETTINGS_CHANGE = "service_settings";
    public static final String INTENT_SERVICE_START_TRAIN = "start_service_train";
    public static final String INTENT_SERVICE_START_TEST = "start_service_test";
    public static final String INTENT_SERVICE_START = "start_service";
    public static final String INTENT_SERVICE_STOP = "stop_service";
    public static final String INTENT_TRAIN_START = "train_start";
    public static final String INTENT_TRAIN_STOP = "train_stop";
    public static final String INTENT_CHECK_BOX = "check_box";

    /**
     * ************** Shared Preference Keys ***********
     */
    public static final String SHARED_PREF = "edu.virginia.cs.mooncake.activolt";
    public static final String SPK_USERID = USERID;
    public static final String SPK_EMAIL = EMAIL;
    public static final String SPK_CONFIG = CONFIG;
    public static final String SPK_TIMER_START_TIME = "timer_start_time";
    public static final String SPK_TIMER_POSITION = "timer_position";
    public static final String SPK_TIMER_ACTIVITY_INDEX = "timer_activity_index";
    public static final String SPK_ACL_RATE = "acl_rate";
    public static final String SPK_GYRO_RATE = "gyro_rate";
    public static final String SPK_FEATURE_CODE = "feature_code";
    public static final String SPK_ACTIVITY_CLASS = "activity_class";
    public static final String SPK_CHECK_BOX_TRAIN = "train_check_box";

    /**
     * ****************** Internet Constants *********************
     */
    public static final String SERVER_NVP_EMAIL = EMAIL;
    public static final String SERVER_NVP_PASSWORD = PASSWORD;
    public static final String SERVER_NVP_JSON = "json";

    public static final String SERVER_URL_ROOT = "http://mooncake.cs.virginia.edu:9000/";
    public static final String SERVER_URL_INSERT = SERVER_URL_ROOT
            + "airsign/insertdata";
    public static final String SERVER_URL_LOGIN = SERVER_URL_ROOT
            + "airsign/login";
    public static final String SERVER_URL_CONNECTION_TEST = SERVER_URL_ROOT
            + "connectiontest";
    public static final String SERVER_URL_UPLOAD_FILE = SERVER_URL_ROOT
            + "airsign/upload";

    /**
     * *********************** Array Constants *********************
     */
    public static final String TRAIN_ACTIVITY_LIST[] = {"Still", "Sitting",
            "Walk", "Run", "Staircase (up)", "Staircase (down)", "Jog",
            "Cycling", "Ride"};

    public static final String TRAIN_POSITION_LIST[] = {"", "Pant Pocket",
            "Shirt Pocket", "Hand Grabbed", "Hand Wrist", "Hand Arm",
            "Hand Bag", "Back Bag", "Belt"};
}
