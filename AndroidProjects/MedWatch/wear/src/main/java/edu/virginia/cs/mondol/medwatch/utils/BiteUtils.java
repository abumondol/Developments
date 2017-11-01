package edu.virginia.cs.mondol.medwatch.utils;

/**
 * Created by Abu on 2/7/2017.
 */

public class BiteUtils {

    public static boolean detectMealStart(int[] biteResults, int index){
        return true;
    }

    public static int detectBite(int[][] arr){
        int result = 0, i, j;
        int min=1000;
        int N = arr.length;
        float mean1 = 0, mean2=0, mean3 = 0, var1 = 0, var2=0, var3=0, var, a;
        int s1 = N/3;
        int s2 = 2*N/3;

        for(i=s1; i<=s2; i++){
            if(arr[i][0]<min)
                min = arr[i][0];
        }

        if(min <= -2500)
            return 1;

        for(i=0; i<N; i++){
            mean1 += arr[i][0];
            mean2 += arr[i][1];
            mean3 += arr[i][2];
        }

        mean1 = mean1/N;
        mean2 = mean2/N;
        mean3 = mean3/N;

        for(i=0; i<N; i++){
            a= arr[i][0]-mean1;
            var1 += a*a;

            a= arr[i][1]-mean2;
            var2 += a*a;

            a= arr[i][2]-mean3;
            var3 += a*a;

        }

        var = (var1+var2+var3)/N;

        return result;
    }
}
