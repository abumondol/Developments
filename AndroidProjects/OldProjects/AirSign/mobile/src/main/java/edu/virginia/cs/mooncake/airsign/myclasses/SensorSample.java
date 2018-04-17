package edu.virginia.cs.mooncake.airsign.myclasses;

import java.io.Serializable;

public class SensorSample implements Serializable {
	public static final long serialVersionUID =2041459669741987835L;
    public int sensorType;
    public long timeStamp;
    public int accuracy;
    public float[] values;
}
