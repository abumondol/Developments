/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class DTW {

    public static float distance(float[][] t, float[][] r) {
        int N = t[0].length;
        int M = r[0].length;
        
        float[][] d = new float[N][M];
        float[][] D = new float[N][M];

        int n, m;
        for (n = 0; n < N; n++) {
            for (m = 0; m < M; m++) {
                d[n][m] = 1 - (t[0][n] * r[0][m] + t[1][n] * r[1][m] + t[2][n] * r[2][m]);
                if(d[n][m]<-0.001){
                    System.out.println(String.format("Distance error:%f", d[n][m]));
                    System.out.println(String.format("T: %f, %f, %f",t[0][n], t[1][n], t[2][n]));
                    System.out.println(String.format("R: %f, %f, %f",r[0][m], r[1][m], r[2][m]));
                    
                    System.exit(0);
                }
            }
        }


        D[0][0] = d[0][0];

        for (n = 1; n < N; n++) {
            D[n][0] = d[n][0] + D[n - 1][0];
        }

        for (m = 1; m < M; m++) {
            D[0][m] = d[0][m] + D[0][m - 1];
        }

        for (n = 1; n < N; n++) {
            for (m = 1; m < M; m++) {
                D[n][m] = d[n][m] + min(D[n - 1][m], D[n][m - 1], D[n - 1][m - 1]);
            }
        }
        
        return D[N-1][M-1];
    }

    public static float min(float a, float b, float c) {
        if (a <= b && a <= c) {
            return a;
        }
        if (b <= a && b <= c) {
            return b;
        }
        return c;
    }

    
    /*float DTW(float[][] s1, float[][] s2) {
        //System.out.println(s1[0].length + ", " + s2[0].length);
        if (s1[0].length != s2[0].length) {
            System.out.println("Lengths are not equal.");
        }

        float d = 0, prox;
        for (int i = 0; i < s1[0].length; i++) {
            prox = s1[0][i] * s2[0][i] + s1[1][i] * s2[1][i] + s1[2][i] * s2[2][i];
            d += (1 - prox);
        }

        return d;
    }*/
}
