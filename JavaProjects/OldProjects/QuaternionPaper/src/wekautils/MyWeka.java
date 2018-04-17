package wekautils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;

import weka.core.Instances;



public class MyWeka {

    public Instances instances = null;
    BufferedReader reader;
    StringReader strReader;
    public RandomForest forest;
    public Evaluation eval;

    public void setData(String arrfData) throws Exception {
        if (instances != null) {
            instances.delete();
        }
        
        strReader = new StringReader(arrfData);
        instances = new Instances(strReader);
        //UtilFunctions.close(strReader);
        strReader.close();
    }

    public void buildModel(String arrfData) throws Exception {
        setData(arrfData);
        instances.setClassIndex(instances.numAttributes() - 1);

        //classifier = new J48();
        forest = new RandomForest();
        forest.setNumIterations(100);
        forest.setNumFeatures(instances.numAttributes()-1);        
        forest.buildClassifier(instances); // build classifier
    }
}
