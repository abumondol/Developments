package handwash;

import myutils.MyArrayUtils;
import myutils.MyFileUtils;
import myutils.Stat;

/**
 *
 * @author mm5gg
 */
public class HWResult {

    float[][][] prob;
    int[] gt;
    int[] res, resbow;
    String[] poses = {"hw", "pose"};
    String[] featureNames = {"fs", "bow"};
    String destFolder, filename, str;
    Stat stat;
    
    public static void resultFromEvlas(){
        
    }

    public void generate_results() throws Exception {
        StringBuilder sb = new StringBuilder();
        prob = new float[2][][];

        int dataset, hand, pose, subject, sub_count, feature_type, act_count;
        for (dataset = 1; dataset <= 2; dataset++) {
            sub_count = (dataset == 1) ? 9 : 7;
            for (hand = 1; hand <= 2; hand++) {
                for (pose = 0; pose < 2; pose++) {
                    for (subject = 1; subject <= sub_count; subject++) {
                        str = dataset + "," + hand + "," + pose + "," + subject + ",";
                        destFolder = (dataset == 1) ? "myresults/handwash/dataset_1/" : "myresults/handwash/dataset_2/";
                        destFolder += (hand == 1 ? "right/" : "left/");
                        destFolder += (pose == 1 ? "pose/" : "hw/");

                        filename = destFolder + "subject_" + subject + "/gt";
                        gt = MyFileUtils.readLabels(filename);

                        for (feature_type = 0; feature_type < 1; feature_type++) {
                            filename = destFolder + "subject_" + subject + "/" + featureNames[feature_type];
                            prob[feature_type] = MyArrayUtils.stringArrayToFloatArray(MyFileUtils.readCSV(filename, true));
                        }

                        //prob[1] = addMatrices(prob[0], prob[1]);
                        res = predictedLabels(prob[0], pose); //add 0 or 1
                        //resbow = predictedLabels(prob[1], pose); //add 0 or 1

                        if (pose == 0) {//hw or not                            
                            for (int act = 0; act <= 1; act++) {
                                stat = calculateStat(res, gt, act);
                                sb.append(str + "0," + act + "," + stat.TP + "," + stat.FP + "," + stat.TN + "," + stat.FN + "\n");
                                //stat = calculateStat(resbow, gt, act);
                                //sb.append(str + "1," + act + "," + stat.TP + "," + stat.FP + "," + stat.TN + "," + stat.FN + "\n");

                            }
                        } else {
                            act_count = (dataset == 1) ? 7 : 10;
                            for (int act = 1; act <= act_count; act++) {
                                stat = calculateStat(res, gt, act);
                                sb.append(str + "0," + act + "," + stat.TP + "," + stat.FP + "," + stat.TN + "," + stat.FN + "\n");
                                //stat = calculateStat(resbow, gt, act);
                                //sb.append(str + "1," + act + "," + stat.TP + "," + stat.FP + "," + stat.TN + "," + stat.FN + "\n");

                            }
                        }



                    }
                }
            }
        }
        
        MyFileUtils.writeToFile("myresults/allres.csv", sb.toString());
    }

    public float[][] addMatrices(float[][] a, float[][] b) {
        int nrows = a.length;
        int ncols = a[0].length;
        float[][] res = new float[nrows][ncols];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                res[i][j] = a[i][j] + b[i][j];
            }
        }
        return res;
    }

    public int[] predictedLabels(float[][] prob, int addWithLabel) {
        int nrows = prob.length;
        int ncols = prob[0].length;
        int[] res = new int[nrows];

        int maxIndex;
        for (int i = 0; i < nrows; i++) {
            maxIndex = 0;
            for (int j = 1; j < ncols; j++) {
                if (prob[i][j] > prob[i][maxIndex]) {
                    maxIndex = j;
                }
            }

            res[i] = maxIndex + addWithLabel;
        }
        return res;
    }

    public Stat calculateStat(int[] pred, int[] gt, int actLabel) {
        Stat stat = new Stat();
        int count = pred.length;
        int TP = 0, FP = 0, TN = 0, FN = 0;

        for (int i = 0; i < count; i++) {
            if (pred[i] == actLabel && gt[i] == actLabel) {
                TP++;
            } else if (pred[i] == actLabel && gt[i] != actLabel) {
                FP++;
            } else if (pred[i] != actLabel && gt[i] == actLabel) {
                FN++;
            } else {
                TN++;
            }

        }

        stat.TP = TP;
        stat.FP = FP;
        stat.TN = TN;
        stat.FN = FN;
        return stat;
    }
}
