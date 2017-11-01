package myutils;
//@author mm5gg

import java.util.ArrayList;

public class MergeSort {

    public static void merge(float[][] list, int column, int low, int middle, int high) {
        // Find sizes of two subarrays to be merged
        int n1 = middle - low + 1;
        int n2 = high - middle;

        /* Create temp arrays */
        float[][] L = new float[n1][];
        float[][] R = new float[n2][];

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i) {
            L[i] = list[low + i];
        }
        for (int j = 0; j < n2; ++j) {
            R[j] = list[middle + 1 + j];
        }

        /* Merge the temp arrays */
        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = low;
        while (i < n1 && j < n2) {
            if (L[i][column] <= R[j][column]) {
                list[k] = L[i];
                i++;
            } else {
                list[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            list[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            list[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    public static void sort(float[][] list, int column, int low, int high) {
        if (high - low <= 10) {
            doSelectionSort(list, column, low, high);
        } else if (low < high) {
            // Find the middle point
            int m = (low + high) / 2;

            // Sort first and second halves
            sort(list, column, low, m);
            sort(list, column, m + 1, high);

            // Merge the sorted halves
            merge(list, column, low, m, high);
        }
    }

    public static void doSelectionSort(float[][] list, int column, int low, int high) {

        int index;
        float[] temp;
        for (int i = low; i <= high; i++) {
            index = i;
            for (int j = i + 1; j <= high; j++) {
                if (list[j][column] < list[index][column]) {
                    index = j;
                }
            }

            temp = list[i];
            list[i] = list[index];
            list[index] = temp;
        }

    }

    public static void swapInArrayList(ArrayList<float[]> list, int i, int j) {
        float[] temp = list.remove(i);
        list.add(i, list.get(j));
        list.remove(j);
        list.add(j, temp);

    }

}
