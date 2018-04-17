package wekaclassification;

import myutils.DataProcess;
import myutils.FileUtils;
import myutils.IndexUtils;
import myutils.PrintUtils;
import myutils.ResultUtils;
import weka.classifiers.Evaluation;
import static wekaclassification.Opportunity.getAttributeIndicesAccelGyro;
import static wekaclassification.Opportunity.getAttributeIndicesAll;
import static wekaclassification.Pamap2.getAttributeIndicesAccelGyro;
import static wekaclassification.Pamap2.getAttributeIndicesAll;
import static wekaclassification.Pamap2.getPct;
import wekautils.ArffUtil;
import wekautils.MyWeka;

public class RealDisp {

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
        System.out.println("Data size: " +data.length+", "+data[0].length);
        for (sub = 1; sub <= 17; sub++) {
            subjectData = DataProcess.filterDataBySubject(data, isHeaderAvailable, sub, false); // excludeSubject = false;
            otherData = DataProcess.filterDataBySubject(data, isHeaderAvailable, sub, true); //excludeSubject = true;

            //System.out.println("====================== Subject " + sub+": "+subjectData.length+", "+otherData.length);
            System.out.println("====================== Subject " + sub+": "+subjectData.length);
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

            System.out.println(ResultUtils.getPct(myWeka1.eval.weightedPrecision()) + ", "
                    + ResultUtils.getPct(myWeka1.eval.weightedRecall()) + ", "
                    + ResultUtils.getPct(myWeka1.eval.weightedFMeasure()));
            
            System.out.println(ResultUtils.getPct(myWeka2.eval.weightedPrecision()) + ", "
                    + ResultUtils.getPct(myWeka2.eval.weightedRecall()) + ", "
                    + ResultUtils.getPct(myWeka2.eval.weightedFMeasure()));

            
            for (int i = 0; i < classList.length; i++) {
                System.out.println(i + ": " 
                        + ResultUtils.getPct(myWeka1.eval.precision(i)) + ", " 
                        + ResultUtils.getPct(myWeka1.eval.recall(i)) + ", " 
                        + ResultUtils.getPct(myWeka1.eval.fMeasure(i)));
                System.out.println(i + ": " 
                        + ResultUtils.getPct(myWeka2.eval.precision(i)) + ", " 
                        + ResultUtils.getPct(myWeka2.eval.recall(i)) + ", " 
                        + ResultUtils.getPct(myWeka2.eval.fMeasure(i)));
                System.out.println();
            }

        }
    }

    public static int[] getAttributeIndicesAll() {
        return IndexUtils.generateIndices(0, 44);
        
    }

    public static int[] getAttributeIndicesAccelGyro() {
        return IndexUtils.generateIndices(0, 29);
    }    
    
}
