/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myutils;

/**
 *
 * @author mm5gg
 */
public class IndexUtils {
    
    public static int[] generateIndices(int start, int end){
        int len = end - start +1;
        int[] indices = new int[len];
        int i,j;
        for(i=0, j = start; i<len; i++, j++)
            indices[i] = j;
        
        return indices;
    }
    
    public static int[] generateIndices(int[][] list){
        int total_count = 0;
        int i,j, k, start, len;
        for(i=0;i<list.length;i++){
            total_count += list[i][1] - list[i][0] + 1;  
        }
                
        int[] indices = new int[total_count];
        
        j = 0;
        for(i=0;i<list.length;i++){
            start = list[i][0];            
            for( k = list[i][0]; k<=list[i][1]; k++, j++)
                indices[j] = k;            
        }        
        return indices;
    }
    
    
    
}
