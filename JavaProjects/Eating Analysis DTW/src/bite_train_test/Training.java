/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bite_train_test;

import constants_config.MyParameters;
import distance_finder.DTW;
import entities.Pattern;
import java.util.ArrayList;

/**
 *
 * @author mm5gg
 */
public class Training {
    
    // **************** label min points **********************
    public static int[] labelMinPoints(int[][] annots, int[] minIndices) {
        int[] labels = new int[minIndices.length];

        int ix, i, j, minDistanceIndex;
        int annotCount = annots.length;

        for (i = 0; i < minIndices.length; i++) {
            ix = minIndices[i];
            minDistanceIndex = 0;
            for (j = 1; j < annotCount; j++) {
                if (Math.abs(annots[j][0] - ix) < Math.abs(annots[minDistanceIndex][0] - ix)) {
                    minDistanceIndex = j;
                }
            }

            int minDist = (int) Math.abs(annots[minDistanceIndex][0] - ix);
            if (minDist <= MyParameters.MAX_ANNOT_DISTANCE) {
                labels[i] = (int) annots[minDistanceIndex][1];
            } else if (minDist <= MyParameters.EXCLUDE_ANNOT_DISTANCE) {
                labels[i] = -1;
            } else {
                labels[i] = 0;
            }
        }

        return labels;
    }

    // **************** Select Patterns **********************     
    public static ArrayList<Pattern> selectPatterns(ArrayList<Pattern> patList) {
        ArrayList<Pattern> selectedList = new ArrayList<Pattern>();
        int i, j, count = patList.size();
        float d;
        Pattern p, p1, p2;

        for (i = 0; i < count; i++) {
            p1 = patList.get(i);
            for (j = i + 1; j < count; j++) {
                p2 = patList.get(j);
                d = DTW.distanceUCR(p1.dataNormalized, p2.dataNormalized, MyParameters.DISTANCE_RADIUS);
                if (d <= MyParameters.DISTANCE_RADIUS) {
                    /*System.out.print(d + ", ");
                    if (j % 10 == 0) {
                        System.out.println();
                    }*/
                    p1.coveredList.add(p2);
                    p2.coveredList.add(p1);
                    p1.increaseCoverageCount(p2.label);
                    p2.increaseCoverageCount(p1.label);
                }
               
            }
            
        }
        
        if (patList.isEmpty()) {
            return patList;
        }

        while (!patList.isEmpty()) {
            count = patList.size();
            System.out.println("Patlist Size: " + patList.size() + ", SelectedListSize: " + selectedList.size());
            p = patList.get(0);
            for (i = 1; i < count; i++) {                
                if (p.totalCoverageCount() < patList.get(i).totalCoverageCount()) {
                    p = patList.get(i);
                }
            }

            count = p.coveredList.size();
            if (count == 0) {
                break;
            }

            selectedList.add(p);
            patList.remove(p);
            //System.out.print("Covered count:"+count+"; ");
            //PatternUtils.printPatternInfo(p);

            for (i = 0; i < count; i++) {
                patList.remove(p.coveredList.get(i));
            }

            p.coveredList = null;

        }

        return selectedList;
    }
    
}
