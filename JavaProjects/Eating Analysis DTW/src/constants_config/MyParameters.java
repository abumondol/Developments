package constants_config;

public class MyParameters {
    public static int LEFT_LENGTH = 48;
    public static int RIGHT_LENGTH = 32;
    
    //Preprocessing paramteres
    public static float XTH = (float)-2.5;    
    public static int MIN_BITE_INTERVAL = 40; //2.5 * 16
    public static int MAX_ANNOT_DISTANCE = 32;
    public static int EXCLUDE_ANNOT_DISTANCE = 64;    
    public static float MIN_STDEV = 1;
    public static float SMOOTH_FACTOR = (float)0.85;
    
    public static int TEST_ANNOT_DISTANCE = 48;
    
    //Pattern matching parameters
    public static float TAINING_DISTANCE = (float)1.0;
    public static float RETAINING_DISTANCE = (float)2.0;        
    public static float DISTANCE_MINX_DIFF = 2;
    
       
    public static float SCORE_THRESHOLD = (float)0.5;
    public static float XWEIGHT = (float)0.75;
    
    public static float BIN_SIZE= (float)0.25;
    public static int BIN_COUNT = 16;
    public static float MAX_BIN_DISTANCE = BIN_SIZE*BIN_COUNT;
    
}
