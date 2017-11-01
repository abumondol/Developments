package edu.virginia.cs.mooncake.m2feddata.myutils;

/**
 * Created by TOSHIBA on 5/19/2016.
 */
public class M2FEDUtil {

    public static String getFormattedNumber(int num){
        String s =""+num;
        if(num<10)
            s = "0"+num;

        return s;
    }
}
