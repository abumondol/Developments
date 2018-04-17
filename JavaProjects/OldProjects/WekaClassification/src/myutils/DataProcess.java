/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myutils;

import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class DataProcess {

    public static String[][] filterDataBySubject(String[][] srcData, boolean isHeaderAvailable, int subject_id, boolean excludeSubject) throws Exception {
        int i, j, sid, row_count_src, row_count_dest, col_count_src, col_count_dest, start_index = 0;

        row_count_src = srcData.length;
        col_count_src = srcData[0].length;
        ArrayList<String[]> list = new ArrayList<String[]>();

        if (isHeaderAvailable) {
            list.add(srcData[0]);
            start_index = 1;
        }

        for (i = start_index; i < row_count_src; i++) {
            sid = Integer.parseInt(srcData[i][col_count_src - 1]);
            if (excludeSubject && sid != subject_id || !excludeSubject && sid==subject_id) {
                list.add(srcData[i]);
            }
        }

        row_count_dest = list.size();
        col_count_dest = col_count_src - 1;
        String[][] destData = new String[row_count_dest][col_count_dest];
        String[] s;

        for (i = 0; i < row_count_dest; i++) {
            s = list.get(i);
            for (j = 0; j < col_count_dest; j++) {
                destData[i][j] = s[j];
            }
        }

        return destData;
    }
}
