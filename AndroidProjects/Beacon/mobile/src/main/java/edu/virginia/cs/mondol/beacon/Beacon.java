package edu.virginia.cs.mondol.beacon;

/**
 * Created by Abu on 9/25/2017.
 */

public class Beacon {
    String mac;
    int counts=0;
    int totalRssi = 0;
    long lastTimeFound = 0;
    int lastMeanRssi = 0;
}
