package myutils;

/**
 *
 * @author mm5gg
 */
public class MyPrintUtils {
    
    public static void print(int[] a, String prefix){
        int i;
        System.out.print(prefix);
        for(i=0;i<a.length-1;i++){
            System.out.print(a[i]+", ");
        }
        
        System.out.println(a[i]);
    }
    
    
    
}
