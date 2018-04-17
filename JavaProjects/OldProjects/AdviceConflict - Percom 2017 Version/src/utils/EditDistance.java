/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author mm5gg
 */
public class EditDistance {
    
     public static int getDistance(String s1, String s2) {
        int len1 = s1.length(), len2 = s2.length();

        int[][] grid = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            grid[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            grid[0][j] = j;
        }

        char c1, c2;
        int rep, ins, del;
        
        for (int i = 0; i < len1; i++) {
            c1 = s1.charAt(i);
            for (int j = 0; j < len2; j++) {
                c2 = s2.charAt(j);
                if (c1 == c2) {
                    grid[i + 1][j + 1] = grid[i][j];
                } else {
                    //rep = grid[i][j] + 1;
                    //ins = grid[i][j + 1] + 1;
                    //del = grid[i + 1][j] + 1;
                    grid[i + 1][j + 1] = min(grid[i][j] + 1, grid[i][j + 1] + 1, grid[i + 1][j] + 1);
                }
            }
        }

        return grid[len1][len2];
    }

    static int min(int a, int b, int c) {
        return a <= b ? (a <= c ? a : c) : (b <= c ? b : c);
    }
    
}
