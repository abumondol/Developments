package wekaclassification;

import myutils.DataProcess;
import myutils.FileUtils;
import myutils.PrintUtils;
import weka.classifiers.Evaluation;
import wekautils.ArffUtil;
import wekautils.MyWeka;

public class Pamap2 {

    public static void classification(String filepath) throws Exception {
        boolean isHeaderAvailable = false;
        String[][] data = FileUtils.readCSV(filepath, true);
        int[] indicesAll = getAttributeIndicesAll();
        int[] indicesAccelGyro = getAttributeIndicesAccelGyro();
        String[][] subjectData, otherData;
        String subjectArff, otherArff;
        String[] classList;
        MyWeka myWeka1, myWeka2;
        Evaluation eval;

        int sub;
        for (sub = 1; sub <= 9; sub++) {
            subjectData = DataProcess.filterDataBySubject(data, isHeaderAvailable, sub, false); // excludeSubject = false;
            otherData = DataProcess.filterDataBySubject(data, isHeaderAvailable, sub, true); //excludeSubject = true;

            System.out.println("====================== Subject: " + sub);
            classList = ArffUtil.getClassList(data, isHeaderAvailable, data[0].length - 2); // classColumnIndex = data[0].length-2, the last column is for subject
            PrintUtils.printArray(classList, "Classes", ", ");
            //classList = null;

            subjectArff = ArffUtil.csvToArff(subjectData, indicesAccelGyro, isHeaderAvailable, classList); // classList = null
            otherArff = ArffUtil.csvToArff(otherData, indicesAccelGyro, isHeaderAvailable, classList); // classList = null            
            myWeka1 = new MyWeka();
            myWeka1.classify(subjectArff, otherArff);


            subjectArff = ArffUtil.csvToArff(subjectData, indicesAll, isHeaderAvailable, classList); // classList = null
            otherArff = ArffUtil.csvToArff(otherData, indicesAll, isHeaderAvailable, classList); // classList = null            
            myWeka2 = new MyWeka();
            myWeka2.classify(subjectArff, otherArff);

            System.out.println(getPct(myWeka1.eval.weightedPrecision()) + ", "
                    + getPct(myWeka1.eval.weightedRecall()) + ", "
                    + getPct(myWeka1.eval.weightedFMeasure()));
            
            System.out.println(getPct(myWeka2.eval.weightedPrecision()) + ", "
                    + getPct(myWeka2.eval.weightedRecall()) + ", "
                    + getPct(myWeka2.eval.weightedFMeasure()));

            
            for (int i = 0; i < classList.length; i++) {
                System.out.println(i + ": " 
                        + getPct(myWeka1.eval.precision(i)) + ", " 
                        + getPct(myWeka1.eval.recall(i)) + ", " 
                        + getPct(myWeka1.eval.fMeasure(i)));
                System.out.println(i + ": " 
                        + getPct(myWeka2.eval.precision(i)) + ", " 
                        + getPct(myWeka2.eval.recall(i)) + ", " 
                        + getPct(myWeka2.eval.fMeasure(i)));
                System.out.println();
            }

        }
    }

    public static int[] getAttributeIndicesAll() {
        int[] indices = new int[75];
        for (int i = 0; i < 75; i++) {
            indices[i] = i;
        }


        return indices;
    }

    public static int[] getAttributeIndicesAccelGyro() {
        int[] indices = new int[30];
        int i, j;
        for (i = 0; i < 30; i++) {
            indices[i] = i;
        }

//        for (i = 30, j = 75; i < 60; i++, j++) {
//            indices[i] = j;
//        }
//
//        for (i = 60, j = 150; i < 90; i++, j++) {
//            indices[i] = j;
//        }
        return indices;
    }

    public static double getPct(double val) {
        return Math.round(val * 10000) / 100.0;
    }
}
