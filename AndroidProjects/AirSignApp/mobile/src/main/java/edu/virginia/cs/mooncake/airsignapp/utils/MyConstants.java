package edu.virginia.cs.mooncake.airsignapp.utils;

import android.os.Environment;

public class MyConstants {
    public static final String SHARED_PREF_TEMP = "edu.virginia.cs.mooncake.airsign";
    public static final String SHARED_PREF_STORED = "edu.virginia.cs.mooncake.airsign.stored";

    public static final String PATH_AIRSIGN = Environment.getExternalStorageDirectory() + "/AirPass";
    public static final String PATH_DATA_FOLDER = PATH_AIRSIGN + "/users";
    public static final String PATH_PASSWORDS_FOLDER = PATH_AIRSIGN + "/password";

    public static final String TYPE = "type";
    public static final String USER = "user";
    public static final String IMPOSTER = "imposter";
    public static final String PASSWORD = "password";
    public static final String PASSWORD_INDEX = "password_index";
    public static final String TIME = "time";
    public static final String SESSION_START_TIME = "session_start_time";
    public static final String START_TIME = "start_time";
    public static final String BYTE_DATA = "byte_data";
    public static final String FILE_NAME = "file_name";

    public static final String UPLOAD = "upload";
    public static final String UPLOAD_RESULT = "upload_result";


    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";

    public static final String TRAIN = "train";
    public static final String TRAIN_SUCCESS = "train_success";
    public static final String TEST = "test";
    public static final String TEST_SUCCESS = "test_success";

    public static final String BCAST_TRAIN = "bcast_train";
    public static final String BCAST_TEST = "bcast_test";
    public static final String BCAST_FALSE_TEST = "bcast_false";
    public static final String BCAST_DATA_RECEIVED = "bcast_data_received";
    public static final String BCAST_DATA_UPLOAD = "bcast_data_upload";

    public static final String MESSAGE = "message";
    public static final String START = "start";
    public static final String STOP = "stop";

    public static final String RAWDATA = "rawdata";
    public static final String LOG = "log";
    public static final String TRAIN_LOG = "train-log";
    public static final String TEST_LOG = "test-log";
    public static final String TEMPLATES = "templates";
    public static final String DISTANCES = "distances";

    public static final String TAG = "tag";
    public static final String ATTACKER = "attacker";
    public static final String ATTACK_TYPE= "attack_type";

    public static final String FALSE_TEST= "false_test";
    public static final String THRESHOLD= "threshold";

    public static final String MYTAG = "MyTAG";

    public static final String SERVER_URL_ROOT = "http://mooncake.cs.virginia.edu/airpass/";
    public static final String SERVER_URL_CONNECTION_TEST = SERVER_URL_ROOT
            + "conn_test.php";
    public static final String SERVER_URL_UPLOAD_FILE = SERVER_URL_ROOT
            + "upload_data.php";

}
