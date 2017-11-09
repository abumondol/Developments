package entities;

import java.util.ArrayList;
import java.util.List;
import pattern.PatternUtils;

// @author mm5gg
public class TrainPattern {

    public Pattern pat;
    public List<TrainPattern> coveredList = new ArrayList<>();
    public List<Float> coveredDistances = new ArrayList<>();
    public int coveredCount = 0;

    public void addPattternCovered(TrainPattern tp, float d) {
        int binIndex = PatternUtils.distanceToBinIndex(d);
        //System.out.println("Distance: " + d + ", Bin Index: " + binIndex + ", Label: " + p.label);        
        pat.coverageCountBins[tp.pat.label][binIndex]++;

        float xDiff = Math.abs(pat.minPointXVal - tp.pat.minPointXVal);
        if (xDiff > pat.binXDiffMax[binIndex]) {
            pat.binXDiffMax[binIndex] = xDiff;
        }

        int i = 0, n = coveredList.size();
        boolean added = false;
        for (i = 0; i < n; i++) {
            if (d < coveredDistances.get(i).floatValue()) {
                coveredList.add(i, tp);
                coveredDistances.add(i, d);
                added = true;
                break;
            }
        }

        if (!added) {
            coveredList.add(tp);
            coveredDistances.add(d);
        }

    }

}
