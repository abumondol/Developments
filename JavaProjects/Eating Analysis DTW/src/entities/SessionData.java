package entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class SessionData implements Serializable {
    public int datasetId;
    public int subjectId;
    public int sessionId;
    
    public float[] accelTime;
    public float[][] accelData;
    public int[][] annots;
    public int[][] meals;
}
