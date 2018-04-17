package wekaclassification;

import myutils.FileUtils;
import wekautils.ArffUtil;

public class WekaClassification {
    
    public static void main(String[] args) {
        try{
            
            //String filepath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\Data Processing and Analysis\\Public Data\\Pamap2 Dataset\\pamap2_features.csv";
            //String filepath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\Data Processing and Analysis\\Public Data\\Opportunity\\opp_features_right.csv";
            String filepath = "C:\\Users\\mm5gg\\Machine Learning\\Dropbox\\MyMatlab\\Data Processing and Analysis\\Public Data\\RealDisp\\realdisp_features_1.csv";
            //String[][] data = FileUtils.readCSV(filepath, true);
            //String d = ArffUtil.csvToArff(data, null, false, null);
            //FileUtils.writeToFile("temp.csv", d);
            //Pamap2.classification(pamap2_filepath);
            //Opportunity.classification(filepath);
            RealDisp.classification(filepath);
            
            
        }catch(Exception ex){
            ex.toString();
            ex.printStackTrace();
        }           
        
    }
    
}
