package wekautils;

import java.util.ArrayList;
import java.util.Arrays;
import myutils.MyArrayUtils;

/**
 *
 * @author mm5gg
 */
public class ArffUtil {

    /*public static double classify(String arffData, int option) throws Exception {
        StringReader strReader = new StringReader(arffData);
        Instances instances = new Instances(strReader);
        strReader.close();
        instances.setClassIndex(instances.numAttributes() - 1);

        Classifier classifier;
        if (option == 1) {
            classifier = new J48(); // Decision Tree classifier
        } else if (option == 2) {
            classifier = new RandomForest();
        } else if (option == 3) {
            classifier = new SMO();  //This is a SVM classifier
        } else {
            return -1;
        }

        classifier.buildClassifier(instances); // build classifier

        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(classifier, instances, 10, new Random(1), new Object[]{});

        return eval.pctCorrect();
    }*/

    public static String toArff(ArrayList<float[]> feature_data, ArrayList<Integer> labels) throws Exception {
        int total_rows = feature_data.size();
        int total_cols = feature_data.get(0).length;
        float[] f;
        int i,j;

        String[] classList = new String[1];
        classList[0] = ""+labels.get(0);        

        for (i = 1; i < total_rows; i++) {
            classList = addClass(classList, ""+labels.get(i));
        }
        Arrays.sort(classList);
        //MyArrayUtils.printArray(classList, "ClassList: ");
                

        StringBuilder sb = getArffHeader(total_cols, classList);

        for (i = 0; i < total_rows; i++) {
            f = feature_data.get(i);
            for (j = 0; j < total_cols; j++) {
                sb.append(f[j]);
                sb.append(",");
            }
            sb.append(labels.get(i).intValue());
            sb.append("\n");
        }

        return sb.toString();
    }
    
    public static String csvToArff(String[][] csvData, int[] featureIndices) throws Exception {
        int total_rows = csvData.length;
        int total_cols = csvData[0].length;
        int fCount = featureIndices.length;
        String[] attributeList = new String[fCount + 1];
        int i, j;
        for (i = 0; i < fCount; i++) {
            attributeList[i] = csvData[0][featureIndices[i]];
        }
        attributeList[i] = csvData[0][total_cols - 1];

        String[] classList = new String[1];
        classList[0] = csvData[1][total_cols - 1];

        for (i = 1; i < total_rows; i++) {
            classList = addClass(classList, csvData[i][total_cols - 1]);
        }

        StringBuilder sb = getArffHeader(attributeList, classList);

        for (i = 1; i < total_rows; i++) {
            for (j = 0; j < fCount; j++) {
                sb.append(csvData[i][featureIndices[j]]);
                sb.append(",");
            }
            sb.append(csvData[i][total_cols - 1]);
            sb.append("\n");
        }

        return sb.toString();
    }
    
    private static StringBuilder getArffHeader(int attributeCount, String[] classList) {
        StringBuilder s = new StringBuilder();
        s.append("@RELATION wada\n\n");

        int i;
        for (i = 1; i <= attributeCount; i++) {
            s.append("@ATTRIBUTE f");
            s.append(i);
            s.append(" numeric\n");
        }

        s.append("@ATTRIBUTE labels");
        
        s.append(" {");
        s.append(classList[0]);

        for (i = 1; i < classList.length; i++) {
            s.append(",");
            s.append(classList[i]);
        }
        s.append("}\n\n");
        s.append("@DATA\n");
        return s;
    }

    private static StringBuilder getArffHeader(String[] attributeList, String[] classList) {
        StringBuilder s = new StringBuilder();
        s.append("@RELATION wada\n\n");

        int i;
        for (i = 0; i < attributeList.length - 1; i++) {
            s.append("@ATTRIBUTE ");
            s.append(attributeList[i]);
            s.append(" numeric\n");
        }

        s.append("@ATTRIBUTE ");
        s.append(attributeList[i]);
        s.append(" {");
        s.append(classList[0]);

        for (i = 1; i < classList.length; i++) {
            s.append(",");
            s.append(classList[i]);
        }
        s.append("}\n\n");
        s.append("@DATA\n");
        return s;
    }

    private static String[] addClass(String[] classList, String className) {
        int len = classList.length;
        int i;
        for (i = 0; i < len; i++) {
            if (className.equals(classList[i])) {
                return classList;
            }
        }

        String[] newList = new String[len + 1];
        for (i = 0; i < len; i++) {
            newList[i] = classList[i];
        }
        newList[i] = className;

        return newList;
    }
}
