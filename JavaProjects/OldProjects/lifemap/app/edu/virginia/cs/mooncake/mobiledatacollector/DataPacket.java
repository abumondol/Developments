package edu.virginia.cs.mooncake.mobiledatacollector;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Md.AbuSayeed on 9/13/2014.
 */
public class DataPacket implements Serializable{
	public static final long serialVersionUID = -5447471022077945848L;
    public String code;
    public ArrayList<SensorSample> list;
}
