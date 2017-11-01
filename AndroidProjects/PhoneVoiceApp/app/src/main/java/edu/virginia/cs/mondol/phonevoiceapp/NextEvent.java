package edu.virginia.cs.mondol.phonevoiceapp;

/**
 * Created by TOSHIBA on 4/10/2016.
 */
public class NextEvent {
    public int index;
    public long absoluteTime;
    public long delay;

    public NextEvent(){
        index = 0;
        absoluteTime = 0;
        delay = 0;
    }


    public NextEvent(int i, long t, long d){
        index = i;
        absoluteTime = t;
        delay = d;
    }
}
