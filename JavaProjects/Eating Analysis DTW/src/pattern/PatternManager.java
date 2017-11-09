package pattern;

import constants_config.MyParameters;
import entities.Pattern;
import myutils.MyArrayUtils;
import myutils.MyMathUtils;

public class PatternManager {

    public static Pattern[] getPatterns(float[] accelTime, float[][] accelData, int[] indices) throws Exception {
        Pattern[] pats = new Pattern[indices.length];

        for (int i = 0; i < indices.length; i++) {
            pats[i] = getPattern(accelData, indices[i]);
            if (accelTime != null) {
                pats[i].minPointTime = accelTime[pats[i].minPointIndex];
            }
        }

        return pats;
    }

    public static Pattern getPattern(float[][] accelData, int index) {
        Pattern p = new Pattern();

        p.data = new float[3][MyParameters.LEFT_LENGTH + MyParameters.RIGHT_LENGTH + 1];
        for (int k = index - MyParameters.LEFT_LENGTH, n = 0; k <= index + MyParameters.RIGHT_LENGTH; k++, n++) {
            p.data[0][n] = accelData[0][k];
            p.data[1][n] = accelData[1][k];
            p.data[2][n] = accelData[2][k];
        }

        p.dataNormalized = MyArrayUtils.normalize_horizontal_data(p.data);        
        p.minPointIndex = index;
        p.minPointXVal = accelData[0][index];
        p.stdev = MyMathUtils.totalStdev(p.data, 0, p.data[0].length - 1);
        return p;
    }

    
}
