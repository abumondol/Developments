package myutils;

import weka.classifiers.Evaluation;
import wekautils.MyWekaTest;

/**
 *
 * @author mm5gg
 */
public class PrintUtils {

    public static void arrayStat(String[][] data, String header) {
        if (data == null) {
            System.out.println(header + " Null Array");
            return;
        }
        int nrows = data.length;
        int ncols = data[0].length;
        System.out.println(header + " " + String.format("rows, cols: %d, %d", nrows, ncols));
    }

    public static void printArray(String[] data, String header, String delim) {
        if (data == null) {
            System.out.println(header + " Null Array");
            return;
        }
        
        if(delim==null)
            delim = ", ";
        if(header == null)
            header = "";

        int i, count = data.length;
        System.out.println(header + ", Total elements: " + count + "");
        for (i = 0; i < count; i++) {
            System.out.print(data[i] + delim);
        }
        
        System.out.println();
    }
    
    public static void printArray(String[][] data, String header, String delim) {
        if (data == null) {
            System.out.println(header + " Null Array");
            return;
        }
        
        if(delim==null)
            delim = ", ";
        if(header == null)
            header = "";

        int i, j, nrows = data.length, ncols;
        System.out.println(header + ", Row Count: " + nrows + "");
        for (i = 0; i < nrows; i++) {
            ncols= data[i].length;
            System.out.print(ncols+": ");
            for(j=0;j<ncols;j++){
                System.out.print(data[i][j] + delim);
            }
            System.out.println();
        }
        
        System.out.println();
    }
    
    
    public static void printWekaResult(String header, Evaluation eval){
        System.out.println(header);        
        System.out.println(eval.toSummaryString());
        
    }
}
