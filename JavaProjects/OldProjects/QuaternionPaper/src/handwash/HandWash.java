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
public class HandWash {

    float[][][] data;
    int subcount, actcount;
    String destFolder;
    StringBuilder sb = new StringBuilder();

    public HandWash() throws Exception {
        data = new float[2][][];
        data[0] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/vfs1.csv", true));

        System.out.println("Dataset 1:");
        System.out.println("FS1: " + data[0].length + ", " + data[0][0].length);

        data[1] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV("mydata/handwash/vfs2.csv", true));
        System.out.println("\nDataset 2:");
        System.out.println("FS2: " + data[1].length + ", " + data[1][0].length);
    }

    public void classify_all() throws Exception {
        int dataset, hand, feature_type;
        for (dataset = 1; dataset <= 2; dataset++) {
            for (hand = 1; hand <= 2; hand++) {
                classify(dataset, hand, false);
                classify(dataset, hand, true);
            }
        }
        
        //MyFileUtils.writeToFile("myresults/resvfs.csv", sb.toString());
    }

    public void classify(int dataset, int hand, boolean pose) throws Exception {
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
            
            dataHw = DataProcessHW.getTrainTest(data[dataset-1], i, hand, actcount, pose);            

            arffTrain = ArffUtil.toArff(dataHw.trainFeatures, dataHw.trainLabels);
            arffTest = ArffUtil.toArff(dataHw.testFeatures, dataHw.testLabels);
            myweka = new MyWekaTest();
            myweka.test(arffTrain, arffTest);
            eval = myweka.eval;
                        
            
            MyFileUtils.serializeFile(eval, "myresults/handwash/evals/ds-"+dataset+"-hand-"+hand+"-pose-"+(pose?1:0)+"-subject-"+i+".eval");
            
            sb.append(dataset + "," + hand + "," + (pose?1:0) + "," + i+",");
            String str;
            if(!pose)
                str = eval.precision(1)+","+eval.recall(1)+","+eval.fMeasure(1)+"\n";
            else
                str = eval.weightedPrecision()+","+eval.weightedRecall()+","+eval.weightedFMeasure()+"\n";
            
            sb.append(str);
            System.out.println("Dataset: " + dataset + ", Hand: " + hand + ", Pose: " + pose + ", Subject: " + i +", Results: "+str);
            
        }
    }

    
}
