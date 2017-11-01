package m2fedutils;
// @author mm5gg

import java.util.ArrayList;

public class MyArrayUtils {

    public static String stringArrayToString(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(arr[i] + ", ");
        }
        sb.append(arr[arr.length - 1] + "\n");
        return sb.toString();
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

    public static void printArray(float[][] d) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                System.out.print(d[i][j] + ", ");
            }
            System.out.println();
        }
    }

    public static void printArray(String[][] d) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                System.out.print(d[i][j] + ", ");
            }
            System.out.println();
        }
    }
}
