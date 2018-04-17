
import weka.classifiers.Evaluation;


/**
 *
 * @author mm5gg
 */
public class WekaApp {

    public static void main(String[] args) {
        try {
            String[][] csvData = MyWekaUtils.readCSV("features.csv");           
            int[] features = {0, 1, 2, 3, 4, 5};
            String arffData = MyWekaUtils.csvToArff(csvData, features);
            System.out.println(arffData);
            
            double accuracy = MyWekaUtils.classify(arffData, 1);
            System.out.println(accuracy);
                        
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }


    }
}
