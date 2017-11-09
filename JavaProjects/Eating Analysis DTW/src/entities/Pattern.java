package entities;

import constants_config.MyParameters;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pattern.PatternUtils;

public class Pattern implements Serializable {

    public static int PATTERN_TYPE_NEG = 0;
    public static int PATTERN_TYPE_POS = 1;
    public static int PATTERN_TYPE_X = 2;

    public float[][] data;
    public float[][] dataNormalized;

    //pattern info in subject data
    public int sessionIndex;
    public int minPointIndex;
    public float minPointTime;

    // feature of the pattern
    public int label = 0;
    public float minPointXVal;
    public float stdev;

    //features pattern testing    
    public int[][] coverageCountBins;
    public float[] binXDiffMax;
    
    //processing 
    public int listIndex;

    //results
    public float nearestDistance;
    public int nearestPatternIndex;
    public Pattern nearestPattern;
    public float biteScore;

    public Pattern() {
        coverageCountBins = new int[3][MyParameters.BIN_COUNT];
        binXDiffMax = new float[MyParameters.BIN_COUNT];
    }

}
