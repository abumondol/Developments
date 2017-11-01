package edu.virginia.cs.mondol.fed.bites;

/**
 * Created by Abu on 3/12/2017.
 */

public class Bite {
    public int index = 0;
    public long time;
    public int type = 0;
    public int status = 0;
    public float minXVal = 0;
    public int patternIndex = 0;

    public Bite(){
        this.index = 0;
        this.time = 0;
        this.type = 0;
        this.status = 0;
        this.minXVal = 0;
    }

    public Bite(int index, long time, int type, int status){
        this.index = index;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    public Bite getClone(){
        Bite b = new Bite();
        b.index = index;
        b.time = time;
        b.type = type;
        b.status = status;
        b.minXVal = minXVal;
        return b;
    }
}
