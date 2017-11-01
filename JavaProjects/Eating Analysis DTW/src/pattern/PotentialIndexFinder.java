package pattern;

import constants_config.MyParameters;
import java.util.ArrayList;
import myutils.MyArrayUtils;
import myutils.MyMathUtils;

public class PotentialIndexFinder {

    public static int[] findMinPointIndicesFiltered(float[][] accelData) {
        int[] minIndices = PotentialIndexFinder.findMinPointIndices(accelData);
        //MyArrayUtils.printArray(minIndices);
        if (minIndices == null) {
            return null;
        }
        minIndices = PotentialIndexFinder.filterMinPoints(accelData, minIndices);
        //System.out.println(", after filter: " + minIndices.length);
        return minIndices;
    }

    public static int[] filterMinPoints(float[][] accelData, int[] minIndices) {
        ArrayList<Integer> selectedIndices = new ArrayList<>();
        int i, ix;
        float std;
        for (i = 0; i < minIndices.length; i++) {
            ix = minIndices[i];
            std = MyMathUtils.totalStdev(accelData, ix - MyParameters.LEFT_LENGTH, ix + MyParameters.RIGHT_LENGTH);

            if (std > MyParameters.MIN_STDEV) {
                selectedIndices.add(ix);
            }
        }

        if (selectedIndices.isEmpty()) {
            return null;
        }

        return MyArrayUtils.intListToArray(selectedIndices);
    }

    public static int[] findMinPointIndices(float[][] accelData) {
        float[] x = accelData[0];
        ArrayList<Integer> minIndices = new ArrayList<>();
        int stepLength = MyParameters.MIN_BITE_INTERVAL / 2;

        int i, ix, count, ixLeft, ixRight;
        for (i = 0; i <= x.length - stepLength; i += stepLength) {
            ix = findMinIndex(x, i, i + stepLength - 1);
            minIndices.add(ix);
        }

        count = minIndices.size();
        ArrayList<Integer> selectedIndices = new ArrayList<>();
        for (i = 1; i < count - 1; i++) {
            ix = minIndices.get(i);
            ixLeft = minIndices.get(i - 1);
            ixRight = minIndices.get(i + 1);
            if (x[ix] <= MyParameters.XTH && x[ix] <= x[ixLeft] && x[ix] < x[ixRight] && ix >= MyParameters.LEFT_LENGTH && ix < x.length - MyParameters.RIGHT_LENGTH) {
                selectedIndices.add(minIndices.get(i));
            }
        }

        //System.out.print("Sample Count: " + x.length + " Min Idices Sizes (Initial, by minx, by interval): " + minIndices.size() + "," + selectedIndices.size());
        minIndices = selectedIndices;

        while (true) {
            count = minIndices.size();
            selectedIndices = new ArrayList<>();

            ix = minIndices.get(0);
            ixRight = minIndices.get(1);
            if (ixRight - ix > MyParameters.MIN_BITE_INTERVAL || x[ix] < x[ixRight]) {
                selectedIndices.add(minIndices.get(0));
            }

            for (i = 1; i < count - 1; i++) {
                ix = minIndices.get(i);
                ixLeft = minIndices.get(i - 1);
                ixRight = minIndices.get(i + 1);

                if (ix - ixLeft > MyParameters.MIN_BITE_INTERVAL && ixRight - ix > MyParameters.MIN_BITE_INTERVAL || x[ix] <= x[ixLeft] && x[ix] < x[ixRight]) {
                    selectedIndices.add(minIndices.get(i));
                }

            }

            ix = minIndices.get(count - 1);
            ixLeft = minIndices.get(count - 2);
            if (ix - ixLeft > MyParameters.MIN_BITE_INTERVAL || x[ix] <= x[ixLeft]) {
                selectedIndices.add(minIndices.get(count - 1));
            }

            if (minIndices.size() == selectedIndices.size()) {
                break;
            }

            minIndices = selectedIndices;

        }

        //System.out.println(", " + minIndices.size());

        if (minIndices.size() == 0) {
            return null;
        }

        return MyArrayUtils.intListToArray(minIndices);

    }

    public static int findMinIndex(float[] data, int startIndex, int endIndex) {
        int minIndex = startIndex;
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (data[i] < data[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

}
