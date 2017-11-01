package myutils;
// @author mm5gg

import java.util.ArrayList;

public class MyArrayUtils {
    
    public static int[] addArrays(int[] a, int[] b){
        int[] res = new int[a.length];
        for(int i=0;i<a.length;i++){
            res[i] = a[i] + b[i];
        }
        return res;
    }
    
    public static void printArray(int[] a){
        for(int i=0;i<a.length-1;i++){
            System.out.print(a[i]+", ");
        }
        
        System.out.println(a[a.length-1]);        
    }
    
    public static void printArray(float[] a){
        for(int i=0;i<a.length-1;i++){
            System.out.print(a[i]+", ");
        }
        
        System.out.println(a[a.length-1]);        
    }
    
    public static void printArray(int[][] a){
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length-1;j++){
                System.out.print(a[i][j]+", ");
            }
            
            System.out.println(a[i][a[i].length-1]);                    
        }
    }
    
    public static void printArray(float[][] a){
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length-1;j++){
                System.out.print(a[i][j]+", ");
            }
            
            System.out.println(a[i][a[i].length-1]);                    
        }
    }
    
    public static void printArray(String[][] a){
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length-1;j++){
                System.out.print(a[i][j]+", ");
            }
            
            System.out.println(a[i][a[i].length-1]);                    
        }
    }

    public static float[] hstack(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        int i;
        for (i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        for (i = 0; i < b.length; i++) {
            c[i + a.length] = b[i];
        }

        return c;
    }

    public static float[][] stringArrayToFloatArray(String[][] data) {
        int nrow = data.length;
        int ncol = data[0].length;
        int i, j, k;
        float[][] d = new float[nrow][ncol];

        for (i = 0; i < nrow; i++) {
            for (j = 0; j < ncol; j++) {
                d[i][j] = Float.parseFloat(data[i][j]);
            }
        }

        return d;
    }

    public static int[][] stringArrayToIntArray(String[][] data) {
        int nrow = data.length;
        int ncol = data[0].length;
        int i, j, k;
        int[][] d = new int[nrow][ncol];

        for (i = 0; i < nrow; i++) {
            for (j = 0; j < ncol; j++) {
                d[i][j] = Integer.parseInt(data[i][j]);
            }
        }

        return d;
    }

    public static float[][] fliplr(float[][] data) {
        int i, nrow = data.length;
        float[][] d = new float[nrow][];

        for (i = 0; i < nrow; i++) {
            d[i] = fliplr(data[i]);
        }
        return d;
    }

    public static float[] fliplr(float[] data) {
        int i, n = data.length;
        float[] d = new float[n];

        for (i = 0; i < n; i++) {
            d[i] = data[n - 1 - i];
        }

        return d;
    }

    public static float[][] transpose(float[][] data) {
        int nrow = data.length;
        int ncol = data[0].length;
        int i, j;
        float[][] d = new float[ncol][nrow];

        for (i = 0; i < nrow; i++) {
            for (j = 0; j < ncol; j++) {
                d[j][i] = data[i][j];
            }
        }
        return d;
    }    
   
    public static int[] intListToArray(ArrayList<Integer> list) {
        int size = list.size();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static float[][] floatArrayListToArray(ArrayList<float[]> list) {
        int size = list.size();
        float[][] arr = new float[size][];
        for (int i = 0; i < size; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static float[][] smooth_data(float[][] data, float factor){
        for(int i=0;i<data.length;i++)
            data[i] = smooth_data(data[i], factor);
        
        return data;
    }
    
    public static float[]smooth_data(float[] data, float factor){
        for(int i=1;i<data.length;i++){
            data[i] = factor*data[i-1] + (1-factor)*data[i];
        }
        return data;
    }
    
    public static float[][] normalize_data(float[][] data) {
        float[][] d = new float[data.length][data[0].length];
        float mag;
        for (int i = 0; i < data.length; i++) {
            mag = (float) Math.sqrt(data[i][0] * data[i][0] + data[i][1] * data[i][1] + data[i][2] * data[i][2]);
            d[i][0] = data[i][0] / mag;
            d[i][1] = data[i][1] / mag;
            d[i][2] = data[i][2] / mag;
        }

        return d;
    }

    public static float[][] normalize_horizontal_data(float[][] data) {
        float[][] d = new float[data.length][data[0].length];
        float mag;
        for (int i = 0; i < data[0].length; i++) {
            mag = (float) Math.sqrt(data[0][i] * data[0][i] + data[1][i] * data[1][i] + data[2][i] * data[2][i]);
            d[0][i] = data[0][i] / mag;
            d[1][i] = data[1][i] / mag;
            d[2][i] = data[2][i] / mag;
            mag = d[0][i] * d[0][i] + d[1][i] * d[1][i] + d[2][i] * d[2][i];
            if (Math.abs(mag - 1) > 0.01) {
                System.out.println("Error normalization: " + mag);
                System.exit(0);
            }
        }

        return d;
    }
    
    public static float[][] z_normalize_horizontal(float[][] data) {
        float[][] d = new float[data.length][];
        for(int i=0;i<data.length;i++)
            d[i] = z_normalize(data[i]);
        
        return d;
    }
    
    public static float[] z_normalize(float[] data) {
        int N  = data.length;
        float[] d = new float[N];
        float sumX = 0, var = 0, sumX2 = 0;        
        for (int i = 0; i <N; i++) {
            sumX += data[i];
            sumX2 += data[i] * data[i];
        }

        float mean = sumX / N;
        var = sumX2 / N - mean * mean;
        float std = (float)Math.sqrt(var);

        for (int i = 0; i <N; i++) {
            d[i] = (data[i]-mean)/std;            
        }
        
        return d;
    }
    
    public static float[][] combineArray(float[][] a, float[][] b) {
        float[][] c = new float[a.length + b.length][];
        int i, j;
        for (i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        for (j = 0; j < b.length; j++, i++) {
            c[i] = b[j];
        }

        return c;

    }
    
    public static int[][] floatToIntArray(float[][] a){
        if(a==null)
            return null;
        
        int r = a.length, c = a[0].length;
        int[][] res = new int[r][c];
        for(int i=0;i<r;i++)
            for(int j=0;j<c; j++)
                res[i][j] = (int)a[i][j];
        
        return res;
    }
}
