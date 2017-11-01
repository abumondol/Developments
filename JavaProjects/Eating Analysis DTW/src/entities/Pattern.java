package entities;

import constants_config.MyParameters;
import java.io.Serializable;
import java.util.ArrayList;
import pattern.PatternUtils;

public class Pattern implements Serializable {

    public static int PATTERN_TYPE_NEG = 0;
    public static int PATTERN_TYPE_X = 1;
    public static int PATTERN_TYPE_POS = 2;

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

    //coverage for training    
    public boolean isCovered = false;
    public boolean isSelected = false;
    public ArrayList<Pattern> coveredList = new ArrayList<>();

    //features pattern testing    
    public int[] coverageCounts = {0, 0, 0};    
    
    //processing 
    public int listIndex;

    //results
    public float nearestDistance;
    public int nearestPatternIndex;
    public Pattern nearestPattern;
    public float biteScore;
    

    public void increaseCoverageCount(int patternType) {
        coverageCounts[patternType]++;        
    }

    public int totalCoverageCount() {
        return coverageCounts[0] + coverageCounts[1] + coverageCounts[2];
    }

}
