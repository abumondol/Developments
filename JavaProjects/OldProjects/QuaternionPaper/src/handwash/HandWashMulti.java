package handwash;

import java.util.ArrayList;
import myutils.MyArrayUtils;
import myutils.MyFileUtils;
import quaternionpaper.MyConstants;
import weka.classifiers.Evaluation;
import wekautils.ArffUtil;
import wekautils.MyWekaTest;

/**
 *
 * @author mm5gg
 */
public class HandWashMulti {

    float[][][][] data;
    int subcount, actcount;
    String destFolder;
    //String[] featureNames = {"fs", "bop", "bow", "bowbin"};
    String[] featureNames = {"fs", "bow"};

    public HandWashMulti() throws Exception {
        data = new float[2][2][][];
        data[0][0] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/fs1.csv", true));
        data[0][1] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bow1.csv", true));
        //data[0][2] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bop1.csv", true));
        //data[0][3] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bowbin1.csv", true));
        System.out.println("Dataset 1:");
        System.out.println("FS1: " + data[0][0].length + ", " + data[0][0][0].length);
        System.out.println("BOW1: " + data[0][1].length + ", " + data[0][1][0].length);
        //System.out.println("BOP1: " + data[0][2].length + ", " + data[0][2][0].length);
        //System.out.println("BOWBIN1: " + data[0][3].length + ", " + data[0][3][0].length);

        data[1][0] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/fs2.csv", true));
        data[1][1] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bow2.csv", true));
        //data[1][2] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bow2.csv", true));
        //data[1][3] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/bowbin2.csv", true));
        System.out.println("\nDataset 2:");
        System.out.println("FS2: " + data[1][0].length + ", " + data[1][0][0].length);
        System.out.println("BOW2: " + data[1][1].length + ", " + data[1][1][0].length);
        //System.out.println("BOP2: " + data[1][2].length + ", " + data[1][2][0].length);
        //System.out.println("BOWBIN2: " + data[1][3].length + ", " + data[1][3][0].length);

    }

    public void classify_all() throws Exception {
        int dataset, hand, feature_type;
        for (dataset = 1; dataset <= 2; dataset++) {
            for (hand = 1; hand <= 2; hand++) {
                for (feature_type = 0; feature_type < 1; feature_type++) {
                    classify(dataset, hand, feature_type, false);
                    classify(dataset, hand, feature_type, true);
                }

            }
        }
    }

    public void classify(int dataset, int hand, int feature_type, boolean pose) throws Exception {
        destFolder = (dataset == 1) ? "myresults/handwash/dataset_1/" : "myresults/handwash/dataset_2/";
        destFolder += (hand == 1 ? "right/" : "left/");
        destFolder += (pose ? "pose/" : "hw/");

        if (dataset == 1) {
            subcount = 9;
            actcount = 9;

        } else {
            subcount = 7;
            actcount = 12;
        }

        int i, j, k, classifier_type = MyConstants.RANDOM_FOREST;
        float[][] train, test;
        DataHw dataHw;
        MyWekaTest myweka;
        Evaluation eval;
        String arffTrain, arffTest;
        double[][] res;

        for (i = 1; i <= subcount; i++) {
            System.out.println("Dataset: " + dataset + ", Hand: " + hand + ", Pose: " + pose + ", Subject: " + i+", Feature: "+featureNames[feature_type]);
            dataHw = DataProcessHW.getTrainTest(data[dataset - 1][feature_type], i, hand, actcount, pose);
            if(feature_type==0){
                //System.out.println("Saving ground truth");
                writeGroundTruth(dataHw.testLabels, destFolder + "subject_"+i + "/gt");
            }
            
            arffTrain = ArffUtil.toArff(dataHw.trainFeatures, dataHw.trainLabels);
            arffTest = ArffUtil.toArff(dataHw.testFeatures, dataHw.testLabels);
            myweka = new MyWekaTest();
            if (feature_type > 0) {
                classifier_type = MyConstants.SVM;
            }
            res = myweka.testWithProbDist(arffTrain, arffTest, classifier_type);
            MyFileUtils.writeToCSVFile(destFolder + "subject_" + i + "/" + featureNames[feature_type], res, null);
        }
    }

    public void writeGroundTruth(ArrayList<Integer> labels, String fileName) throws Exception {
        StringBuilder sb = new StringBuilder();
        int size = labels.size();
        for (int i = 0; i < size; i++) {
            sb.append(labels.get(i).intValue());
            sb.append("\n");
        }
        
        MyFileUtils.writeToFile(fileName, sb.toString());
    }
    
}
