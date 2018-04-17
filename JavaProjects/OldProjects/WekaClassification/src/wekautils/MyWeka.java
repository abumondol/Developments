package wekautils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class MyWeka {

    public Instances instances_train = null;
    public Instances instances_test = null;    
    BufferedReader reader;
    StringReader strReader;
    public Classifier classifier;
    public Evaluation eval;

    public void setData(String arffDataTrain, String arffDataTest) throws Exception {
        if (instances_train != null) {
            instances_train.delete();
        }
        strReader = new StringReader(arffDataTrain);
        instances_train = new Instances(strReader);        
        strReader.close();
        
        if(arffDataTest!=null){
            if (instances_test != null) {
                instances_test.delete();
            }
            strReader = new StringReader(arffDataTest);
            instances_test = new Instances(strReader);        
            strReader.close();
        }
    }

    public void classify(String arffDataTrain, String arffDataTest) throws Exception {
        setData(arffDataTrain, arffDataTest);
        instances_train.setClassIndex(instances_train.numAttributes() - 1);
        instances_test.setClassIndex(instances_test.numAttributes() - 1);

       //classifier = new J48();
        classifier = new RandomForest();  
        //classifier = new SMO();  //This is a SVM classifier 
        classifier.buildClassifier(instances_train); // build classifier
        eval = new Evaluation(instances_train);
        if(arffDataTest == null)
            eval.crossValidateModel(classifier, instances_train, 10, new Random(1));
        else
            eval.evaluateModel(classifier, instances_test);

    }
    
}
